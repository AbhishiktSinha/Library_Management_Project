package com.example.librarymanagementsystem.InternalDTOs;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReturnBookEmailDTO {
    private String bookName;
    private int bookId;
    private String receiverName;
    private String receiverEmail;
    private int fineAmt;
    private LocalDate issueDate;
    private LocalDate returnDate;
}
