package com.example.librarymanagementsystem.CustomExceptions;

public class BookNotLoanedException extends Exception{
    BookNotLoanedException(String message) {
        super(message);
    }
}
