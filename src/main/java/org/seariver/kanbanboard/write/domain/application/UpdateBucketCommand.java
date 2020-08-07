package org.seariver.kanbanboard.write.domain.application;

import org.seariver.kanbanboard.commom.SelfValidating;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.UUID;

public class UpdateBucketCommand extends SelfValidating<UpdateBucketCommand> implements Command {

    @NotBlank
    @Pattern(regexp = UUID_FORMAT, message = INVALID_UUID)
    private final String bucketExternalId;
    @NotBlank
    @Size(min = 1, max = 100)
    private final String name;

    public UpdateBucketCommand(String bucketExternalId, String name) {
        this.bucketExternalId = bucketExternalId;
        this.name = name;
        validateSelf();
    }

    public UUID getBucketExternalId() {
        return UUID.fromString(bucketExternalId);
    }

    public String getName() {
        return name;
    }
}
