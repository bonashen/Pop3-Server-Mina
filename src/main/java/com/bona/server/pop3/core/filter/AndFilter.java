package com.bona.server.pop3.core.filter;


import com.bona.server.pop3.api.RequestFilter;
import com.bona.server.pop3.api.SessionContext;
import com.bona.server.pop3.api.filter.DefaultRequestFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bona on 2015/10/10.
 */
public class AndFilter extends DefaultRequestFilter {
    protected final List<RequestFilter> filters=new ArrayList<RequestFilter>();

    @Override
    public boolean doFilter(SessionContext context, String cmd, String argument) {
        int count=0;
        for(RequestFilter filter:filters){
            if(filter.doFilter(context, cmd, argument)){
                count++;
            }else{
                return false;
            }
        }
        return count==filters.size();
    }

    public AndFilter(RequestFilter... filters){

        this.filters.addAll(Arrays.asList(filters));
    }

    public List<RequestFilter> getFilters() {
        return filters;
    }

    public void addFilter(RequestFilter filter){
        if(!filters.contains(filter))filters.add(filter);
    }

    public void removeFilter(RequestFilter filter){
        filters.remove(filter);
    }
}
