package com.example.librarymanagementsystem.RequestDto;

import com.example.librarymanagementsystem.Enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddAuthorDTO {
    private String name;
    private String emailId;
    private Integer age;
    private Gender gender;
}
