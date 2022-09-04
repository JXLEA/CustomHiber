package com.edu.orm.session;

import com.edu.orm.session.util.EntityUtil;

public record KeyEntity(Class<?> type, Object id) {
    public static <T> KeyEntity of(T entity) {
        var idField = EntityUtil.getIdField(entity.getClass());
        return new KeyEntity(entity.getClass(), idField);
    }

    public static <T> KeyEntity of(Class<T> type, Object id) {
        return new KeyEntity(type, id);
    }
}