package com.edu.orm.demo.entity;

import com.edu.orm.annotation.Column;
import com.edu.orm.annotation.Id;
import com.edu.orm.annotation.OneToMany;
import com.edu.orm.annotation.Table;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Table(name = "persons")
@ToString(exclude = "notes")
public class Person {

    @Id
    private long id;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    private long age;

    @OneToMany(mappedBy = "author")
    private List<Note> notes;
}
