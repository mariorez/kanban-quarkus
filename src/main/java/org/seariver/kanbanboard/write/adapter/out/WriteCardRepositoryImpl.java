package org.seariver.kanbanboard.write.adapter.out;

import org.seariver.kanbanboard.write.domain.core.Card;
import org.seariver.kanbanboard.write.domain.core.WriteCardRepository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.inject.Singleton;
import javax.sql.DataSource;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class WriteCardRepositoryImpl implements WriteCardRepository {

    public static final String BUCKET_ID_FIELD = "bucket_id";
    public static final String UUID_FIELD = "uuid";
    public static final String POSITION_FIELD = "position";
    public static final String NAME_FIELD = "name";
    public static final String CREATED_AT_FIELD = "created_at";
    public static final String UPDATED_AT_FIELD = "updated_at";

    private NamedParameterJdbcTemplate jdbcTemplate;

    public WriteCardRepositoryImpl(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void create(Card card) {
        var sql = "INSERT INTO card (bucket_id, uuid, position, name) values (:bucket_id, :uuid, :position, :name)";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
            .addValue(BUCKET_ID_FIELD, card.getBucketId())
            .addValue(UUID_FIELD, card.getUuid())
            .addValue(POSITION_FIELD, card.getPosition())
            .addValue(NAME_FIELD, card.getName());

        jdbcTemplate.update(sql, parameters);
    }

    @Override
    public Optional<Card> findByUuid(UUID uuid) {

        var sql = "SELECT bucket_id, uuid, position, name, created_at, updated_at FROM card WHERE uuid = :uuid";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
            .addValue(UUID_FIELD, uuid);

        return jdbcTemplate.query(sql, parameters, resultSet -> {

            if (resultSet.next()) {
                return Optional.of(new Card()
                    .setBucketId(resultSet.getLong(BUCKET_ID_FIELD))
                    .setUuid(UUID.fromString(resultSet.getString(UUID_FIELD)))
                    .setPosition(resultSet.getDouble(POSITION_FIELD))
                    .setName(resultSet.getString(NAME_FIELD))
                    .setCreatedAt(resultSet.getTimestamp(CREATED_AT_FIELD).toLocalDateTime())
                    .setUpdatedAt(resultSet.getTimestamp(UPDATED_AT_FIELD).toLocalDateTime())
                );
            }

            return Optional.empty();
        });
    }
}
