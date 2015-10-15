package com.bona.server.pop3.core.command;

/**
 * Created by bona on 2015/10/12.
 *  rest 命令用于清除所有邮件的删除标记。
 */
public class RestCommand extends AbstractCommand {
    @Override
    public void exec(String argument) {
        getStorage().rollback();
        sendOkMessage("rollback all deleted mails.");
    }
}
