package com.bona.server.pop3.api;

import com.bona.server.pop3.core.POP3Context;

/**
 * Created by bona on 2015/10/10.
 */
public interface RequestFilter {
    /**
     * 过滤客户的请求命令
     *  @param context
     * @param cmd      请求命令
     * @param argument 请求命令的参数   @return boolean
     */
    public boolean doFilter(SessionContext context, String cmd, String argument);



}
