package org.seariver.kanbanboard.write.application.service;

import org.seariver.kanbanboard.commom.SelfValidating;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.UUID;

public class UpdateCardCommand extends SelfValidating<UpdateCardCommand> implements Command {

    @NotBlank
    @Pattern(regexp = UUID_FORMAT, message = INVALID_UUID)
    private final String cardExternalId;
    @NotBlank
    @Size(min = 1, max = 100)
    private final String name;
    private final String description;

    public UpdateCardCommand(String cardExternalId, String name, String description) {
        this.cardExternalId = cardExternalId;
        this.name = name;
        this.description = description;
        validateSelf();
    }

    public UUID getCardExternalId() {
        return UUID.fromString(cardExternalId);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
