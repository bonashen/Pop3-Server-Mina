# Pop3-Server-Mina

This POP3Server is developed on the basis of MINA Apache(NIO), to implementation RFC1939 part of the command. Command list is as follows:

    CAPA,DELE,LIST,NOOP,PASS, USER, REST, RETR, QUIT, STAT, TOP, UIDL


* Create and start POP3 Server

```java

public class RunServer {

    
    public static void main(String[] args){
        //set storage factory.
        System.setProperty(Constants.PROTOCOL_POP3_STORAGE_FACTORY, FileStorageFactory.class.getName());
        /**
         * set storage root path
         * like {rootpath}\username\*.eml
        **/
        System.setProperty(Constants.INBOX_STORAGE_DIR,new File(".").getAbsolutePath());

        POP3Server server = new POP3Server(new DefaultAuthHandler(){
            @Override
            public boolean authUser(String userName, String password) {
                return userName.equalsIgnoreCase("bona.shen@gmail.com") && password.equalsIgnoreCase("123456");
            }
        });
        server.start();

    }
}
```

* LoggerFilter
insert before server.start(),you can add follow codes implementation log client request.

```java
server.getConfig().addFilter("logger",new LoggerRequestFilter());
```

* Custom storage
Custom storage implementation and integration of other systems

```java

server.getConfig().setStorageFactory(
                new StorageFactory() {
                    @Override
                    public Storage createStorage() {
                        return new DefaultStorage() {
                            @Override
                            public int getCount() {
                                return 1;
                            }

                            @Override
                            public InputStream openStream(int index) {
                                MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties()));
                                ByteArrayOutputStream out=null;
                                try {
                                    message.getAllHeaderLines();
                                    message.setFrom("bona.shen@csgjs.com");
                                    message.setText("test");
                                    message.setSubject("One mail");
                                    InternetAddress to = new InternetAddress("bona.shen@gmail.com");
                                    message.setRecipient(Message.RecipientType.TO, to);
                                    out = new ByteArrayOutputStream();
                                    message.writeTo(out);

                                } catch (MessagingException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return new ByteArrayInputStream(out.toByteArray());
                            }

                            @Override
                            public String getState(int index) {
                                return "One mail";
                            }

                            @Override
                            public long getSize(int index) {
                                int size = 256;
                                LOG.debug("get user:{} index:{} size:{} .",getUserName(),index,size);
                                return size;
                            }

                            @Override
                            public long getSize() {
                                return getSize(0);
                            }
                        };
                    }
                }
        );
```