package org.seariver.kanbanboard.write.adapter.out;

import org.seariver.kanbanboard.write.domain.core.Bucket;
import org.seariver.kanbanboard.write.domain.core.WriteBucketRepository;
import org.seariver.kanbanboard.write.domain.exception.DuplicatedDataException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.inject.Singleton;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.seariver.kanbanboard.write.domain.exception.DomainException.Error.INVALID_DUPLICATED_DATA;

@Singleton
public class WriteBucketRepositoryImpl implements WriteBucketRepository {

    public static final String POSITION_FIELD = "position";
    public static final String EXTERNAL_ID = "external_id";
    public static final String NAME_FIELD = "name";

    private NamedParameterJdbcTemplate jdbcTemplate;

    public WriteBucketRepositoryImpl(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void create(Bucket bucket) {

        try {
            var sql = "INSERT INTO bucket(external_id, position, name) values (:external_id, :position, :name)";

            MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(EXTERNAL_ID, bucket.getExternalId())
                .addValue(POSITION_FIELD, bucket.getPosition())
                .addValue(NAME_FIELD, bucket.getName());

            jdbcTemplate.update(sql, parameters);

        } catch (DuplicateKeyException exception) {
            duplicatedKeyException(bucket.getExternalId(), bucket.getPosition(), exception);
        }
    }

    @Override
    public void update(Bucket bucket) {

        try {
            var sql = "UPDATE bucket SET position = :position, name =:name WHERE external_id = :external_id";

            MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(EXTERNAL_ID, bucket.getExternalId())
                .addValue(POSITION_FIELD, bucket.getPosition())
                .addValue(NAME_FIELD, bucket.getName());

            jdbcTemplate.update(sql, parameters);

        } catch (DuplicateKeyException exception) {
            duplicatedKeyException(null, bucket.getPosition(), exception);
        }
    }

    public Optional<Bucket> findByExternalId(UUID externalId) {

        var sql = "SELECT id, external_id, position, name, created_at, updated_at FROM bucket WHERE external_id = :external_id";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
            .addValue(EXTERNAL_ID, externalId);

        return jdbcTemplate.query(sql, parameters, resultSet -> {

            if (resultSet.next()) {
                return Optional.of(new Bucket()
                    .setId(resultSet.getLong("id"))
                    .setExternalId(UUID.fromString(resultSet.getString(EXTERNAL_ID)))
                    .setPosition(resultSet.getDouble(POSITION_FIELD))
                    .setName(resultSet.getString(NAME_FIELD))
                    .setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime())
                    .setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime())
                );
            }

            return Optional.empty();
        });
    }

    public List<Bucket> findByExternalIdOrPosition(UUID externalId, double position) {

        var sql = "SELECT id, external_id, position, name, created_at, updated_at FROM bucket WHERE external_id = :external_id OR position = :position";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
            .addValue(EXTERNAL_ID, externalId)
            .addValue(POSITION_FIELD, position);

        return jdbcTemplate.query(sql, parameters, (rs, rowNum) ->
            new Bucket()
                .setId(rs.getLong("id"))
                .setExternalId(UUID.fromString(rs.getString(EXTERNAL_ID)))
                .setPosition(rs.getDouble(POSITION_FIELD))
                .setName(rs.getString(NAME_FIELD))
                .setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime())
                .setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
        );
    }

    private void duplicatedKeyException(UUID externalId, double position, DuplicateKeyException exception) {

        var duplicatedDataException = new DuplicatedDataException(INVALID_DUPLICATED_DATA, exception);

        var existentBuckets = findByExternalIdOrPosition(externalId, position);

        existentBuckets.forEach(existentBucket -> {

            if (existentBucket.getExternalId().equals(externalId)) {
                duplicatedDataException.addError("id", externalId);
            }

            if (existentBucket.getPosition() == position) {
                duplicatedDataException.addError(POSITION_FIELD, position);
            }
        });

        throw duplicatedDataException;
    }
}
