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
    public static final String EMAIL_FILE_EXT_NAME = ".eml";

    private File root = null;
    private Map<Integer, File> deleteFiles = Collections.synchronizedMap(new HashMap<Integer, File>());

    private List<File> mails = Collections.synchronizedList(new ArrayList<File>());

    private FilenameFilter filenameFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            int i = name.lastIndexOf(".");
            String extName = i > 0 ? name.substring(i) : null;
            return extName == null ? false : extName.equalsIgnoreCase(EMAIL_FILE_EXT_NAME);
        }
    };

    public FileStorage(File path) {
        root = path;
    }


    @Override
    public String getState(int index) {
        if (checkIndex(index) && !isDeleted(index)) {
            String state = getMails().get(index).getName();
            state = state.substring(0, state.lastIndexOf("."));
            return "unique:" + state;
        }
        return null;
    }

    @Override
    public void fetch() {
        for (File file : getUserDir().listFiles(filenameFilter)) {
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

    protected List<File> getMails() {
        if (mails.size() == 0)
            mails.addAll(Arrays.asList(getUserDir().listFiles(filenameFilter)));
        return mails;
    }

    protected File getUserDir() {
        if (null == root) {
            root = new File(System.getProperty(Constants.INBOX_STORAGE_DIR, "."));
            LOG.info("Inbox storage path:{}", root.getAbsolutePath());
        }
        File dir = new File(root, getUserName());
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    protected boolean isDeleted(int index) {
        return checkIndex(index)? (!getMails().get(index).exists()):false || deleteFiles.containsKey(index);
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
    public InputStream openStream(int index) {
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
    public boolean delete(int index) {
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
    public void commit() {
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
    public void rollback() {
        if (getContext().isAuthorized())
            LOG.info("User {} rollback delete {} files.", getUserName(), deleteFiles.size());
        deleteFiles.clear();
        super.rollback();
    }

}
