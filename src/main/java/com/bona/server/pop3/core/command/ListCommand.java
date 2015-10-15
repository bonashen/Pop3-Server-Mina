package com.bona.server.pop3.core.command;

/**
 * Created by bona on 2015/10/9.
 * <b>
 *     list 命令用于列出邮箱中的邮件信息，参数 msg#是一个可选参数，表示邮件的序号。
 *     当不指定参数时，POP3服务器列出邮箱中所有的邮件信息；
 *     当指定参数msg#时，POP3服务器只返回序号对应的邮件信息。
 * </b>
 */
public class ListCommand extends AbstractCommand {
    @Override
    public void exec( String argument) {
        if (argument == null) {
            sendOkMessage(" ");
            int len = getStorage().getCount();
            for (int i = 0; i < len; i++)
            {
                sendMessage(i + 1 + " " + getStorage().getSize(i));
            }
            sendMessage(".");
        } else {
            int index = Integer.parseInt(argument);
            sendOkMessage(index + " " + getStorage().getSize(index - 1));
        }
    }
}
