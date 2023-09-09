package com.example.librarymanagementsystem.ResponseDto;

import com.example.librarymanagementsystem.Enums.Genre;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BooksByAuthorResponse {

    private String title;
    private Genre genre;
    private LocalDate publicationDate;
    private Boolean isAvailable;


}
