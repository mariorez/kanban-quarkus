package org.seariver.kanbanboard.commom.observable;

import org.jboss.logging.Logger;
import org.seariver.kanbanboard.commom.exception.ServiceBusInvalidObjectException;
import org.seariver.kanbanboard.read.domain.application.Query;
import org.seariver.kanbanboard.read.domain.application.Resolver;
import org.seariver.kanbanboard.read.observable.QueryEvent;
import org.seariver.kanbanboard.write.domain.application.Command;
import org.seariver.kanbanboard.write.domain.application.Handler;
import org.seariver.kanbanboard.write.observable.CommandEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.CDI;

@ApplicationScoped
public class ServiceBus {

    final static Logger logger = Logger.getLogger(ServiceBus.class);
    private Event<InternalEvent> eventPublisher;

    public ServiceBus(Event<InternalEvent> eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void execute(Command command) {
        var event = new CommandEvent(command);
        execute(event);
    }

    public void execute(Query query) {
        var event = new QueryEvent(query);
        execute(event);
    }

    private void execute(InternalEvent event) {

        try {
            run(event);
        } catch (Exception exception) {
            event.setException(exception);
            throw exception;
        } finally {
            event.stopTimer();
            eventPublisher.fire(event);
        }
    }

    private void run(InternalEvent event) {

        try {
            switch (event.getType()) {
                case COMMAND:
                    var handlerFqn = event.getOrigin().replace("Command", "Handler");
                    var handlerClass = Class.forName(handlerFqn);
                    Handler<Command> handler = (Handler) CDI.current().select(handlerClass).get();
                    handler.handle((Command) event.getSource());
                    break;
                case QUERY:
                    var resolverFqn = event.getOrigin().replace("Query", "Resolver");
                    var resolverClass = Class.forName(resolverFqn);
                    Resolver<Query> resolver = (Resolver) CDI.current().select(resolverClass).get();
                    resolver.resolve((Query) event.getSource());
                    break;
                default:
                    throw new ServiceBusInvalidObjectException(event);
            }
        } catch (ClassNotFoundException exception) {
            throw new ServiceBusInvalidObjectException(event, exception);
        }
    }
}
