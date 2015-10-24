package com.bona.server.pop3.core;

import com.bona.server.pop3.api.SessionContext;
import com.bona.server.pop3.api.Storage;
import com.bona.server.pop3.util.Constants;
import com.bona.server.pop3.POP3ServerConfig;
import com.bona.server.pop3.api.AuthenticationHandler;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bona on 2015/10/12.
 */
public class POP3Context implements SessionContext {
    private final static Logger LOG = LoggerFactory.getLogger(POP3Context.class);

    private final static String POP3_CONTEXT = POP3Context.class.getName() + ".ctx";
    private static final String USER_NAME = AuthenticationHandler.class.getName() + ".UserName";
    private static final String AUTH_VALUE = AuthenticationHandler.class.getName() + ".Authed";
    private final Storage storage;

    private IoSession session;
    private final POP3ServerConfig config;
    private final Map<String, Object> properties = new ConcurrentHashMap<String, Object>();


    public POP3Context(POP3ServerConfig cfg, IoSession session) {
        this.session = session;
        this.config = cfg;
        session.setAttribute(POP3_CONTEXT, this);
        this.storage = cfg.getStorageFactory().createStorage();
    }

    public static final POP3Context getContextFromSession(IoSession session) {
        return (POP3Context) session.getAttribute(POP3_CONTEXT);
    }

    public POP3ServerConfig getConfig() {
        return config;
    }

    public Response getResponse() {
        return response;
    }

    public Object getAttribute(String key) {
        return properties.get(key);
    }

    public Object getAttribute(String key, Object def) {
        return properties.getOrDefault(key, def);
    }

    public void setAttribute(String key, Object value) {
        properties.put(key, value);
    }

    public String getUserName() {
        return (String) getAttribute(USER_NAME);
    }

    public void setUserName(String userName) {
        setAttribute(USER_NAME, userName);
    }

    public boolean hasUser() {
        return getAttribute(USER_NAME) != null;
    }

    public boolean isAuthorized() {
        Object value = getAttribute(AUTH_VALUE);
        return value != null && ((Boolean) value == true);
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return session.getRemoteAddress();
    }

    public void authorize() {
        setAttribute(AUTH_VALUE, true);
        //init user storage.
        this.storage.initStorage(this);
        LOG.debug("User {} has authorized!", getUserName());
    }

    public void authorize(boolean value) {
        if (value) authorize();
        else
            removeAttribute(AUTH_VALUE);
    }

    private void removeAttribute(String authValue) {
        properties.remove(authValue);
    }

    public Storage getStorage() {
        return storage;
    }

    public IoSession getSession() {
        return session;
    }

    private final Response response = new Response() {
        private final String lineSeparator = "\r";

        public void sendMessage(String message) {
            session.write(message + lineSeparator);
        }

        public void sendOkMessage(String message) {
            sendMessage(Constants.POP_OK + message);
        }

        public void sendErrMessage(String message) {
            sendMessage(Constants.POP_ERR + message);
        }

        public void sendMessage(InputStream data) {
            session.write(data);
        }

        public void close() {
            session.close(false);
        }

    };
}
