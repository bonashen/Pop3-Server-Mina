package com.bona.server.pop3.core.factory;


import com.bona.server.pop3.core.command.Command;
import com.bona.server.pop3.core.command.BuiltinCommandRegistry;
import com.bona.server.pop3.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bona on 2015/10/10.
 */
public final class POP3ProtocolFactory implements POP3Factory {
    private final static Logger LOG = LoggerFactory.getLogger(POP3ProtocolFactory.class);

    private static final Object LOCK = new Object();
    private static POP3Factory _instance;
    private Map<String,Class<? extends Command>> commandMap = new HashMap<String, Class<? extends Command>>();

    public static final POP3Factory getInstance() {

        if (null != _instance) return _instance;

        synchronized (LOCK) {
            String factoryName = System.getProperty(Constants.PROTOCOL_POP3_FACTORY, POP3ProtocolFactory.class.getName());
            try {
                Class<?> factoryClass = Class.forName(factoryName);
                LOG.debug("Found POP3 protocol factory name:{}",factoryName);
                if (!POP3Factory.class.isAssignableFrom(factoryClass)) {
                    factoryClass = POP3ProtocolFactory.class;
                }
                _instance = (POP3Factory) factoryClass.newInstance();
                LOG.debug("Instance the {}",_instance.getClass().getName());
                return _instance;

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private POP3ProtocolFactory(){
        loadCommands();
    }

    protected void loadCommands(){
        for(BuiltinCommandRegistry entry :BuiltinCommandRegistry.values()){
            commandMap.put(entry.name(), (Class<Command>) entry.getCommandClass());
            LOG.debug("Added command {}",entry.name());
        }
    }

    public final Command createCommand(String name){

        Class<? extends Command> cmdClass = commandMap.get(name.toUpperCase());
        if(null!= cmdClass){
            try {
                LOG.debug("Created command :{}",cmdClass.getName());
                return cmdClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
