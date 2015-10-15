package com.bona.server.pop3.server;

import com.bona.server.pop3.POP3ServerConfig;
import com.bona.server.pop3.core.factory.FileStorageFactory;
import com.bona.server.pop3.util.Constants;
import org.junit.Test;

import javax.mail.MessagingException;

/**
 * Created by bona on 2015/10/14.
 */
public class UseFileStorage extends CustomAuthHandlerTest {

    public UseFileStorage(){
        System.setProperty(Constants.PROTOCOL_POP3_STORAGE_FACTORY, FileStorageFactory.class.getName());

    }
    @Override
    protected void initConfig(POP3ServerConfig cfg) {
    }

    @Test
    @Override
    public void popMail() throws MessagingException {
        new PopClient("bona.shen@gmail.com","123456").receiveAndDelete();
    }

}
