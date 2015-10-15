package com.bona.server.pop3.core.command;

import com.bona.server.pop3.core.POP3Context;
import org.apache.mina.core.session.IoSession;

/**
 * Created by bona on 2015/10/9.
 * 无状态POP3协议处理命令
 */
public interface Command {

    public void execute(POP3Context context, String argument);

}
