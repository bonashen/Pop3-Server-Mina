package com.bona.server.pop3.core.filter;

import com.bona.server.pop3.api.filter.DefaultRequestFilter;
import com.bona.server.pop3.core.POP3Context;

import java.util.List;

/**
 * Created by bona on 2015/10/10.
 */
public class AcceptRequestFilter extends DefaultRequestFilter {
    private final List<String> accepts;

    @Override
    public boolean doFilter(POP3Context context, String cmd, String argument) {
        return !accepts.contains(cmd.toUpperCase().trim());
    }

    public AcceptRequestFilter(List<String> accepts){
        this.accepts = accepts;
    }

}
