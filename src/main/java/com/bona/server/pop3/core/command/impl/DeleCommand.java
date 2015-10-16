package com.bona.server.pop3.core.command.impl;

import com.bona.server.pop3.core.command.AbstractCommand;

/**
 * Created by bona on 2015/10/10.
 * <b>
 *      dele 命令用于在某封邮件上设置删除标记，参数msg#表示邮件的序号。
 *      POP3服务器执行dele命令时，只是为邮件设置了删除标记，并没有真正把邮件删除掉，
 *      只有POP3客户端发出quit命令后，POP3服务器才会真正删除所有设置了删除标记的邮件。
 * </b>
 */
public class DeleCommand extends AbstractCommand {
    @Override
    public void exec(String argument) {
        int index = Integer.parseInt(argument) - 1;
        if (getStorage().delete(index)) {
            sendOkMessage( "message " + argument + " deleted");
        } else {
            sendErrMessage( "message " + argument + " already deleted");
        }
    }
}
