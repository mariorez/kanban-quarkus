package org.seariver.kanbanboard.read.application;

import org.seariver.kanbanboard.read.domain.application.Resolver;
import org.seariver.kanbanboard.read.domain.core.BucketDto;
import org.seariver.kanbanboard.read.domain.core.ReadBucketRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.List;

@Named
@ApplicationScoped
public class ListAllBucketResolver implements Resolver<ListAllBucketQuery> {

    private ReadBucketRepository repository;

    public ListAllBucketResolver(ReadBucketRepository repository) {
        this.repository = repository;
    }

    @Override
    public void resolve(ListAllBucketQuery query) {

        List<BucketDto> result = repository.findAll();

        query.setResult(result);
    }
}
