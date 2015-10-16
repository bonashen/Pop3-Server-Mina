package com.bona.server.pop3.core.storage;

import com.bona.server.pop3.api.SessionContext;
import com.bona.server.pop3.api.Storage;
import com.bona.server.pop3.util.MD5Utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by bona on 2015/10/13.
 */
public class DefaultStorage implements Storage {
    private SessionContext context;

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
        InputStream is = openStream(index);
        if(null==is){
            return null;
        }
        String state=null;
        try {
            state = "MD5:" + MD5Utils.get(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return state;
    }

    public void commit() {

    }

    public void rollback() {

    }

    @Override
    public final void initStorage(SessionContext context) {
        this.context = context;
        onInitStorage();
    }

    @Override
    public void fetch() {

    }

    protected void onInitStorage(){

    }

    public final SessionContext getContext() {
        return context;
    }

    public final String getUserName(){
        return context.getUserName();
    }
}
