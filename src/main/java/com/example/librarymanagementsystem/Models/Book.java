package com.example.librarymanagementsystem.Models;


import com.example.librarymanagementsystem.Enums.Genre;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookId;

    @Column(unique = true)
    private String title;

    private Boolean isAvailable;

    @Enumerated(value = EnumType.STRING)
    private Genre genre;

    private LocalDate publicationDate;

    private Integer price;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private Author author;

    @OneToMany(mappedBy = "book",cascade = CascadeType.ALL)
    private List<Transaction> transactionList = new ArrayList<>();

}
