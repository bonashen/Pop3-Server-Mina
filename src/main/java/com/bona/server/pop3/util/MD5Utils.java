package com.bona.server.pop3.util;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by bona on 2015/10/16.
 */
public class MD5Utils {

    static public String get(String value) throws IOException {
        return get(new ByteArrayInputStream(value.getBytes()));
    }

    static public String get(InputStream in) throws IOException {
        MessageDigest md5 = null;
        String value = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(StreamUtils.toByteArray(in));
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return value;
    }

    static public String get(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        try {
            return get(is);
        } finally {
            is.close();
        }
    }

}
