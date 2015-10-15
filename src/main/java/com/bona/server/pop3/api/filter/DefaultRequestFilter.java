package com.bona.server.pop3.api.filter;


import com.bona.server.pop3.api.RequestFilter;
import com.bona.server.pop3.core.POP3Context;

/**
 * Created by bona on 2015/10/10.
 */

public abstract class DefaultRequestFilter implements RequestFilter {
    public abstract boolean doFilter(POP3Context context, String cmd, String argument);
}
