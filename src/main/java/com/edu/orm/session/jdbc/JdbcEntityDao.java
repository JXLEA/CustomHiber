package com.edu.orm.session.jdbc;

import com.edu.orm.collection.LazyList;
import com.edu.orm.session.KeyEntity;
import com.edu.orm.session.util.EntityUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.edu.orm.session.jdbc.JdbcQuery.SELECT_BY;
import static com.edu.orm.session.util.EntityUtil.getSortedFields;
import static com.edu.orm.session.util.EntityUtil.resolveColumnName;
import static com.edu.orm.session.util.EntityUtil.resolveTableName;
import static com.edu.orm.session.util.EntityUtil.retrieveValue;

//TODO perform refactoring during ActionQueue implementation
@RequiredArgsConstructor
public class JdbcEntityDao {
    private final DataSource dataSource;
    private Map<KeyEntity, Object> sessionCache = new HashMap<>();
    private Map<KeyEntity, Object[]> entitySnapshotCopies = new HashMap<>();

    @SneakyThrows
    public <T> T findById(Class<T> entityType, Object id) {
        var key = KeyEntity.of(entityType, id);
        if (sessionCache.containsKey(key)) {
            return entityType.cast(sessionCache.get(key));
        }
        var idField = EntityUtil.getIdField(entityType);
        return findBy(entityType, idField, id);
    }

    @SneakyThrows
    public <T> T findBy(Class<T> entityType, Field field, Object value) {
        try (var connection = dataSource.getConnection()) {
            var tableName = resolveTableName(entityType);
            var column = resolveColumnName(field);
            var query = prepareQuery(tableName, column);
            try (var statement = connection.prepareStatement(query)) {
                statement.setObject(1, value);
                var resultSet = statement.executeQuery();
                resultSet.next();
                return createEntityFromResultSet(entityType, resultSet);
            }
        }
    }

    private static String prepareQuery(String tableName, String column) {
        return String.format(SELECT_BY.getValue(), tableName, column);
    }

    @SneakyThrows
    private <T> List<T> findAllBy(Class<T> type, Field field, Object value) {
        var result = new ArrayList<T>();
        try (var connection = dataSource.getConnection()) {
            var tableName = resolveTableName(type);
            var columnName = resolveColumnName(field);
            var selectSql = prepareQuery(tableName, columnName);
            try (var statement = connection.prepareStatement(selectSql)) {
                statement.setObject(1, value);
                var resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    result.add(createEntityFromResultSet(type, resultSet));
                }
            }
        }
        return result;
    }

    @SneakyThrows
    private <T> T createEntityFromResultSet(Class<T> entityType, ResultSet resultSet) {
        var entity = entityType.getDeclaredConstructor().newInstance();

        var declaredFields = getSortedFields(entityType);
        var snapshotCopy = new Object[declaredFields.length];

        for (int i = 0; i < declaredFields.length; i++) {

            var field = declaredFields[i];
            field.setAccessible(Boolean.TRUE);


            if (EntityUtil.isRegularType(field)) {
                var columnName = resolveColumnName(field);
                var value = resultSet.getObject(columnName);

                field.set(entity, value);

                snapshotCopy[i] = field.get(entity);
            }


            else if (EntityUtil.isEntityType(field)) {
                var joinColumnName = resolveColumnName(field);
                var joinColumnValue = resultSet.getObject(joinColumnName);

                var relatedEntityType = field.getType();
                var relatedEntityIdField = EntityUtil.getIdField(relatedEntityType);

                var relatedEntity = findBy(relatedEntityType, relatedEntityIdField, joinColumnValue);
                field.set(entity, relatedEntity);
            }


            else if (EntityUtil.isCollectionType(field)) {
                var relatedEntityType = EntityUtil.getCollectionEntityType(field);
                var relatedEntityField = EntityUtil.getRelatedField(entityType, relatedEntityType);

                var collection = new LazyList<>(() ->
                        findAllBy(relatedEntityType, relatedEntityField, EntityUtil.getId(entity)));
                field.set(entity, collection);
            }


        }
        var key = KeyEntity.of(entity);
        var copy = entitySnapshotCopies.put(key, snapshotCopy);
        System.out.println("SnapshotCopy: " + Arrays.toString(copy));
        return cache(entity);
    }

    private <T> T cache(T entity) {
        var key = KeyEntity.of(entity);
        return (T) sessionCache.computeIfAbsent(key, keyEntity -> entity);
    }

    public void close() {
        sessionCache.entrySet().stream()
                .filter(this::hasChanged)
                .forEach(this::performUpdate);
    }

    private boolean hasChanged(Map.Entry<KeyEntity, Object> keyEntityObjectEntry) {
        var snapshotValues = entitySnapshotCopies.get(keyEntityObjectEntry.getKey());
        var type = keyEntityObjectEntry.getValue().getClass();
        var fields = getSortedFields(type);

        for (int i = 0; i < fields.length; i++) {
            if (retrieveValue(fields[i], keyEntityObjectEntry.getValue()) != snapshotValues[i]) {
                return true;
            }
        }
        return false;
    }

    @SneakyThrows
    private void performUpdate(Map.Entry<KeyEntity, Object> keyEntityObjectEntry) {
        var key = keyEntityObjectEntry.getKey();
        var entity = keyEntityObjectEntry.getValue();
        try (var connection = dataSource.getConnection()) {
            var sql = prepareUpdateQuery(key.type(), entity, EntityUtil.getIdField(key.type()), key.id());
            try (var statement = connection.prepareStatement(sql)) {
                statement.executeUpdate();
            }
        }
    }

    public <T> String prepareUpdateQuery(Class<T> entityType, Object entity, Field field, Object value) {
        var query = new StringBuilder("update ")
                .append(EntityUtil.resolveTableName(entityType)).append(" set ");
        Arrays.stream(entityType.getDeclaredFields())
                .filter(EntityUtil::isRegularType)
                .forEach(f -> query.append(EntityUtil.resolveColumnName(f))
                        .append("=")
                        .append((f.getType() == String.class) ? "'" : "")
                        .append(retrieveValue(f, entity))
                        .append((f.getType() == String.class) ? "'" : "")
                        .append(","));
        query.deleteCharAt(query.lastIndexOf(","))
                .append(" where ").append(EntityUtil.resolveColumnName(field)).append("=").append(value);
        return query.toString();
    }


}
