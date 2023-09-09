package com.example.librarymanagementsystem.Models;


import com.example.librarymanagementsystem.Enums.Department;
import com.example.librarymanagementsystem.Enums.Gender;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="student")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Student {

    @Id //Used for saying it's a primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rollNo;

    private String name;

    private Integer age;

    @Enumerated(value = EnumType.STRING)
    private Gender gender; //This gender variable is of user defined datatype : this contains only 2 values : MALE,FEMALE

    @Enumerated(value = EnumType.STRING)
    private Department department;

    @Column(unique = true)
    private String emailId;

    @OneToOne(mappedBy = "student",cascade = CascadeType.ALL)
    private LibraryCard libraryCard;
}
