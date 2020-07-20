package org.seariver.kanbanboard.write;

import org.jboss.logging.Logger;
import org.seariver.kanbanboard.write.domain.application.Command;
import org.seariver.kanbanboard.write.domain.application.Handler;
import org.seariver.kanbanboard.write.observable.CommandEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;

@ApplicationScoped
public class CommandBus {

    final static Logger logger = Logger.getLogger(CommandBus.class);

    public void execute(Command command) {

        var event = new CommandEvent(command);

        try {
            handle(command);
        } catch (Exception exception) {
            event.setException(exception);
            throw exception;
        } finally {
            event.stopTimer();
        }
    }

    private void handle(Command command) {
        try {
            var handlerFqn = command.getClass().getCanonicalName().replace("Command", "Handler");
            var handlerClass = Class.forName(handlerFqn);
            Handler<Command> handler = (Handler) CDI.current().select(handlerClass).get();
            handler.handle(command);
        } catch (ClassNotFoundException exception) {
            logger.error(exception);
        }
    }
}
