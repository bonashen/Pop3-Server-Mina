package com.bona.server.pop3.core.command;

import com.bona.server.pop3.POP3ServerConfig;
import com.bona.server.pop3.core.POP3Context;
import com.bona.server.pop3.core.filter.OrFilter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by bona on 2015/10/12.
 */
public class CommandHandler {
    private final static Logger LOG = LoggerFactory.getLogger(CommandHandler.class);

    private final POP3ServerConfig config;

    public CommandHandler(POP3ServerConfig cfg) {
        config = cfg;
        initCommands();
    }

    protected void initCommands() {

    }

    public boolean dispatchCommand(IoSession session, String cmd){

        LOG.debug("parse command text:{}",cmd);
        POP3Context context = POP3Context.getContextFromSession(session);
        int i = cmd.indexOf(" ");
        String cmdName,argument = null;
        cmdName = cmd;
        if(i>-1){
            cmdName = cmd.substring(0,i).trim().toUpperCase();
            argument = cmd.substring(i+1);
        }
        if (doFilters(context,cmdName,argument )) {
            context.getResponse().sendErrMessage("This session is not security!");
            context.getResponse().sendMessage(".");
            return cmd.trim().length()>0;
        }

        LOG.debug("will execute {} command.",cmdName);
        Command command = config.getPOP3Factory().createCommand(cmdName);
        if(null!=command){
            LOG.debug("found {} command=>{}",cmdName,command.getClass().getName());
            command.execute(context, argument);
        }else {
            context.getResponse().sendErrMessage("unknown "+cmdName+" command!");
        }
        return cmd.trim().length()>0;
    }

    protected boolean doFilters(POP3Context context,String cmd, String argument) {
        LOG.debug("execute {} filter(s).",context.getConfig().getFilters().size());
        OrFilter filter= new OrFilter();
        filter.getFilters().addAll(context.getConfig().getFilters());
        return filter.doFilter(context,cmd, argument);
    }
}
