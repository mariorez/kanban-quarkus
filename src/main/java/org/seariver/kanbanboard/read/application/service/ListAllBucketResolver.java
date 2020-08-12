package org.seariver.kanbanboard.read.application.service;

import org.seariver.kanbanboard.read.application.domain.BucketDto;
import org.seariver.kanbanboard.read.application.domain.ReadBucketRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.List;

@Named
@ApplicationScoped
public class ListAllBucketResolver implements Resolver<ListAllBucketQuery> {

    private final ReadBucketRepository repository;

    public ListAllBucketResolver(ReadBucketRepository repository) {
        this.repository = repository;
    }

    @Override
    public void resolve(ListAllBucketQuery query) {

        List<BucketDto> result = repository.findAll();

        query.setResult(result);
    }
}
