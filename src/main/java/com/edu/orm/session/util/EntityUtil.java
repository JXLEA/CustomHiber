package com.edu.orm.session.util;

import com.edu.orm.annotation.Column;
import com.edu.orm.annotation.Id;
import com.edu.orm.annotation.ManyToOne;
import com.edu.orm.annotation.OneToMany;
import com.edu.orm.annotation.Table;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public class EntityUtil {

    public static <T> String resolveTableName(Class<T> entityType) {
        return Optional.of(entityType.getDeclaredAnnotation(Table.class))
                .map(Table::name)
                .orElseThrow(() -> new RuntimeException("Can not find marker @Table for " + entityType));
    }

    @SneakyThrows
    public static <T> String resolveColumnName(Field field) {
        return Optional.ofNullable(field.getAnnotation(Column.class))
                .map(Column::name)
                .orElseGet(field::getName);
    }

    @SneakyThrows
    public static <T> Field getIdField(Class<T> entityType) {
        return Arrays.stream(entityType.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Can not find maker @Id for " + entityType));
    }


    public static boolean isRegularType(Field field) {
        return !isEntityType(field) && !isCollectionType(field);
    }

    public static boolean isEntityType(Field field) {
        return field.isAnnotationPresent(ManyToOne.class);
    }

    public static boolean isCollectionType(Field field) {
        return field.isAnnotationPresent(OneToMany.class);
    }

    public static Class<?> getCollectionEntityType(Field field) {
        var parameterizedType = (ParameterizedType) field.getGenericType();
        var typeArgs = parameterizedType.getActualTypeArguments();
        return (Class<?>) typeArgs[0];
    }

    public static Field getRelatedField(Class<?> fromEntity, Class<?> toEntity) {
        return Arrays.stream(toEntity.getDeclaredFields())
                .filter(field -> field.getType().equals(fromEntity))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Can not find a related fields in " + toEntity + " for " + fromEntity));
    }

    @SneakyThrows
    public static <T> Object getId(T entity) {
        var type = entity.getClass();
        var idField = getIdField(type);
        idField.setAccessible(Boolean.TRUE);
        return idField.get(entity);
    }

    @SneakyThrows
    public static Object retrieveValue(Field field, Object object) {
        field.setAccessible(Boolean.TRUE);
        return field.get(object);
    }

    public static <T> Field[] getSortedFields(Class<T> entity) {
        return Arrays.stream(entity.getDeclaredFields())
                .sorted(Comparator.comparing(Field::getName))
                .toArray(Field[]::new);
    }
}
