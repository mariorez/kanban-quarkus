package org.seariver.kanbanboard.write.observable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.seariver.kanbanboard.commom.observable.InternalEvent;
import org.seariver.kanbanboard.write.application.exception.WriteException;
import org.seariver.kanbanboard.write.application.service.Command;

import java.util.HashMap;
import java.util.Map;

public class CommandEvent extends InternalEvent {

    private final Command command;

    public CommandEvent(Command command) {
        startTimer();
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    @Override
    public Object getSource() {
        return getCommand();
    }

    @Override
    public String toJson() {

        var mapper = new ObjectMapper();
        Map<String, Object> message = new HashMap<>(Map.of("event", getOrigin()));
        message.put("content", getCommand());
        message.put("elapsedTimeInMilli", getElapsedTimeInMilli());

        try {
            if (hasError()) {
                message.put("message", getException().getMessage());

                if (getException() instanceof WriteException) {
                    var domainException = (WriteException) getException();
                    message.put("errors", domainException.getErrors().toString());
                }
            }

            return mapper.writeValueAsString(message);

        } catch (JsonProcessingException jsonException) {
            return String.format("%s - %s", command, jsonException);
        }
    }
}
