package com.bona.server.pop3.core.storage;

import com.bona.server.pop3.api.Storage;
import com.bona.server.pop3.core.POP3Context;

import java.io.InputStream;

/**
 * Created by bona on 2015/10/13.
 */
public class DefaultStorage implements Storage {
    private POP3Context context;

    public long getSize(int index) {
        return 0;
    }

    @Override
    public long getSize() {
        return 0;
    }

    public int getCount() {
        return 0;
    }

    public InputStream openStream(int index) {
        return null;
    }

    public boolean delete(int index) {
        return false;
    }

    public String getState(int index) {
        return null;
    }

    public void commit() {

    }

    public void rollback() {

    }

    @Override
    public final void initStorage(POP3Context context) {
        this.context = context;
        onInitStorage();
    }

    @Override
    public void fetch() {

    }

    protected void onInitStorage(){

    }

    public final POP3Context getContext() {
        return context;
    }

    public final String getUserName(){
        return context.getUserName();
    }
}
