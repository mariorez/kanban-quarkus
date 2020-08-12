package org.seariver.kanbanboard.read.application.service;

public interface Resolver<T extends Query> {

    void resolve(T query);
}
