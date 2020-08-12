package org.seariver.kanbanboard.read.application.service;

import org.seariver.kanbanboard.read.application.domain.BucketDto;

import java.util.List;

public class ListAllBucketQuery implements Query {

    private List<BucketDto> result;

    public List<BucketDto> getResult() {
        return result;
    }

    public void setResult(List<BucketDto> result) {
        this.result = result;
    }
}
