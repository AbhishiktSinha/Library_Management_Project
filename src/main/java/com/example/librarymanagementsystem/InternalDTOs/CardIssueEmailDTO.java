package com.example.librarymanagementsystem.InternalDTOs;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CardIssueEmailDTO {
    private String receiverName;
    private String receiverEmail;
    private int cardId;
}
