package com.bona.server.pop3.core.command.impl;


import com.bona.server.pop3.core.command.AbstractCommand;

/**
 * Created by bona on 2015/10/9.
 * <b>
 * user 命令是POP3客户端程序与POP3邮件服务器建立连接后通常发送的第一条命令，参数 username 表示收件人的帐户名称。
 * </b>
 */
public class UserCommand extends AbstractCommand {

    public void exec(String argument) {
        getCurrentContext().authorize(false);
        if(argument==null){
            sendErrMessage("user name must set value.");
            return;
        }
        getCurrentContext().setUserName(argument.trim());
        sendOkMessage(argument + " is a user");
    }
}
