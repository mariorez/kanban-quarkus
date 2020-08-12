package org.seariver.kanbanboard.read.application.service;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.seariver.kanbanboard.read.application.domain.BucketDto;
import org.seariver.kanbanboard.read.application.domain.CardDto;
import org.seariver.kanbanboard.read.application.domain.ReadBucketRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
public class ListAllBucketResolverTest {

    @Test
    void WHEN_QueryForAllBucket_MUST_RetrieveAllResults() {

        // given
        var bucketDto = new BucketDto(UUID.randomUUID(), 1.2, "TODO");
        bucketDto.addCard(new CardDto(UUID.randomUUID(), 2.1, "My Task"));
        var queryResult = List.of(bucketDto);
        var repository = mock(ReadBucketRepository.class);
        when(repository.findAll()).thenReturn(queryResult);
        var query = new ListAllBucketQuery();

        // when
        ListAllBucketResolver resolver = new ListAllBucketResolver(repository);
        resolver.resolve(query);

        // then
        verify(repository).findAll();
        assertThat(query.getResult()).isEqualTo(queryResult);
    }

    @Test
    void WHEN_BucketNotExists_MUST_ReturnEmptyList() {

        // given
        var repository = mock(ReadBucketRepository.class);
        when(repository.findAll()).thenReturn(Collections.emptyList());
        var query = new ListAllBucketQuery();

        // when
        ListAllBucketResolver resolver = new ListAllBucketResolver(repository);
        resolver.resolve(query);

        // then
        verify(repository).findAll();
        assertThat(query.getResult()).isEmpty();
    }
}
