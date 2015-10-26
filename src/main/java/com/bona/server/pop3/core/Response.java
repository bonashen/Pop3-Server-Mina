package com.bona.server.pop3.core;

import java.io.InputStream;
import java.util.List;

/**
 * Created by bona on 2015/10/13.
 */
public interface Response {

    void sendMessage(String message);

    void sendOkMessage(String message);

    void sendErrMessage(String message);

    void sendMessage(InputStream data);

    void sendMessage(List<String> data);

    void close();
}
