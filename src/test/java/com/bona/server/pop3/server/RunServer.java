package com.bona.server.pop3.server;

import com.bona.server.pop3.POP3Server;
import com.bona.server.pop3.api.Handler.DefaultAuthHandler;
import com.bona.server.pop3.core.factory.FileStorageFactory;
import com.bona.server.pop3.util.Constants;

import java.io.File;
import java.io.IOException;

/**
 * Created by bona on 2015/10/14.
 */
public class RunServer {
    public static void main(String[] args){
        //set storage factory.
        System.setProperty(Constants.PROTOCOL_POP3_STORAGE_FACTORY, FileStorageFactory.class.getName());
        /**
         * set storage root path
        **/
        System.setProperty(Constants.INBOX_STORAGE_DIR,new File(".").getAbsolutePath());

        POP3Server server = new POP3Server(new DefaultAuthHandler(){
            @Override
            public boolean authUser(String userName, String password) {
                return userName.equalsIgnoreCase("bona.shen@gmail.com") && password.equalsIgnoreCase("123456");
            }
        });
        server.start();

    }
}
