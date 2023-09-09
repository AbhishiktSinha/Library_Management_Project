package com.example.librarymanagementsystem.RequestDto;

import com.example.librarymanagementsystem.Enums.Department;
import com.example.librarymanagementsystem.Enums.Gender;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UpdateStudentRequestDTO {

    private String originalName;
    private String originalEmailId;
    private String updatedName;
    private String updatedEmailId;
    private Department updateDepartment;
    private Integer updatedAge;
    private Gender updatedGender;
}
