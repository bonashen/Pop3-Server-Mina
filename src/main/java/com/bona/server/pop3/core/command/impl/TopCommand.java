package com.bona.server.pop3.core.command.impl;

import com.bona.server.pop3.core.command.AbstractCommand;
import com.bona.server.pop3.util.MimeMessageParser;
import com.sun.mail.util.LineInputStream;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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
            /*
            MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties()), is);
            MimeMessageParser parser = new MimeMessageParser(message).parse();
            Enumeration hdrLines = message.getAllHeaderLines();
            */

            //send email header lines
            LineInputStream lis = new LineInputStream(is);
            try {
                String text;
                List<String> sb = new ArrayList<String>();
                boolean body = false;
                int row = 0;
                while ((text = lis.readLine()) != null) {
                    sb.add(text);
                    //body start.
                    if (text.isEmpty() && body == false) {
                        body = true;
                        continue;
                    }
                    if (body) row++;
                    //body end.
                    if (body && ((row >= contextRow) || text.isEmpty())) {
                        break;
                    }
                }
                if (contextRow > 0 && row < contextRow) {
                    sendErrMessage("Too much rows.");
                    return;
                }
                sendOkMessage("index:" + (index + 1));
                for (String s : sb)
                    sendMessage(s);
            } finally {
                lis.close();
            }

            /*while (hdrLines.hasMoreElements()) {
                sendMessage((String) hdrLines.nextElement());
            }*/
            //send email body
//            if (parser.hasPlainContent() || parser.hasHtmlContent()) {
//                BufferedReader br = new BufferedReader(new StringReader(parser.hasPlainContent() ? parser.getPlainContent() : parser.getHtmlContent()));
//                String txt;
//                int row = 0;
//                while ((txt = br.readLine()) != null && row < contextRow) {
//                    sendMessage(txt);
//                    row++;
//                }
//            }
            sendEndMessage();

        } catch (Exception e) {
            sendErrMessage(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
