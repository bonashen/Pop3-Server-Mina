package com.bona.server.pop3;

import com.bona.server.pop3.api.AuthenticationHandler;
import com.bona.server.pop3.api.RequestFilter;
import com.bona.server.pop3.core.POP3ConnectionHandler;
import com.bona.server.pop3.core.factory.StorageFactory;
import com.bona.server.pop3.core.command.CommandHandler;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by bona on 2015/10/12.
 */
public class POP3Server {
    private static final Logger LOG = LoggerFactory.getLogger(POP3Server.class);

    private InetAddress bindAddress = null;

    private CommandHandler commandHandler;
    private POP3ConnectionHandler handler;

    private SocketAcceptor acceptor;
    private ExecutorService executor;

    private boolean running = false;
    private boolean shutdowned = false;

    /**
     * The server configuration.
     */
    private POP3ServerConfig config = POP3ServerConfig.buildDefault();

    public POP3Server()
    {
        initInstance();
    }

    public POP3Server(POP3ServerConfig cfg){
        config = cfg;
        initInstance();
    }

    public POP3Server(AuthenticationHandler handler){
        this();
        config.setAuthenticationHandler(handler);
    }
    public POP3Server(AuthenticationHandler handler,StorageFactory storageFactory){
        this();
        config.setAuthenticationHandler(handler);
        config.setStorageFactory(storageFactory);
    }
    private void initInstance()
    {
        this.commandHandler = new CommandHandler(getConfig());
        initService();
    }

    /**
     * Initializes the runtime service.
     */
    private void initService()
    {
        try
        {
            IoBuffer.setUseDirectBuffer(false);
            acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);


            DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();

            if (LOG.isTraceEnabled())
                chain.addLast("logger", new LoggingFilter());

            chain.addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));

            executor = Executors.newCachedThreadPool(new ThreadFactory() {
                int sequence;

                public Thread newThread(Runnable r) {
                    sequence += 1;
                    return new Thread(r, "BonaPOP3 Thread " + sequence);
                }
            });

            chain.addLast("threadPool", new ExecutorFilter(executor));

            acceptor.getSessionConfig().setReadBufferSize( 1024*1024 );
            acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 10 );
            acceptor.getSessionConfig().setReuseAddress(true);

            handler = new POP3ConnectionHandler(getConfig(), getCommandHandler());
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Call this method to get things rolling after instantiating the
     * SMTPServer.
     */
    public synchronized void start()
    {
        if (running)
        {
            LOG.info("POP3 server is already started.");
            return;
        }

        if (shutdowned)
            throw new RuntimeException("Error: server has been shutdown previously");


        InetSocketAddress isa;

        if (this.bindAddress == null)
        {
            isa = new InetSocketAddress(getPort());
        }
        else
        {
            isa = new InetSocketAddress(this.bindAddress, getPort());
        }

        acceptor.setBacklog(config.getBacklog());
        acceptor.setHandler(handler);

        try
        {
            acceptor.bind(isa);
            running = true;
            LOG.info("POP3 server started ...");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stops the server by unbinding server socket. To really clean
     * things out, one must call {@link #shutdown()}.
     */
    public synchronized void stop()
    {
        try
        {
            try {
                acceptor.unbind();
            } catch (Exception e) { e.printStackTrace(); }

            LOG.info("POP3 server stopped.");
        }
        finally
        {
            running = false;
        }
    }

    /**
     * Shut things down gracefully. Please pay attention to the fact
     * that a shutdown implies that the server would fail to restart
     * because som internal resources have been freed.
     *
     * You can directly call shutdown() if you do not intend to restart
     * it later. Calling start() after shutdown() will throw a
     * {@link RuntimeException}.
     */
    public synchronized void shutdown()
    {
        try
        {
            LOG.info("POP3 server shutting down...");
            if (isRunning())
                stop();

            try {
                executor.shutdown();
            } catch (Exception e) { e.printStackTrace(); }

            shutdowned = true;
            LOG.info("POP3 server shutdown complete.");
        }
        finally
        {
            running = false;
        }
    }

    /**
     * Is the server running after start() has been called?
     */
    public synchronized boolean isRunning()
    {
        return this.running;
    }

    /**
     * Returns the bind address. Null means all interfaces.
     */
    public InetAddress getBindAddress()
    {
        return this.bindAddress;
    }

    /**
     * Sets the bind address. Null means all interfaces.
     */
    public void setBindAddress(InetAddress bindAddress)
    {
        this.bindAddress = bindAddress;
    }

    /**
     * Returns the port the server is running on.
     */
    public int getPort()
    {
        return this.config.getPort();
    }

    /**
     * Sets the port the server will run on.
     */
    public void setPort(int port)
    {
        config.setPort(port);
    }

    public void setStorageFactory(StorageFactory factory) {
        config.setStorageFactory(factory);
    }

    public StorageFactory getStorageFactory() {
        return config.getStorageFactory();
    }

    public Collection<RequestFilter> getFilters() {
        return config.getFilters();
    }

    /**
     * The CommandHandler manages handling the POP3 commands
     * such as USER,PASS,CAPA,DELE,NOOP,QUIT..., etc.
     *
     * @return An instance of CommandHandler
     */
    public CommandHandler getCommandHandler()
    {
        return this.commandHandler;
    }

    /**
     * Returns the server configuration.
     */
    public POP3ServerConfig getConfig()
    {
        return config;
    }
}
