package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.CustomExceptions.StudentNotFoundException;
import com.example.librarymanagementsystem.Enums.Department;
import com.example.librarymanagementsystem.Enums.TransactionStatus;
import com.example.librarymanagementsystem.Enums.TransactionType;
import com.example.librarymanagementsystem.Models.*;
import com.example.librarymanagementsystem.Repositories.CardRepository;
import com.example.librarymanagementsystem.Repositories.StudentRepository;
import com.example.librarymanagementsystem.Repositories.TransactionRepository;
import com.example.librarymanagementsystem.RequestDto.AddStudent;
import com.example.librarymanagementsystem.RequestDto.UpdateStudentRequestDTO;
import com.example.librarymanagementsystem.ResponseDto.BookResponseStudent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CardRepository cardRepository1;

    @Autowired
    private TransactionRepository transactionRepository2;

    public String addStudent(AddStudent addStudent) throws Exception{

        Student student = Student.builder().name(addStudent.getName()).
                emailId(addStudent.getEmailId()).age(addStudent.getAge()).
                department(addStudent.getDepartment()).gender(addStudent.getGender()).
                build();

        try{
            //exception in case of duplicat email Id
            studentRepository.save(student);
            return "SUCCESS";
        } catch (Exception e) {
            throw e;
        }
    }

    public Department getDepartmentById(Integer rollNo)throws Exception{

        Optional<Student> optionalStudent = studentRepository.findById(rollNo);

        if(!optionalStudent.isPresent()) {
            throw new Exception("Roll No Entered is Invalid");
        }
        Student student = optionalStudent.get();

        return student.getDepartment();
    }

    public List<String> studentListWithCardNotActive() {
        List<Integer> idList = cardRepository1.studentIdWithCardNotActive();
        List<String> list = new ArrayList<>();

        for(int id : idList) {
            Optional<Student> optionalStudent = studentRepository.findById(id);
            if(optionalStudent.isPresent()) {
                String studentName = optionalStudent.get().getName();
                list.add(studentName);
            }
        }
        return list;
    }

    public String findBookworm() {
        List<Transaction> transactionList = transactionRepository2.findAll();

        Map<Integer, HashSet<Integer>> student_book_map = new HashMap<>();

        for(Transaction txn : transactionList) {
            if(txn.getTransactionStatus().equals(TransactionStatus.SUCCESS) &&
                    txn.getTransactionType().equals(TransactionType.ISSUE)) {

                int studentId = txn.getLibraryCard().getStudent().getRollNo();
                int bookId = txn.getBook().getBookId();

                HashSet<Integer> bookSet = student_book_map.getOrDefault(studentId, new HashSet<>());
                bookSet.add(bookId);
                student_book_map.put(studentId, bookSet);
            }
        }

        int mostBooks = 0;
        Student studentReader = null;

        for(int studentId : student_book_map.keySet()) {
            int bookCount = student_book_map.get(studentId).size();
            if(bookCount > mostBooks) {
                mostBooks = bookCount;
                studentReader = studentRepository.findById(studentId).get();
            }
        }
        return studentReader.getName();
    }

    public List<BookResponseStudent> booksIssuedByStudent(Integer id) throws Exception {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if(!optionalStudent.isPresent()) {
            throw new StudentNotFoundException("student id not correct");
        }

        //library card tells how many books are currently issued
        Student student = optionalStudent.get();
        LibraryCard studentCard = student.getLibraryCard();

        int numberOfBooks = studentCard.getNoOfBooksIssued();

        //find all books issued ever on the card
        List<Transaction> transactionList = transactionRepository2.findTransactionsByLibraryCardAndTransactionStatusAndTransactionType(studentCard, TransactionStatus.SUCCESS, TransactionType.ISSUE);

        //get last numberOfBooks from the list to find the currently issued books
        List<BookResponseStudent> responseList = new ArrayList<>();

        for(int i = transactionList.size() - numberOfBooks; i < transactionList.size(); i++) {
            Transaction transaction = transactionList.get(i);
            Book book = transaction.getBook();

            BookResponseStudent responseBook = BookResponseStudent.builder().title(book.getTitle()).
                    genre(book.getGenre()).authorName(book.getAuthor().getName()).price(book.getPrice()).
                    publicatonDate(book.getPublicationDate()).build();

            responseList.add(responseBook);
        }

        return responseList;
    }

    public void updateStudentDetails(UpdateStudentRequestDTO updateStudentRequestDTO) throws Exception {
        if(updateStudentRequestDTO.getOriginalEmailId() == null) {
            throw new Exception("Original EmailId not provided");
        }
        if(updateStudentRequestDTO.getOriginalName() == null) {
            throw new Exception("Original Name not provided");
        }
        String originalName = updateStudentRequestDTO.getOriginalName();
        String originalEmailId = updateStudentRequestDTO.getOriginalEmailId();

        Student student = studentRepository.findStudentByNameAndEmailId(originalName, originalEmailId);

        if(student == null) {
            throw new StudentNotFoundException("No matching name and email pair found");
        }

        if(updateStudentRequestDTO.getUpdatedName() != null)
            student.setName(updateStudentRequestDTO.getUpdatedName());
        if(updateStudentRequestDTO.getUpdatedEmailId() != null)
            student.setEmailId(updateStudentRequestDTO.getUpdatedEmailId());
        if(updateStudentRequestDTO.getUpdatedAge() != null)
            student.setAge(updateStudentRequestDTO.getUpdatedAge());
        if(updateStudentRequestDTO.getUpdatedGender() != null)
            student.setGender(updateStudentRequestDTO.getUpdatedGender());
        if(updateStudentRequestDTO.getUpdateDepartment() != null)
            student.setDepartment(updateStudentRequestDTO.getUpdateDepartment());

        studentRepository.save(student);
    }

}
