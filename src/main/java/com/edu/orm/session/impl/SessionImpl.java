package com.edu.orm.session.impl;

import com.edu.orm.session.jdbc.JdbcEntityDao;
import com.edu.orm.session.Session;

import javax.sql.DataSource;

public class SessionImpl implements Session {

    private final JdbcEntityDao jdbcEntityDao;

    public SessionImpl(DataSource dataSource) {
        this.jdbcEntityDao = new JdbcEntityDao(dataSource);
    }

    @Override
    public <T> T findById(Class<T> type, Object id) {
        return jdbcEntityDao.findById(type, id);
    }

    @Override
    public void close() {
        jdbcEntityDao.close();
    }
}
