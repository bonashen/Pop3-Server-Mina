package com.bona.server.pop3.core.filter;

import com.bona.server.pop3.api.SessionContext;
import com.bona.server.pop3.api.filter.DefaultRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by bona on 2015/10/14.
 */
public class LoggerRequestFilter extends DefaultRequestFilter {
    private final static Logger LOG = LoggerFactory.getLogger(LoggerRequestFilter.class);

    @Override
    public boolean doFilter(SessionContext context, String cmd, String argument) {
        LOG.info("Remote Address:{} UserName:{} Command:{} Argument:{}",
                context.getRemoteAddress(), context.getUserName(), cmd, argument);
        return false;
    }
}
