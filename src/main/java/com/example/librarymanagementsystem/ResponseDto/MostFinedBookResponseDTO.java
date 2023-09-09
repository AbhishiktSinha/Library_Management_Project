package com.example.librarymanagementsystem.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MostFinedBookResponseDTO {
    String bookTitle;
    int fine;
}
