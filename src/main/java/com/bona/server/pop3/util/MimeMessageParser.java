package com.bona.server.pop3.util;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.*;
import java.util.*;

/**
 * Created by bona on 2015/10/20.
 */

public class MimeMessageParser {
    private final MimeMessage mimeMessage;
    private String plainContent;
    private String htmlContent;
    private final List<DataSource> attachmentList = new ArrayList();
    private final Map<String, DataSource> cidMap = new HashMap();
    private boolean isMultiPart;

    public MimeMessageParser(MimeMessage message) {
        this.mimeMessage = message;
        this.isMultiPart = false;
    }

    public MimeMessageParser parse() throws Exception {
        this.parse((Multipart)null, this.mimeMessage);
        return this;
    }

    public List<Address> getTo() throws Exception {
        Address[] recipients = this.mimeMessage.getRecipients(Message.RecipientType.TO);
        return (List)(recipients != null? Arrays.asList(recipients):new ArrayList());
    }

    public List<Address> getCc() throws Exception {
        Address[] recipients = this.mimeMessage.getRecipients(Message.RecipientType.CC);
        return (List)(recipients != null?Arrays.asList(recipients):new ArrayList());
    }

    public List<Address> getBcc() throws Exception {
        Address[] recipients = this.mimeMessage.getRecipients(Message.RecipientType.BCC);
        return (List)(recipients != null?Arrays.asList(recipients):new ArrayList());
    }

    public String getFrom() throws Exception {
        Address[] addresses = this.mimeMessage.getFrom();
        return addresses != null && addresses.length != 0?((InternetAddress)addresses[0]).getAddress():null;
    }

    public String getReplyTo() throws Exception {
        Address[] addresses = this.mimeMessage.getReplyTo();
        return addresses != null && addresses.length != 0?((InternetAddress)addresses[0]).getAddress():null;
    }

    public String getSubject() throws Exception {
        return this.mimeMessage.getSubject();
    }

    protected void parse(Multipart parent, MimePart part) throws MessagingException, IOException {
        if(this.isMimeType(part, "text/plain") && this.plainContent == null && !"attachment".equalsIgnoreCase(part.getDisposition())) {
            this.plainContent = (String)part.getContent();
        } else if(this.isMimeType(part, "text/html") && this.htmlContent == null && !"attachment".equalsIgnoreCase(part.getDisposition())) {
            this.htmlContent = (String)part.getContent();
        } else if(this.isMimeType(part, "multipart/*")) {
            this.isMultiPart = true;
            Multipart cid = (Multipart)part.getContent();
            int ds = cid.getCount();

            for(int i = 0; i < ds; ++i) {
                this.parse(cid, (MimeBodyPart)cid.getBodyPart(i));
            }
        } else {
            String var6 = this.stripContentId(part.getContentID());
            DataSource var7 = this.createDataSource(parent, part);
            if(var6 != null) {
                this.cidMap.put(var6, var7);
            }

            this.attachmentList.add(var7);
        }

    }

    private String stripContentId(String contentId) {
        return contentId == null?null:contentId.trim().replaceAll("[\\<\\>]", "");
    }

    private boolean isMimeType(MimePart part, String mimeType) throws MessagingException, IOException {
        try {
            ContentType ex = new ContentType(part.getDataHandler().getContentType());
            return ex.match(mimeType);
        } catch (ParseException var4) {
            return part.getContentType().equalsIgnoreCase(mimeType);
        }
    }

    protected DataSource createDataSource(Multipart parent, MimePart part) throws MessagingException, IOException {
        DataHandler dataHandler = part.getDataHandler();
        DataSource dataSource = dataHandler.getDataSource();
        String contentType = this.getBaseMimeType(dataSource.getContentType());
        byte[] content = this.getContent(dataSource.getInputStream());
        ByteArrayDataSource result = new ByteArrayDataSource(content, contentType);
        String dataSourceName = this.getDataSourceName(part, dataSource);
        result.setName(dataSourceName);
        return result;
    }

    public MimeMessage getMimeMessage() {
        return this.mimeMessage;
    }

    public boolean isMultipart() {
        return this.isMultiPart;
    }

    public String getPlainContent() {
        return this.plainContent;
    }

    public List<DataSource> getAttachmentList() {
        return this.attachmentList;
    }

    public Collection<String> getContentIds() {
        return Collections.unmodifiableSet(this.cidMap.keySet());
    }

    public String getHtmlContent() {
        return this.htmlContent;
    }

    public boolean hasPlainContent() {
        return this.plainContent != null;
    }

    public boolean hasHtmlContent() {
        return this.htmlContent != null;
    }

    public boolean hasAttachments() {
        return this.attachmentList.size() > 0;
    }

    public DataSource findAttachmentByName(String name) {
        for(int i = 0; i < this.getAttachmentList().size(); ++i) {
            DataSource dataSource = (DataSource)this.getAttachmentList().get(i);
            if(name.equalsIgnoreCase(dataSource.getName())) {
                return dataSource;
            }
        }

        return null;
    }

    public DataSource findAttachmentByCid(String cid) {
        DataSource dataSource = (DataSource)this.cidMap.get(cid);
        return dataSource;
    }

    protected String getDataSourceName(Part part, DataSource dataSource) throws MessagingException, UnsupportedEncodingException {
        String result = dataSource.getName();
        if(result == null || result.length() == 0) {
            result = part.getFileName();
        }

        if(result != null && result.length() > 0) {
            result = MimeUtility.decodeText(result);
        } else {
            result = null;
        }

        return result;
    }

    private byte[] getContent(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BufferedInputStream isReader = new BufferedInputStream(is);
        BufferedOutputStream osWriter = new BufferedOutputStream(os);

        int ch;
        while((ch = isReader.read()) != -1) {
            osWriter.write(ch);
        }

        osWriter.flush();
        byte[] result = os.toByteArray();
        osWriter.close();
        return result;
    }

    private String getBaseMimeType(String fullMimeType) {
        int pos = fullMimeType.indexOf(59);
        return pos >= 0?fullMimeType.substring(0, pos):fullMimeType;
    }
}

