package com.example.librarymanagementsystem.RequestDto;

import com.example.librarymanagementsystem.Enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.validation.annotation.Validated;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateAuthorDTO {

    private String originalName;
    private String originalEmailId;
    private String updatedName;
    private String updateEmailId;
    private Integer updatedAge;
    private Gender updatedGender;
}
