package com.bona.server.pop3.core.factory;

import com.bona.server.pop3.api.Storage;
import com.bona.server.pop3.core.factory.StorageFactory;
import com.bona.server.pop3.core.storage.FileStorage;

/**
 * Created by bona on 2015/10/14.
 */
public class FileStorageFactory implements StorageFactory{
    @Override
    public Storage createStorage() {
        return new FileStorage();
    }
}
