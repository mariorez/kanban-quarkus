package org.seariver.kanbanboard.write.domain.application;

import org.seariver.kanbanboard.commom.SelfValidating;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.UUID;

public class CreateBucketCommand extends SelfValidating<CreateBucketCommand> implements Command {

    @NotBlank
    @Pattern(regexp = UUID_FORMAT, message = INVALID_UUID)
    private final String bucketExternalId;
    @Positive
    private final double position;
    @NotBlank
    @Size(min = 1, max = 100)
    private final String name;

    public CreateBucketCommand(String bucketExternalId, double position, String name) {
        this.bucketExternalId = bucketExternalId;
        this.position = position;
        this.name = name;
        validateSelf();
    }

    public UUID getBucketExternalId() {
        return UUID.fromString(bucketExternalId);
    }

    public double getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }
}
