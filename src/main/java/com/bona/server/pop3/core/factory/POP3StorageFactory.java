package com.bona.server.pop3.core.factory;

import com.bona.server.pop3.api.Storage;
import com.bona.server.pop3.core.storage.DefaultStorage;
import com.bona.server.pop3.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by bona on 2015/10/14.
 */
public class POP3StorageFactory implements StorageFactory {
    private static final Logger LOG = LoggerFactory.getLogger(POP3StorageFactory.class);

    private static final Object LOCK = new Object();
    private static StorageFactory _instance =null;

    @Override
    public Storage createStorage() {
        return new DefaultStorage();
    }

    public static final StorageFactory getInstance() {

        if (null != _instance) return _instance;

        synchronized (LOCK) {
            String factoryName = System.getProperty(Constants.PROTOCOL_POP3_STORAGE_FACTORY,POP3StorageFactory.class.getName() );
            try {
                LOG.debug("Found Storage factory name:{}",factoryName);
                Class<?> factoryClass = Class.forName(factoryName);
                if (!StorageFactory.class.isAssignableFrom(factoryClass)) {
                    factoryClass = POP3StorageFactory.class;
                }
                _instance = (StorageFactory) factoryClass.newInstance();
                LOG.debug("Instance the {}",_instance.getClass().getName());

                return _instance;

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
