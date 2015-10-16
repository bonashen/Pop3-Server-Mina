package com.bona.server.pop3.core.filter;

import com.bona.server.pop3.api.RequestFilter;
import com.bona.server.pop3.api.SessionContext;

/**
 * Created by bona on 2015/10/10.
 */
public class OrFilter extends AndFilter {

    @Override
    public boolean doFilter(SessionContext context, String cmd, String argument) {
        for(RequestFilter filter:filters){
            if(filter.doFilter(context, cmd, argument)){
                return true;
            }
        }
        return false;
    }

    public OrFilter(RequestFilter... filters){
        super(filters);
    }
}
