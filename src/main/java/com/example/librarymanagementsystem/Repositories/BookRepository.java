package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.Enums.Genre;
import com.example.librarymanagementsystem.Models.Author;
import com.example.librarymanagementsystem.Models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book,Integer> {

    List<Book> findBooksByGenre(Genre genre);

    List<Book> findBooksByIsAvailableFalse();

    List<Book> findBooksByAuthor(Author author);

}
