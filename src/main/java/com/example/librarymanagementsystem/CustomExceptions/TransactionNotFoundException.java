package com.example.librarymanagementsystem.CustomExceptions;

public class TransactionNotFoundException extends Exception{
    public TransactionNotFoundException(String message) {
        super(message);
    }
}
