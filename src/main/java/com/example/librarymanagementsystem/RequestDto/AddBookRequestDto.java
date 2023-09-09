package com.example.librarymanagementsystem.RequestDto;

import com.example.librarymanagementsystem.Enums.Genre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddBookRequestDto {

    private String title;
    private Integer authorId;
    private Genre genre;
    private LocalDate publicationDate;
    private Integer price;

}
