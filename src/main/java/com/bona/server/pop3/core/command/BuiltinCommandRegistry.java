package com.bona.server.pop3.core.command;

import com.bona.server.pop3.core.command.impl.*;

/**
 * Created by bona on 2015/10/13.
 */
public enum BuiltinCommandRegistry {
    CAPA(CapaCommand.class),
    DELE(DeleCommand.class),
    LIST(ListCommand.class),
    NOOP(NoopCommand.class),
    PASS(PassCommand.class),
    USER(UserCommand.class),
    REST(RestCommand.class),
    RETR(RetrCommand.class),
    QUIT(QuitCommand.class),
    STAT(StatCommand.class),
    TOP(TopCommand.class),
    UIDL(UidlCommand.class);

    private static final Object LOCK = new Object();

    private final Class<? extends Command> aClass;
    private Command instance;

    private BuiltinCommandRegistry(Class<? extends Command> c) {
        try {
            this.aClass = c;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Command getCommand() {
        if (instance != null) return instance;
        synchronized (LOCK) {
            try {
                instance = aClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return instance;
        }
    }

    public Class<? extends Command> getCommandClass() {
        return this.aClass;
    }

}
