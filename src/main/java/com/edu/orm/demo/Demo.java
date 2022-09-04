package com.edu.orm.demo;

import com.edu.orm.demo.entity.Note;
import com.edu.orm.demo.entity.Person;
import com.edu.orm.factory.SessionFactory;
import lombok.SneakyThrows;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public class Demo {

    public static void main(String[] args) {
        var dataSource = initializeDataSource();
        var sessionFactory = new SessionFactory(dataSource);
        var session = sessionFactory.openSession();

        var person = session.findById(Person.class, 1L);
        var note = session.findById(Note.class, 1L);

        System.out.println(person);
        System.out.println(note);

        System.out.println(person.getNotes());

    }

    @SneakyThrows
    public static DataSource initializeDataSource() {
        PGSimpleDataSource pgSimpleDataSource = new PGSimpleDataSource();
        pgSimpleDataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
        return pgSimpleDataSource;
    }

}
