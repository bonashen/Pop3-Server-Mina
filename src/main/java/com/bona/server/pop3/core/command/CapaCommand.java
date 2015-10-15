package com.bona.server.pop3.core.command;

import com.bona.server.pop3.core.POP3Context;

/**
 * Created by bona on 2015/10/10.
 */
public class CapaCommand extends AbstractCommand {
    @Override
    protected void exec(String argument) {
        sendOkMessage("");
        sendMessage("USER");
        sendMessage("UIDL");
        sendEndMessage();
    }

}
