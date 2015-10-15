package com.bona.server.pop3;

import com.bona.server.pop3.api.RequestFilter;
import com.bona.server.pop3.api.Handler.DefaultAuthHandler;
import com.bona.server.pop3.api.Storage;
import com.bona.server.pop3.api.AuthenticationHandler;
import com.bona.server.pop3.core.factory.POP3ProtocolFactory;
import com.bona.server.pop3.core.factory.POP3StorageFactory;
import com.bona.server.pop3.core.factory.POP3Factory;
import com.bona.server.pop3.core.factory.StorageFactory;
import com.bona.server.pop3.core.filter.AuthFilter;
import com.bona.server.pop3.core.storage.DefaultStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by bona on 2015/10/12.
 */
public class POP3ServerConfig {
    private final static Logger LOG = LoggerFactory.getLogger(POP3ServerConfig.class);
    private int Port = 110;
    private AuthenticationHandler authenticationHandler;
    private Storage storage;
    private final Map<String, RequestFilter> filterMap = new HashMap<String, RequestFilter>();

    /**
     * pop3 protocol pop3Factory
     */
    private POP3Factory pop3Factory = POP3ProtocolFactory.getInstance();

    private StorageFactory storageFactory = POP3StorageFactory.getInstance();

    /**
     * The maximal number of recipients that this server accepts
     * per message delivery request.
     * Default value is set to 1000.
     */
    private int maxRecipients = 1000;

    /**
     * Tells the server if it can announce it's support of TLS.
     * This allow to disable it for clients who doesn't support
     * enough cipher suites andthat won't fallback to unsecured
     * connections.
     * Defaults to true.
     */
    private boolean tLSSupported = true;

    /**
     * The connection backlog. Defaults to 5000.
     */
    private int backlog = 5000;

    /**
     * The socket receive buffer size. Defaults to 128.
     */
    private int receiveBufferSize = 128;

    /**
     * Set a hard limit on the maximum number of connections this server will accept
     * once we reach this limit, the server will gracefully reject new connections.
     * Default is 1000.
     */
    private int maxConnections = 1000;

    /**
     * The timeout for waiting for data on a connection is one minute:
     * 1000 * 60.
     */
    private int connectionTimeout = 1000 * 60;

    private POP3ServerConfig() {

    }

    public int getMaxRecipients() {
        return maxRecipients;
    }

    public void setMaxRecipients(int maxRecipients) {
        this.maxRecipients = maxRecipients;
    }

    public boolean istLSSupported() {
        return tLSSupported;
    }

    public void settLSSupported(boolean tLSSupported) {
        this.tLSSupported = tLSSupported;
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public static Builder builder() {
        return new Builder(new POP3ServerConfig());
    }

    public Collection<RequestFilter> getFilters() {
        return filterMap.values();
    }

    public static POP3ServerConfig buildDefault() {
        LOG.warn("build POP3ServerConfig default.");
        return builder().setPort(110).
                setStorage(new DefaultStorage()).
                setAuthHandler(new DefaultAuthHandler()).
                addFilter("auth", new AuthFilter()).build();
    }

    public int getPort() {
        return Port;
    }

    public void setPort(int port) {
        Port = port;
    }

    public StorageFactory getStorageFactory() {
        return storageFactory;
    }

    public void setStorageFactory(StorageFactory storageFactory) {
        this.storageFactory = storageFactory;
    }

    public void setPOP3Factory(POP3Factory pop3Factory) {
        this.pop3Factory = pop3Factory;
    }


    public AuthenticationHandler getAuthenticationHandler() {
        return authenticationHandler;
    }

    public void setAuthenticationHandler(AuthenticationHandler authenticationHandler) {
        this.authenticationHandler = authenticationHandler;
    }


    public POP3Factory getPOP3Factory() {
        return pop3Factory;
    }

    public Map<String, RequestFilter> getFilterMap() {
        return filterMap;
    }

    public void addFilter(String name, RequestFilter filter) {

        if (filterMap.containsKey(name))removeFilter(name);
        LOG.debug("add {} filter.", name);
        filterMap.put(name, filter);
    }

    public void removeFilter(String name) {
        LOG.debug("remove {} filter.", name);
        filterMap.remove(name);
    }

    public void removeFilter(RequestFilter filter) {
        if (filterMap.containsValue(filter)) {
            filterMap.values().remove(filter);
        }
    }

    public static class Builder {
        private POP3ServerConfig config;

        private Builder(POP3ServerConfig POP3ServerConfig) {
            config = POP3ServerConfig;
        }

        public Builder setPort(int port) {
            config.Port = port;
            return this;
        }

        public Builder setAuthHandler(AuthenticationHandler authenticationHandler) {
            config.setAuthenticationHandler(authenticationHandler);
            return this;
        }

        public Builder setStorage(Storage storage) {
            config.storage = storage;
            return this;
        }

        public Builder addFilter(String name, RequestFilter filter) {
            config.addFilter(name, filter);
            return this;
        }

        public Builder removeFilter(RequestFilter filter) {
            config.removeFilter(filter);
            return this;
        }

        public Builder removeFilter(String name) {
            config.removeFilter(name);
            return this;
        }

        public POP3ServerConfig build() {
            if (config.getStorageFactory() == null) {
                LOG.warn("not found storage,will set {}.", DefaultStorage.class.getName());
                config.setStorageFactory(POP3StorageFactory.getInstance());
            }
            if (config.getAuthenticationHandler() == null) {
                LOG.warn("not found authentication handler,will set {}.", DefaultAuthHandler.class.getName());
                config.setAuthenticationHandler(new DefaultAuthHandler());
            }
            boolean authFilterFound=false;
            for(RequestFilter filter:config.getFilters()){
                if(filter instanceof AuthFilter){
                    break;
                }
            }
            if(authFilterFound)LOG.warn("not found authentication filter. POP3 Server maybe not security!");

            return this.config;
        }

        public Builder setMaxRecipients(int maxRecipients) {
            config.setMaxRecipients(maxRecipients);
            return this;
        }

        public Builder settLSSupported(boolean tLSSupported) {
            config.settLSSupported(tLSSupported);
            return this;
        }

        public Builder setConnectionTimeout(int connectionTimeout) {
            config.setConnectionTimeout(connectionTimeout);
            return this;
        }

        public Builder setReceiveBufferSize(int receiveBufferSize) {
            config.setReceiveBufferSize(receiveBufferSize);
            return this;
        }

        public Builder setMaxConnections(int maxConnections) {
            config.setMaxConnections(maxConnections);
            return this;
        }

        public Builder setBacklog(int backlog) {
            config.setBacklog(backlog);
            return this;
        }
    }


}
