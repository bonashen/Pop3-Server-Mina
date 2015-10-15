package com.bona.server.pop3.core.command;

/**
 * Created by bona on 2015/10/10.
 * <b>
 *     quit 命令表示要结束邮件接收过程，POP3服务器接收到此命令后，将删除所有设置了删除标记的邮件，并关闭与POP3客户端程序的网络连接。
 * </b>
 */
public class QuitCommand extends AbstractCommand {
    @Override
    public void exec( String argument) {
        getStorage().commit();
        sendOkMessage("Bye");
        getResponse().close();
    }
}
