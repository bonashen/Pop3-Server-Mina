package com.bona.server.pop3.core.storage;

import com.bona.server.pop3.util.Constants;
import com.sun.mail.util.LineOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Created by bona on 2015/10/14.
 */
public class FileStorage extends DefaultStorage {

    private final static Logger LOG = LoggerFactory.getLogger(FileStorage.class);

    private final File root;
    private Map<Integer, File> deleteFiles = new HashMap<Integer, File>();
    private List<File> mails = new ArrayList<File>();

    public FileStorage() {
        root = new File(System.getProperty(Constants.INBOX_STORAGE_DIR, "."));
        LOG.info("Inbox storage path:{}", root.getAbsolutePath());
    }

    @Override
    public synchronized void fetch() {
        for (File file : getUserDir().listFiles()) {
            boolean append = true;
            for (File mail : mails) {
                if (mail.equals(file)) {
                    append = false;
                    break;
                }
            }
            if (append) {
                mails.add(file);
            }
        }
    }

    protected boolean checkIndex(int index) {
        return index < getMails().size() && index > -1;
    }

    protected synchronized List<File> getMails() {
        if (mails.size() == 0)
            mails.addAll(Arrays.asList(getUserDir().listFiles()));
        return mails;
    }

    protected File getUserDir() {
        File dir = new File(root, getUserName());
        if (!dir.exists()) dir.mkdirs();
        LOG.info("User {} Inbox storage path:{}", getUserName(), dir.getAbsolutePath());
        return dir;
    }

    protected boolean isDeleted(int index) {
        return !getMails().get(index).exists() || deleteFiles.containsKey(index);
    }

    @Override
    public long getSize(int index) {
        if (checkIndex(index))
            return getMails().get(index).length();
        else
            return 0;
    }

    @Override
    public long getSize() {
        long size = 0;
        for (File file : getMails()) {
            size += file.length();
        }
        return size;
    }

    @Override
    public int getCount() {
        return getMails().size();
    }

    @Override
    public synchronized InputStream openStream(int index) {
        /**
         * if index mail not in 0..count range or index mail is deleted  return null;
         */
        if (isDeleted(index) || !checkIndex(index)) return null;

        ByteArrayOutputStream buffered = new ByteArrayOutputStream();
        LineOutputStream out = new LineOutputStream(buffered);
        FileInputStream fileStream;
        try {
            fileStream = new FileInputStream(getMails().get(index));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
        String txt;
        try {
            while ((txt = reader.readLine()) != null) {
                out.writeln(txt);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                fileStream.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return new ByteArrayInputStream(buffered.toByteArray());
    }

    @Override
    public synchronized boolean delete(int index) {
        boolean deleted = deleteFiles.containsKey(index);
        if (!deleted || !getMails().get(index).exists()) {
            if (checkIndex(index))
                deleteFiles.put(index, getMails().get(index));
            else
                deleted = true;
        }
        if (deleted) return false;
        LOG.info("User {}  delete {} !", getUserName(), getMails().get(index).getName());
        return true;
    }

    @Override
    public String getState(int index) {
        if (checkIndex(index))
            return "size:" + getMails().get(index).length();
        else
            return "not found!";
    }

    @Override
    public synchronized void commit() {
        for (File file : deleteFiles.values()) {
            getMails().remove(file);
            if (!file.delete()) {
                LOG.error("User {} delete {} error!", getUserName(), file.getName());
            }
        }
        if (getContext().isAuthorized()) LOG.info("User {} commit delete {} files.", getUserName(), deleteFiles.size());
        super.commit();
    }

    @Override
    public synchronized void rollback() {
        if (getContext().isAuthorized())
            LOG.info("User {} rollback delete {} files.", getUserName(), deleteFiles.size());
        deleteFiles.clear();
        super.rollback();
    }

}
