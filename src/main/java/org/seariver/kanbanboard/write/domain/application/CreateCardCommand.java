package org.seariver.kanbanboard.write.domain.application;

import org.seariver.kanbanboard.commom.SelfValidating;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.UUID;

public class CreateCardCommand extends SelfValidating<CreateCardCommand> implements Command {

    @NotBlank
    @Pattern(regexp = UUID_FORMAT, message = INVALID_UUID)
    private final String bucketExternalId;
    @NotBlank
    @Pattern(regexp = UUID_FORMAT, message = INVALID_UUID)
    private final String cardExternalId;
    @Positive
    private final double position;
    @NotBlank
    @Size(min = 1, max = 100)
    private final String name;

    public CreateCardCommand(String bucketExternalId, String cardExternalId, double position, String name) {
        this.bucketExternalId = bucketExternalId;
        this.cardExternalId = cardExternalId;
        this.position = position;
        this.name = name;
        validateSelf();
    }

    public UUID getBucketExternalId() {
        return UUID.fromString(bucketExternalId);
    }

    public UUID getCardExternalId() {
        return UUID.fromString(cardExternalId);
    }

    public double getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }
}
