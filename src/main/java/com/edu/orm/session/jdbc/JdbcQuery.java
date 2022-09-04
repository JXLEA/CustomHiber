package com.edu.orm.session.jdbc;

public enum JdbcQuery {

    SELECT_BY("select * from %s where %s = ?"),
    UPDATE("update %s set %s=? where %s=?");
    private final String value;

    JdbcQuery(String query) {
        this.value = query;
    }

    public String getValue() {
        return value;
    }
}
