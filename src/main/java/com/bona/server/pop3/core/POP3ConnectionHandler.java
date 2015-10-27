package com.bona.server.pop3.core;

import com.bona.server.pop3.util.Constants;
import com.bona.server.pop3.POP3ServerConfig;
import com.bona.server.pop3.core.command.CommandHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bona on 2015/10/12.
 */
public class POP3ConnectionHandler extends IoHandlerAdapter {
    private final static Logger LOG = LoggerFactory.getLogger(POP3ConnectionHandler.class);

    private final CommandHandler handler;
    private final POP3ServerConfig config;
    /**
     * A thread safe variable that represents the number
     * of active connections.
     */
    private AtomicInteger numberOfConnections = new AtomicInteger(0);


    public POP3ConnectionHandler(POP3ServerConfig config, CommandHandler commandHandler) {
        this.config = config;
        this.handler = commandHandler;
    }

    /**
     * Are we over the maximum amount of connections ?
     */
    private boolean hasTooManyConnections() {
        return (config.getMaxConnections() > -1 &&
                getNumberOfConnections() >= config.getMaxConnections());
    }

    /**
     * Update the number of active connections.
     */
    private void updateNumberOfConnections(int delta) {
        int count = numberOfConnections.addAndGet(delta);
        LOG.debug("Active connections count = {}", count);
    }

    /**
     * @return The number of open connections
     */
    public int getNumberOfConnections() {
        return numberOfConnections.get();
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        updateNumberOfConnections(+1);
        if (session.getTransportMetadata().getSessionConfigType() == SocketSessionConfig.class) {
            ((SocketSessionConfig) session.getConfig()).setReceiveBufferSize(config.getReceiveBufferSize());
            ((SocketSessionConfig) session.getConfig()).setSendBufferSize(64);
        }

        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, config.getConnectionTimeout() / 1000);

        // We're going to use SSL negotiation notification.
        session.setAttribute(SslFilter.USE_NOTIFICATION);

        new POP3Context(this.config, session);

        // Init protocol internals
        LOG.debug("POP3 connection count: {}", getNumberOfConnections());
        if (hasTooManyConnections()) {
            LOG.debug("Too many connections to the POP3 server !");
            sendResponse(session, Constants.POP_ERR + "Too many connections.");
        }

    }

    private static void sendResponse(IoSession session, String message) {
        POP3Context.getContextFromSession(session).getResponse().sendMessage(message);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        sendResponse(session, Constants.POP_WELCOME);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        LOG.debug("IDLE" + session.getIdleCount(status));
        if (status == IdleStatus.BOTH_IDLE)
            try {
                sendResponse(session, Constants.POP_ERR + "Timeout waiting for data from client.");
            } finally {
                session.close(false);
            }
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        if (message == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("no more lines from client");
                return;
            }
        }

        if (message instanceof SslFilter.SslFilterMessage) {
            if (LOG.isDebugEnabled())
                LOG.debug("SSL FILTER message -> " + message);
            return;
        }
        if (LOG.isDebugEnabled())
            LOG.debug("session id:{}  =>C:{}", session.getId(), message);

        if (message instanceof String) {
            if (!handler.dispatchCommand(session, (String) message)) {
                session.close(false);
            }
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        updateNumberOfConnections(-1);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        LOG.debug("session id:{}  =>S:{}", session.getId(), message);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        sendResponse(session, Constants.POP_ERR + cause.getMessage());
        session.close(false);
        LOG.error("found error", cause);

    }
}
