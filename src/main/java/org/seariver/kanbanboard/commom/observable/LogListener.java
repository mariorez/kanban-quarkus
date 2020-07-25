package org.seariver.kanbanboard.commom.observable;

import org.jboss.logging.Logger;
import org.seariver.kanbanboard.commom.exception.DomainException;

import javax.enterprise.event.Observes;
import javax.inject.Named;

@Named
public class LogListener {

    final static Logger logger = Logger.getLogger(LogListener.class);

    public void onEventOccur(@Observes InternalEvent internalEvent) {

        if (internalEvent.isSuccess()) {
            logger.infov("Test: {0}", internalEvent.getOrigin());
        } else if (internalEvent.getException() instanceof DomainException) {
            logger.warnv("Test: {0}", internalEvent.getOrigin(), internalEvent.getException());
        } else {
            logger.errorv("Test: {0}", internalEvent.getOrigin(), internalEvent.getException());
        }
    }
}
