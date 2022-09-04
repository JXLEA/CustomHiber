package com.edu.orm.entity.builder.impl;

import com.edu.orm.entity.builder.EntityBuilder;

import java.sql.ResultSet;

public class EntityBuilderImpl implements EntityBuilder {

    @Override
    public <T> T build(Class<T> entityType, ResultSet resultSet) {
        return null;
    }
}
