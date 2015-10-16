package com.bona.server.pop3.core.command.impl;

import com.bona.server.pop3.core.command.AbstractCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by bona on 2015/10/10.
 * <b>
 *     retr 命令用于获取某封邮件的内容，参数 msg#表示邮件的序号。
 * </b>
 */
public class RetrCommand extends AbstractCommand {
    private final static Logger LOG = LoggerFactory.getLogger(RetrCommand.class);

    @Override
    public void exec( String argument) {
        if(null==argument){
            sendErrMessage(" Mail number or Password is required...");
            return;
        }

        int index = Integer.parseInt(argument) - 1;
        InputStream mailStream = getStorage().openStream(index);
        if(null==mailStream){
            sendErrMessage("not found mail!maybe delete!");
            return;
        }
        BufferedReader br =  new BufferedReader(new InputStreamReader(mailStream));

        try {
            LOG.debug("prepare mail for user client.");
            String s = null;
            sendOkMessage(" ");
            while ((s = br.readLine()) != null) {
                sendMessage(s);
            }
            sendEndMessage();
        } catch (Exception e) {
            e.printStackTrace();
            sendErrMessage(e.getMessage());
        }finally {
            try {
                br.close();
            } catch (IOException e) {
                LOG.error("close BufferedReader error.",e);
                e.printStackTrace();
            }
        }
    }
}