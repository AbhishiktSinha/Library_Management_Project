package com.example.librarymanagementsystem.ResponseDto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BorrowerReturnDTO {
    private String studentName;
    private Integer studentId;
    private Integer studentLibraryCardId;
}
