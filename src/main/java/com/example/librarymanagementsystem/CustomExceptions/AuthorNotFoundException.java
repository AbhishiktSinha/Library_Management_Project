package com.example.librarymanagementsystem.CustomExceptions;

public class AuthorNotFoundException extends Exception{
    public AuthorNotFoundException(String message) {
        super(message);
    }
}
