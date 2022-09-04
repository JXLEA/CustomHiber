package com.edu.orm.factory;

import com.edu.orm.session.Session;
import com.edu.orm.session.impl.SessionImpl;
import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;

@RequiredArgsConstructor
public class SessionFactory {

    private final DataSource dataSource;

    public Session openSession() {
        return new SessionImpl(dataSource);
    }
}
