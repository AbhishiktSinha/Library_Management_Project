package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.Enums.TransactionStatus;
import com.example.librarymanagementsystem.Enums.TransactionType;
import com.example.librarymanagementsystem.Models.Author;
import com.example.librarymanagementsystem.Models.Book;
import com.example.librarymanagementsystem.Models.Transaction;
import com.example.librarymanagementsystem.Repositories.AuthorRepository;
import com.example.librarymanagementsystem.Repositories.TransactionRepository;
import com.example.librarymanagementsystem.RequestDto.AddAuthorDTO;
import com.example.librarymanagementsystem.RequestDto.UpdateAuthorDTO;
import com.example.librarymanagementsystem.RequestDto.UpdateNameAndPenNameRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.function.Function;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private TransactionRepository transactionRepository1;


    public String addAuthor(AddAuthorDTO addAuthorDTO)throws Exception{

        Author author = Author.builder().name(addAuthorDTO.getName())
                        .emailId(addAuthorDTO.getEmailId()).gender(addAuthorDTO.getGender())
                        .age(addAuthorDTO.getAge()).build();

        //throws exception in case of duplicate email id
        try {
            authorRepository.save(author);
            return "SUCCESS";
        } catch(Exception e) {
            throw e;
        }
    }

    public void updateAuthorDetails(UpdateAuthorDTO updateAuthorDTO) throws Exception{
        if(updateAuthorDTO.getOriginalEmailId() == null && updateAuthorDTO.getOriginalName() == null) {
            throw new Exception("Provide original name and original email id");
        }

        Author author = authorRepository.findAuthorByNameAndEmailId(updateAuthorDTO.getOriginalName(), updateAuthorDTO.getOriginalEmailId());

        //update not null value
        if(updateAuthorDTO.getUpdatedName() != null)
            author.setName(updateAuthorDTO.getUpdatedName());
        if(updateAuthorDTO.getUpdateEmailId() != null)
            author.setEmailId(updateAuthorDTO.getUpdateEmailId());
        if(updateAuthorDTO.getUpdatedAge() != null)
            author.setAge(updateAuthorDTO.getUpdatedAge());
        if(updateAuthorDTO.getUpdatedGender() != null)
            author.setGender(updateAuthorDTO.getUpdatedGender());

        authorRepository.save(author);
    }

    public Author getAuthorById(Integer authorId){

        Author author = authorRepository.findById(authorId).get();
        return author;

    }

    public String getMostPopularAuthor() {

        List<Transaction> transactionList = transactionRepository1.findAll();

        Map<Integer, HashSet<Integer>> author_student_map = new HashMap<>();

        for(Transaction txn : transactionList) {
            if(txn.getTransactionStatus().equals(TransactionStatus.SUCCESS) &&
                    txn.getTransactionType().equals(TransactionType.ISSUE)) {

                int authorId = txn.getBook().getAuthor().getAuthorId();
                int studentId = txn.getLibraryCard().getStudent().getRollNo();

                HashSet<Integer> studentSet = author_student_map.getOrDefault(authorId, new HashSet<>());
                studentSet.add(studentId);
                author_student_map.put(authorId, studentSet);
            }
        }

        int mostReaders = 0;
        Author mostReadAuthor = null;

        for(int authorId : author_student_map.keySet()) {
            int readers = author_student_map.get(authorId).size();
            if(readers > mostReaders) {
                mostReaders = readers;
                mostReadAuthor = authorRepository.findById(authorId).get();
            }
        }

        return mostReadAuthor.getName();

    }


}
