package com.bona.server.pop3.core.command;


import com.bona.server.pop3.POP3ServerConfig;
import com.bona.server.pop3.api.AuthenticationHandler;
import com.bona.server.pop3.core.Response;
import com.bona.server.pop3.api.Storage;
import com.bona.server.pop3.core.POP3Context;

import java.io.InputStream;
import java.util.List;

/**
 * Created by bona on 2015/10/9.
 */
public abstract class AbstractCommand implements Command {

    private POP3Context context;

    abstract protected void exec(String argument);

    public final void execute(POP3Context context, String argument) {
        this.context = context;
        exec(argument);
    }


    public String getUserName() {
        String userName = getCurrentContext().getUserName();
        if (getCurrentContext().hasUser()) {
            return userName.toString();
        } else
            return null;
    }

    public void sendMessage(String message){
        getResponse().sendMessage(message);
    }

    public void sendOkMessage(String message) {
        getResponse().sendOkMessage(message);
    }

    public void sendErrMessage(String message) {
        getResponse().sendErrMessage(message);
    }

    public void sendEndMessage() {
        sendMessage(".");
    }

    public void sendMessage(InputStream data){
        getResponse().sendMessage(data);
    };

    public void sendMessage(List<String> data) {
        getResponse().sendMessage(data);
    }

    public Storage getStorage(){
        return getCurrentContext().getStorage();
    }

    public AuthenticationHandler getAuthHandler(){
        return getConfig().getAuthenticationHandler();
    }

    public POP3ServerConfig getConfig(){
        return getCurrentContext().getConfig();
    }


    public Response getResponse() {
        return getCurrentContext().getResponse();
    }

    public POP3Context getCurrentContext() {
        return context;
    }
}
