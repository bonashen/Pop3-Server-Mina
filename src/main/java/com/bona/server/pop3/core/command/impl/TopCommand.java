package com.bona.server.pop3.core.command.impl;

import com.bona.server.pop3.core.command.AbstractCommand;
import com.bona.server.pop3.util.MimeMessageParser;
import com.sun.mail.util.LineInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Created by bona on 2015/10/15.
 */
public class TopCommand extends AbstractCommand {
    private final static Logger LOG = LoggerFactory.getLogger(TopCommand.class);

    @Override
    protected void exec(String argument) {
        if (null == argument) {
            sendErrMessage("must set mail index!");
            return;
        }
        String[] argv = argument.split(" ");

        int index = Integer.parseInt(argv[0]) - 1;
        int readRowCount = -1;
        if (argv.length >= 2)
            readRowCount = Integer.parseInt(argv[1]);
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
                List<String> parts = new ArrayList<String>();
                boolean body = false;
                int row = 0;
                while ((text = lis.readLine()) != null) {
                    parts.add(text);
//                    LOG.debug(text);
                    //body start.
                    if (text.equals("") && body == false) {
                        body = true;
//                        continue;
                    }
                    if (body) row++;
                    //body end.
                    if (body && ((row >= readRowCount))) {
                        break;
                    }
                }
                if (readRowCount > 0 && row < readRowCount) {
                    sendErrMessage("Too much rows.");
                    return;
                }

                sendOkMessage("index:" + (index + 1));

                sendMessage(parts);

//                StringBuilder sb = new StringBuilder();
//                for (String s : parts){
//                    LOG.debug("row:"+s);
//                    sb.append(s+"\n\r");
//                }
//                LOG.debug("send:"+sb.toString());
//                ByteArrayInputStream in = new ByteArrayInputStream(sb.toString().getBytes());
//                sendMessage(in);
//                in.close();

//                    sendMessage(s);
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
