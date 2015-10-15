package com.bona.server.pop3.api.Handler;

import com.bona.server.pop3.api.AuthenticationHandler;

/**
 * Created by bona on 2015/10/12.
 */
public class DefaultAuthHandler implements AuthenticationHandler {
    public boolean authUser(String userName, String password) {
        return true;
    }
}
