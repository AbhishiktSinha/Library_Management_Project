package com.example.librarymanagementsystem.Controllers;


import com.example.librarymanagementsystem.CustomExceptions.StudentNotFoundException;
import com.example.librarymanagementsystem.Enums.Department;
import com.example.librarymanagementsystem.Models.Student;
import com.example.librarymanagementsystem.RequestDto.AddStudent;
import com.example.librarymanagementsystem.RequestDto.UpdateStudentRequestDTO;
import com.example.librarymanagementsystem.ResponseDto.BookResponseStudent;
import com.example.librarymanagementsystem.Services.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/student")
@Slf4j
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/add")
    public ResponseEntity addStudent(@RequestBody AddStudent addStudent){

        try{
            String result = studentService.addStudent(addStudent);
            return new ResponseEntity(result,HttpStatus.CREATED);
        }catch (Exception e){
            log.error("Student not added successfully {}",e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/findDeptById")
    public ResponseEntity findDeptById(@RequestParam("Id")Integer Id){

        try{
            Department department = studentService.getDepartmentById(Id);
            return new ResponseEntity(department,HttpStatus.OK);
        }catch (Exception e){
            log.error("Department not found/Invalid Request {}",e.getMessage());
            return new ResponseEntity(null,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/findStudentsWithCardsNotActive")
    public ResponseEntity<List<String>> getStudentsNotActive() {
        List<String> list = studentService.studentListWithCardNotActive();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/student-who-has-read-most-distinct-books")
    public ResponseEntity<String> getStudentWhoHasReadMostBooks() {
        String name = studentService.findBookworm();
        return new ResponseEntity<>(name, HttpStatus.OK);
    }

    @GetMapping("/booksCurrentlyIssuedByStudent")
    public ResponseEntity<List<BookResponseStudent>> currentlyIssuedBooks(@RequestParam("id") Integer id) {
        try{
            List<BookResponseStudent> responseList = studentService.booksIssuedByStudent(id);
            return new ResponseEntity<>(responseList, HttpStatus.OK);
        } catch(Exception e) {
            log.error(e.getClass().toString() + " : " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateDetails")
    public ResponseEntity updateStudentDetails(@RequestBody UpdateStudentRequestDTO updateStudentRequestDTO) {
        try {
            studentService.updateStudentDetails(updateStudentRequestDTO);
            return new ResponseEntity("DETAILS UPDATED SUCCESSFULLY", HttpStatus.OK);
        } catch(StudentNotFoundException e) {
            log.error(e.getClass().toString()+" : "+e.getMessage());
            return new ResponseEntity("FAILURE", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch(Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity("FAILURE", HttpStatus.BAD_REQUEST);
        }
    }

}
