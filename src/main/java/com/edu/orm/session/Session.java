package com.edu.orm.session;

public interface Session {

    <T> T findById(Class<T> type, Object id);

    void close();
}
