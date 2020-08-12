package org.seariver.kanbanboard.write.application.exception;

import helper.TestHelper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.seariver.kanbanboard.write.application.exception.WriteException.Error.BUCKET_NOT_EXIST;
import static org.seariver.kanbanboard.write.application.exception.WriteException.Error.INVALID_DUPLICATED_DATA;

@Tag("unit")
class WriteExceptionTest extends TestHelper {

    @Test
    void ALL_UseCaseException_MUST_ImplementsDomainException() {

        // given
        var duplicatedDataException = new DuplicatedDataException(INVALID_DUPLICATED_DATA, new RuntimeException());
        var bucketNotExistentException = new BucketNotExistentException(BUCKET_NOT_EXIST);

        // then
        assertThat(duplicatedDataException).isInstanceOf(WriteException.class);
        assertThat(bucketNotExistentException).isInstanceOf(WriteException.class);
    }
}