package com.bona.server.pop3.core.factory;

import com.bona.server.pop3.api.Storage;

/**
 * Created by bona on 2015/10/14.
 */
public interface StorageFactory {
    public Storage createStorage();
}

