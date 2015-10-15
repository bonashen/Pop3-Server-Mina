package com.bona.server.pop3.server;

import com.bona.server.pop3.POP3Server;
import com.bona.server.pop3.POP3ServerConfig;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Created by bona on 2015/10/12.
 */
public class TestBase {
    protected POP3Server server;

    @Before
    public void initServer() throws IOException {
        server = new POP3Server();
        server.start();
    }

    @Test
    public void popMail() throws MessagingException {
        new PopClient("bona.shen@gmail.com","123456").receive();
    }

    @After
    public void shutdown(){
        server.shutdown();
    }
}
