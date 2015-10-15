package com.bona.server.pop3.util;

import com.bona.server.pop3.api.AuthenticationHandler;
import com.bona.server.pop3.core.POP3Context;

/**
 * Created by bona on 2015/10/10.
 */
public class Constants {
    public final static String POP_OK = "+OK ";
    public final static String POP_ERR = "-ERR ";
    public final static String POP_WELCOME=POP_OK+"BonaMail POP3 Server is ready!";
    public static final String PROTOCOL_POP3_FACTORY = "org.bona.mail.protocol.pop3.factory";
    public static final String PROTOCOL_POP3_STORAGE_FACTORY = "org.bona.mail.protocol.pop3.Storage.factory";
    /*System evn variant*/
    public static final String INBOX_STORAGE_DIR = "INBOX_STORAGE_DIR";
}
