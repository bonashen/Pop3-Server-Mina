package com.bona.server.pop3.api;

import java.net.SocketAddress;

/**
 * Created by bona on 2015/10/16.
 */
public interface SessionContext {

    public Object getAttribute(String key);

    public Object getAttribute(String key, Object def);

    public void setAttribute(String key,Object value);

    public String getUserName();

    public void setUserName(String userName);

    public boolean hasUser();

    public boolean isAuthorized();

    public SocketAddress getRemoteAddress();
}
