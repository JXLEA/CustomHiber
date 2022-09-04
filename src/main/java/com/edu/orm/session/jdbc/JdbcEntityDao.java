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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.edu.orm.session.jdbc.JdbcQuery.SELECT_BY;
import static com.edu.orm.session.util.EntityUtil.resolveColumnName;
import static com.edu.orm.session.util.EntityUtil.resolveTableName;

@RequiredArgsConstructor
public class JdbcEntityDao {
    private final DataSource dataSource;
    private Map<KeyEntity, Object> sessionCache = new HashMap<>();

    @SneakyThrows
    public <T> T findById(Class<T> entityType, Object id) {
        var key = KeyEntity.of(entityType, id);
        if (sessionCache.containsKey(key)) {
            return (T) sessionCache.get(key);
        }
        var idField = EntityUtil.getIdField(entityType);
        return findBy(entityType, idField, id);
    }

    @SneakyThrows
    public <T> T findBy(Class<T> entityType, Field field, Object value) {
        try (var connection = dataSource.getConnection()) {
            var tableName = resolveTableName(entityType);
            var column = resolveColumnName(field);
            try (var statement = connection.prepareStatement(String.format(SELECT_BY.getValue(), tableName, column))) {
                statement.setObject(1, value);
                var resultSet = statement.executeQuery();
                resultSet.next();
                return createEntityFromResultSet(entityType, resultSet);
            }
        }
    }

    @SneakyThrows
    private <T> List<T> findAllBy(Class<T> type, Field field, Object value) {
        var result = new ArrayList<T>();
        try (var connection = dataSource.getConnection()) {
            var tableName = resolveTableName(type);
            var columnName = resolveColumnName(field);
            var selectSql = String.format(SELECT_BY.getValue(), tableName, columnName);
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
        for (var field : entityType.getDeclaredFields()) {
            field.setAccessible(Boolean.TRUE);
            if (EntityUtil.isRegularType(field)) {
                var columnName = resolveColumnName(field);
                var result = resultSet.getObject(columnName);
                field.set(entity, result);
            } else if (EntityUtil.isEntityType(field)) {
                var relatedEntityType = field.getType();
                var joinColumnName = resolveColumnName(field);
                var joinColumnValue = resultSet.getObject(joinColumnName);
                var relatedEntityIdField = EntityUtil.getIdField(relatedEntityType);
                var relatedEntity = findBy(relatedEntityType, relatedEntityIdField, joinColumnValue);
                field.set(entity, relatedEntity);
            } else if (EntityUtil.isCollectionType(field)) {
                var collectionEntityType = EntityUtil.getCollectionEntityType(field);
                var relatedEntityField = EntityUtil.getRelatedField(entityType, collectionEntityType);
                var entityId = EntityUtil.getId(entity);
                var collection = new LazyList<>(() -> findAllBy(collectionEntityType, relatedEntityField, entityId));
                field.set(entity, collection);
            }
        }
        return cache(entity);
    }

    private <T> T cache(T entity) {
        var key = KeyEntity.of(entity);
        return (T) sessionCache.computeIfAbsent(key, keyEntity -> entity);
    }

    public void close() {
        sessionCache.clear();
    }
}
