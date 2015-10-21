package com.bona.server.pop3.core.filter;

import com.bona.server.pop3.api.RequestFilter;
import com.bona.server.pop3.api.SessionContext;
import com.bona.server.pop3.api.filter.DefaultRequestFilter;

/**
 * Created by bona on 2015/10/21.
 */
public class NotFilter extends DefaultRequestFilter {
    private RequestFilter filter;

    @Override
    public boolean doFilter(SessionContext context, String cmd, String argument) {
        return !filter.doFilter(context,cmd,argument);
    }

    public NotFilter(RequestFilter filter) {
        this.filter = filter;
    }
}
