package com.bona.server.pop3.core.factory;

import com.bona.server.pop3.core.command.Command;

/**
 * Created by bona on 2015/10/10.
 */
public interface POP3Factory {

    public Command createCommand(String name);
}
