package org.seariver.kanbanboard.read.observable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.seariver.kanbanboard.commom.observable.InternalEvent;
import org.seariver.kanbanboard.read.domain.application.Query;
import org.seariver.kanbanboard.write.domain.exception.WriteException;

import java.util.HashMap;
import java.util.Map;

public class QueryEvent extends InternalEvent {

    private final Query query;

    public QueryEvent(Query query) {
        this.query = query;
    }

    public Query getQuery() {
        return query;
    }

    @Override
    public Object getSource() {
        return getQuery();
    }

    @Override
    public String toJson() {

        var mapper = new ObjectMapper();
        Map<String, Object> message = new HashMap<>(Map.of("event", getOrigin()));
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
            return String.format("%s - %s", getOrigin(), jsonException);
        }
    }
}
