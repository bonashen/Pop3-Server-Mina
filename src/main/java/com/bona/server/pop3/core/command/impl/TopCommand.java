package com.bona.server.pop3.core.command.impl;

import com.bona.server.pop3.core.command.AbstractCommand;
import org.apache.commons.mail.util.MimeMessageParser;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by bona on 2015/10/15.
 */
public class TopCommand extends AbstractCommand {
    @Override
    protected void exec(String argument) {
        if (null == argument) {
            sendErrMessage("must set mail index!");
            return;
        }
        String[] argv = argument.split(" ");

        int index = Integer.parseInt(argv[0]) - 1;
        int contextRow = 0;
        if (argv.length >= 2)
            contextRow = Integer.parseInt(argv[1]);
        InputStream is = getStorage().openStream(index);
        if (is == null) {
            sendErrMessage("mail has been deleted!");
            return;
        }
        try {
            MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties()), is);
            MimeMessageParser parser = new MimeMessageParser(message).parse();
            Enumeration hdrLines = message.getAllHeaderLines();
            sendOkMessage("index:"+index+1);
            while (hdrLines.hasMoreElements()) {
                sendMessage((String) hdrLines.nextElement());
            }
            if (parser.hasPlainContent() || parser.hasHtmlContent()) {
                BufferedReader br = new BufferedReader(new StringReader(parser.hasPlainContent() ? parser.getPlainContent() : parser.getHtmlContent()));
                String txt;
                int row = 0;
                while ((txt = br.readLine()) != null && row < contextRow) {
                    sendMessage(txt);
                    row++;
                }
            }
            sendEndMessage();

        } catch (Exception e) {
            sendErrMessage(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
