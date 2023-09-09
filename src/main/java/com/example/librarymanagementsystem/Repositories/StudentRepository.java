package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.Models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student,Integer> {

    @Query(value = "select * from student where name =:name and email_id =:emailId", nativeQuery = true)
    Student findStudentByNameAndEmailId(@Param("name") String name,@Param("emailId") String emailId);
}
