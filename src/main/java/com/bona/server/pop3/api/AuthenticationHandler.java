package com.bona.server.pop3.api;

/**
 * Created by bona on 2015/10/9.
 * <b/>
 * 用户认证接口，主要用于session中判断用户是否有效。
 */

public interface AuthenticationHandler {

    /**
     * 校验用户名与密码一致性
     * @param userName
     * 用户名
     * @param password
     * 用户密码
     * @return boolean
     */
    public boolean authUser(String userName, String password);

}
