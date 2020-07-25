package org.seariver.kanbanboard.commom.exception;

import org.seariver.kanbanboard.commom.observable.InternalEvent;

public class ServiceBusInvalidObjectException extends RuntimeException {

    public static final String ERROR_MESSAGE = "ServiceBus does not recognizes Object of type: %s";

    public ServiceBusInvalidObjectException(InternalEvent event) {
        super(String.format(ERROR_MESSAGE, event.getSource().getClass().getCanonicalName()));
    }

    public ServiceBusInvalidObjectException(InternalEvent event, Exception exception) {
        super(String.format(ERROR_MESSAGE, event.getSource().getClass().getCanonicalName()), exception);
    }
}
