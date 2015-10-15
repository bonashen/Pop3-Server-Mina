package com.bona.server.pop3.server;

import com.bona.server.pop3.core.POP3Context;

import javax.mail.*;
import java.util.Properties;

/**
 * Created by bona on 2015/10/13.
 */
public class PopClient {
    private final String username;
    private final String password;

    public PopClient(String username,String password){
        this.username = username;
        this.password = password;

    }

    public void receive(){
        Properties props = new Properties();

        javax.mail.Session session =  javax.mail.Session.getDefaultInstance(props, null);
        System.out.println("create session.");
        String host = "localhost";
        Store store = null;
        try {
            store = session.getStore("pop3");

        System.out.println("get store pop3");
        System.out.println("connecting to "+host);
        store.connect(host,110, username, password);
        System.out.println("connected to "+host);
        // Get folder
        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);

        // Get directory
        Message message[] = folder.getMessages();
        for (int i=0, n=message.length; i<n; i++) {
            System.out.println(i + ": " + message[i].getFrom()[0]
                    + "/t" + message[i].getSubject());


        }

        //Close connection

        folder.close(false);

        store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void receiveAndDelete(){
        Properties props = new Properties();

        javax.mail.Session session =  javax.mail.Session.getDefaultInstance(props, null);
        System.out.println("create session.");
        String host = "localhost";
        Store store = null;
        try {
            store = session.getStore("pop3");

            System.out.println("get store pop3");
            System.out.println("connecting to "+host);
            store.connect(host,110, username, password);
            System.out.println("connected to "+host);
            // Get folder
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            // Get directory
            Message message[] = folder.getMessages();
            for (int i=0, n=message.length; i<n; i++) {
                System.out.println(i + ": " + message[i].getFrom()[0]
                        + "/t" + message[i].getSubject());

                message[i].setFlag(Flags.Flag.DELETED, true);
            }

            //Close connection

            folder.close(true);

            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
