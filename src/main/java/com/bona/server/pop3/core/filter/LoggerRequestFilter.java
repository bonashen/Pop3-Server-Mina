package com.bona.server.pop3.core.filter;

import com.bona.server.pop3.api.filter.DefaultRequestFilter;
import com.bona.server.pop3.core.POP3Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by bona on 2015/10/14.
 */
public class LoggerRequestFilter extends DefaultRequestFilter {
    private final static Logger LOG = LoggerFactory.getLogger(LoggerRequestFilter.class);

    @Override
    public boolean doFilter(POP3Context context, String cmd, String argument) {
        LOG.info("Remote Address:{} UserName:{} Command:{} Argument:{}",
                context.getSession().getRemoteAddress(), context.getUserName(), cmd, argument);
        return false;
    }
}
