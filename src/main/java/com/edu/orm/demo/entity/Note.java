package com.edu.orm.demo.entity;

import com.edu.orm.annotation.Column;
import com.edu.orm.annotation.Id;
import com.edu.orm.annotation.ManyToOne;
import com.edu.orm.annotation.Table;
import lombok.Data;

@Data
@Table(name = "notes")
public class Note {

    @Id
    private long id;

    @Column(name = "body")
    private String body;

    @ManyToOne
    @Column(name = "author")
    private Person person;
}
