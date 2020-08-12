package org.seariver.kanbanboard.write.application.service;

public interface Handler<T extends Command> {

    void handle(T command);
}
