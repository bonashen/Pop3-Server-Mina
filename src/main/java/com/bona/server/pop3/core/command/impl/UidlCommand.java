package com.bona.server.pop3.core.command.impl;

import com.bona.server.pop3.core.command.AbstractCommand;

/**
 * Created by bona on 2015/10/10.
 * <b>
 * uidl 命令用于查询某封邮件的唯一标志符，参数msg#表示邮件的序号，是一个从1开始编号的数字。
 * </b>
 */
public class UidlCommand extends AbstractCommand {
    @Override
    public void exec(String argument) {
        sendOkMessage(" ");
        if (argument == null) {
            for (int i = 0; i < getStorage().getCount(); i++)
                sendMessage(i + 1 + " " + getStorage().getIdentity(i));
            sendMessage(".");
        }else{
            int index = Integer.parseInt(argument);
            String state=getStorage().getIdentity(index - 1);
            if(null==state)sendErrMessage("no such message");
            else sendMessage(index + " " + state);
        }
    }
}
