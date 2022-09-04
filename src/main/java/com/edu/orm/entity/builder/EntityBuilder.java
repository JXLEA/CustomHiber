package com.edu.orm.entity.builder;

import java.sql.ResultSet;

public interface EntityBuilder {

    <T> T build(Class<T> entityType, ResultSet resultSet);
}
