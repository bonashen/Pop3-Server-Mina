package com.bona.server.pop3.server;

import com.bona.server.pop3.POP3Server;
import com.bona.server.pop3.POP3ServerConfig;
import com.bona.server.pop3.api.Handler.DefaultAuthHandler;
import com.bona.server.pop3.core.filter.AuthFilter;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by bona on 2015/10/13.
 */
public class CustomAuthHandlerTest extends TestBase {
    private final static Logger LOG = LoggerFactory.getLogger(CustomAuthHandlerTest.class);

    // 定义监听端口
    private static final int PORT = 110;

    @Before
    @Override
    public void initServer() throws IOException {
        POP3ServerConfig cfg = POP3ServerConfig.builder().setAuthHandler(new DefaultAuthHandler() {
            @Override
            public boolean authUser(String userName, String password) {
                LOG.debug("check user name and password!");
                if (userName.equalsIgnoreCase("bona.shen@gmail.com") && password.equalsIgnoreCase("123456")) {
                    return true;
                }
                return false;
            }
        })
                .setPort(PORT)
                .addFilter("auth",new AuthFilter())
                .build();
        server = new POP3Server(cfg);
        initConfig(cfg);
        server.start();

    }

    protected void initConfig(POP3ServerConfig cfg) {

    }


}
