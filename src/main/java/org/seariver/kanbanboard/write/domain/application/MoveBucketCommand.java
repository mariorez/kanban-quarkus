package org.seariver.kanbanboard.write.domain.application;

import org.seariver.kanbanboard.commom.SelfValidating;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.UUID;

public class MoveBucketCommand extends SelfValidating<MoveBucketCommand> implements Command {

    @NotBlank
    @Pattern(regexp = UUID_FORMAT, message = INVALID_UUID)
    private final String externalId;
    @Positive
    private final double position;

    public MoveBucketCommand(String externalId, double position) {
        this.externalId = externalId;
        this.position = position;
        validateSelf();
    }

    public UUID getExternalId() {
        return UUID.fromString(externalId);
    }

    public double getPosition() {
        return position;
    }
}
