package com.bona.server.pop3.core.factory;

import com.bona.server.pop3.api.Storage;
import com.bona.server.pop3.core.storage.FileStorage;
import com.bona.server.pop3.util.Constants;

import java.io.File;

/**
 * Created by bona on 2015/10/14.
 */
public class FileStorageFactory implements StorageFactory {
    private String path;

    @Override
    public Storage createStorage() {
        return new FileStorage(getPath());
    }

    public FileStorageFactory() {
        path = new File(System.getProperty(Constants.INBOX_STORAGE_DIR, ".")).getAbsolutePath();
    }

    public FileStorageFactory( File path) {
        this();
        setPath(path);
    }

    public FileStorageFactory(String path) {
        this(new File(path));
    }

    public File getPath() {
        if (path == null) {
            path = new File(System.getProperty(Constants.INBOX_STORAGE_DIR, ".")).getAbsolutePath();
        }
        return new File(path);
    }

    public void setPath(File path) {

        this.path = path.getAbsolutePath();
    }
}
