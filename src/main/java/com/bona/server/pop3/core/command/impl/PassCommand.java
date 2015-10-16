package com.bona.server.pop3.core.command.impl;


import com.bona.server.pop3.core.command.AbstractCommand;

/**
 * Created by bona on 2015/10/9.
 * <b>
 * pass 命令是在user命令成功通过后，POP3客户端程序接着发送的命令，它用于传递帐户的密码，参数 password 表示帐户的密码。
 * </b>
 */
public class PassCommand extends AbstractCommand {

    @Override
    public void exec(String argument) {
        String userName = "", password = argument;
        if (getCurrentContext().hasUser())
            userName = getCurrentContext().getUserName();
        if ("".equals(userName) ||
                userName == null ||
                "".equals(password) ||
                password == null ||
                !getAuthHandler().authUser(userName, password)) {
            sendErrMessage("User's name or password is error!");
        } else {
//            getCurrentContext().setAttribute(Constants.AUTH_VALUE, true);
            getCurrentContext().authorize();

            sendOkMessage("");
        }
    }
}
