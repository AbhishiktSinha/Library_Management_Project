package com.example.librarymanagementsystem.InternalDTOs;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookIssueEmailDTO {
    private String receiverName;
    private String bookName;
    private String authorName;
    private int cardId;
    private LocalDate issueDate;
    private int currentBooksIssued;

}
