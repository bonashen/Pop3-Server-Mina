package com.bona.server.pop3.api;

import com.bona.server.pop3.core.POP3Context;

import java.io.BufferedReader;
import java.io.InputStream;

/**
 * Created by bona on 2015/10/9.
 */
public interface Storage {
    /**
     * 获取用户UserName的邮箱收件箱第index邮件占用空间大小
     * @param index
     * @return int
     */
    long getSize(int index);

    /**
     * 获取用户UserName的邮箱收件箱总占用空间大小
     * @return int
     */
    long getSize();

    /**
     * 获取用户UserName的邮箱收件箱邮件数量
     * @return int
     */
    int getCount();

    /**
     * 打开用户UserName的第index的邮件流
     * @param index
     * @return InputStream
     */
    InputStream openStream(int index);

    /**
     * 删除用户UserName的第index的邮件
     * @param index 邮件号
     * @return boolean
     *  false  -  表示删除不成功
     *  true  - 表示删除成功
     */
    boolean delete(int index);

    /**
     * 查询用户第index的邮件状态
     * @param index
     * @return Boolean
     */
    public String getState(int index);

    /**
     * 确认删除邮件
     */
    void commit();

    /**
     * 恢复删除邮件
     */
    void rollback();

    /**
     * 设置会话上下文,并初始化存储
     * @param context
     */
    void initStorage(SessionContext context);

    /**
     * 提取当前用户的邮箱，设定与用户端的会话一致性
     */
    void fetch();
}
