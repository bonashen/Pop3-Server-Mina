package com.bona.server.pop3.core.command.impl;

import com.bona.server.pop3.core.POP3Context;
import com.bona.server.pop3.core.command.AbstractCommand;

/**
 * Created by bona on 2015/10/12.
 */
public class NoopCommand extends AbstractCommand {
    @Override
    public void exec(String argument) {
        sendOkMessage(" ");
    }
}
