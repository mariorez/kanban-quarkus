package org.seariver.kanbanboard.write.application.service;

import org.seariver.kanbanboard.commom.SelfValidating;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.UUID;

public class MoveBucketCommand extends SelfValidating<MoveBucketCommand> implements Command {

    @NotBlank
    @Pattern(regexp = UUID_FORMAT, message = INVALID_UUID)
    private final String bucketExternalId;
    @Positive
    private final double position;

    public MoveBucketCommand(String bucketExternalId, double position) {
        this.bucketExternalId = bucketExternalId;
        this.position = position;
        validateSelf();
    }

    public UUID getBucketExternalId() {
        return UUID.fromString(bucketExternalId);
    }

    public double getPosition() {
        return position;
    }
}
