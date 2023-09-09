package com.example.librarymanagementsystem.ResponseDto;

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
public class BooksByGenreResponse {

    private String title;

    private Genre genre;

    private LocalDate publicationDate;

    private Integer price;

    private String authorName;


}
