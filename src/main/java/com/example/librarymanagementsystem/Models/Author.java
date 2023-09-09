package com.example.librarymanagementsystem.Models;


import com.example.librarymanagementsystem.Enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer authorId;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String emailId;

    private Integer age;

    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    @OneToMany(mappedBy = "author",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Book> bookList = new ArrayList<>();

}
