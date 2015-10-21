package com.bona.server.pop3.core.filter;



import com.bona.server.pop3.api.RequestFilter;
import com.bona.server.pop3.api.SessionContext;
import com.bona.server.pop3.api.filter.DefaultRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Created by bona on 2015/10/10.
 */
public class AuthFilter extends DefaultRequestFilter {
    private final static Logger LOG = LoggerFactory.getLogger(AuthFilter.class);

    private final RequestFilter authFilter = new RequestFilter() {
        public boolean doFilter(SessionContext context, String cmd, String argument) {
            return !context.isAuthorized();
        }
    };

    private final RequestFilter acceptFilter = new AcceptRequestFilter(Arrays.asList(new String[]{"USER", "PASS", "QUIT","CAPA","REST"}));

    @Override
    public boolean doFilter(SessionContext context, String cmd, String argument) {
        boolean ret = new AndFilter(authFilter,acceptFilter).doFilter(context, cmd, argument);
//        LOG.debug("check user state.{}",ret);
        return ret;
//        return authFilter.doFilter(cmd) && acceptFilter.doFilter(cmd);
    }

}
