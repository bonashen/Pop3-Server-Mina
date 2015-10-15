package com.bona.server.pop3.core.command;


import com.bona.server.pop3.api.Storage;

/**
 * Created by bona on 2015/10/9.
 * <b>
 *      stat 命令用于查询邮箱中的统计信息，例如：邮箱中的邮件数量和邮件占用的字节大小等。
 * </b>
 */
public class StatCommand extends AbstractCommand {

    @Override
    public void exec(String argument) {
        Storage storage = getStorage();
        storage.fetch();
        sendOkMessage(storage.getCount() + " " + storage.getSize());
    }
}
