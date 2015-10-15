package com.bona.server.pop3.server;

import com.bona.server.pop3.POP3ServerConfig;
import com.bona.server.pop3.api.Storage;
import com.bona.server.pop3.core.factory.StorageFactory;
import com.bona.server.pop3.core.filter.LoggerRequestFilter;
import com.bona.server.pop3.core.storage.DefaultStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Properties;

/**
 * Created by bona on 2015/10/14.
 */
public class CustomStorage extends CustomAuthHandlerTest {
    private final static Logger LOG = LoggerFactory.getLogger(CustomStorage.class);

    @Override
    protected void initConfig(POP3ServerConfig cfg) {

        cfg.addFilter("logger", new LoggerRequestFilter());

        for (String key : cfg.getFilterMap().keySet())
            System.out.printf("Filter Name:%s \n",key);

        cfg.setStorageFactory(
                new StorageFactory() {
                    @Override
                    public Storage createStorage() {
                        return new DefaultStorage() {
                            @Override
                            public int getCount() {
                                return 1;
                            }

                            @Override
                            public InputStream openStream(int index) {
                                MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties()));
                                ByteArrayOutputStream out=null;
                                try {
                                    message.getAllHeaderLines();
                                    message.setFrom("bona.shen@csgjs.com");
                                    message.setText("test");
                                    message.setSubject("One mail");
                                    InternetAddress to = new InternetAddress("bona.shen@gmail.com");
                                    message.setRecipient(Message.RecipientType.TO, to);
                                    out = new ByteArrayOutputStream();
                                    message.writeTo(out);

                                } catch (MessagingException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return new ByteArrayInputStream(out.toByteArray());
                            }

                            @Override
                            public String getState(int index) {
                                return "One mail";
                            }

                            @Override
                            public long getSize(int index) {
                                int size = 256;
                                LOG.debug("get user:{} index:{} size:{} .",getUserName(),index,size);
                                return size;
                            }

                            @Override
                            public long getSize() {
                                return getSize(0);
                            }
                        };
                    }
                }
        );
    }
}
