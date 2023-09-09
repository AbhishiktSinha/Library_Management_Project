package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.CustomExceptions.BookNotAvailableException;
import com.example.librarymanagementsystem.CustomExceptions.BookNotFoundException;
import com.example.librarymanagementsystem.CustomExceptions.InvalidYearException;
import com.example.librarymanagementsystem.Enums.CardStatus;
import com.example.librarymanagementsystem.Enums.TransactionStatus;
import com.example.librarymanagementsystem.Enums.TransactionType;
import com.example.librarymanagementsystem.InternalDTOs.BookIssueEmailDTO;
import com.example.librarymanagementsystem.InternalDTOs.ReturnBookEmailDTO;
import com.example.librarymanagementsystem.Models.Book;
import com.example.librarymanagementsystem.Models.LibraryCard;
import com.example.librarymanagementsystem.Models.Transaction;
import com.example.librarymanagementsystem.Repositories.AuthorRepository;
import com.example.librarymanagementsystem.Repositories.BookRepository;
import com.example.librarymanagementsystem.Repositories.CardRepository;
import com.example.librarymanagementsystem.Repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Value("${book.maxLimit}")
    private Integer maxBookLimit;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private EmailService emailService;


    public String issueBook(Integer bookId,Integer cardId)throws Exception{

        //Book Related Exception Handling

        Transaction transaction = Transaction.builder().transactionType(TransactionType.ISSUE).
                transactionStatus(TransactionStatus.PENDING).fineAmount(0).build();

        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if(!optionalBook.isPresent()){
            throw new BookNotFoundException("Book Id is incorrect");
        }
        Book book = optionalBook.get();
        if(book.getIsAvailable()==Boolean.FALSE){
            throw new BookNotAvailableException("Book is not Avaialble");
        }


        //Card Related Exception Handling
        Optional<LibraryCard> optionalLibraryCard = cardRepository.findById(cardId);
        if(!optionalLibraryCard.isPresent()){
            throw new Exception("Card Id entered is Invalid");
        }

        LibraryCard card = optionalLibraryCard.get();
        if(!card.getCardStatus().equals(CardStatus.ACTIVE)){
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transaction = transactionRepository.save(transaction);

            throw new Exception("Card is not in Right status");
        }
        if(card.getNoOfBooksIssued() >= maxBookLimit){

            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transaction = transactionRepository.save(transaction);
            throw new Exception("Already max Limit Books are issued");
        }
        /*  All the failed cases and invalid scenarios are over */
        //We have reached at a success point

        transaction.setTransactionStatus(TransactionStatus.SUCCESS);

        //update the card and book Entity
        book.setIsAvailable(Boolean.FALSE);
        card.setNoOfBooksIssued(card.getNoOfBooksIssued()+1);
        //We need to do unidirectional mappings :-->
        transaction.setBook(book);
        transaction.setLibraryCard(card);


        Transaction newTransactionWithId = transactionRepository.save(transaction);
        //We need to do in the parent classes
        book.getTransactionList().add(newTransactionWithId);
        card.getTransactionList().add(newTransactionWithId);

        bookRepository.save(book);
        cardRepository.save(card);

        BookIssueEmailDTO bookIssueEmailDTO = BookIssueEmailDTO.builder().bookName(book.getTitle()).
                authorName(book.getAuthor().getName()).issueDate(newTransactionWithId.getCreatedAt()).
                receiverName(card.getStudent().getName()).cardId(cardId).currentBooksIssued(card.getNoOfBooksIssued()).
                build();

        emailService.sendBookIssueEmail(bookIssueEmailDTO, card.getStudent().getEmailId());

        //What all needs to saved
        return "Transaction has been saved successfully";

    }

    public String returnBook(Integer bookId,Integer cardId)throws Exception {

        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if(!optionalBook.isPresent()){
            throw new BookNotFoundException("Book Id is incorrect");
        }

        Optional<LibraryCard> optionalLibraryCard = cardRepository.findById(cardId);
        if(!optionalLibraryCard.isPresent()){
            throw new Exception("Card Id entered is Invalid");
        }

        Book book = bookRepository.findById(bookId).get();
        LibraryCard card = cardRepository.findById(cardId).get();

        //get latest from list of transactions for ISSUE on that card
        List<Transaction> transactionList = transactionRepository.findTransactionsByBookAndLibraryCardAndTransactionStatusAndTransactionType(book,card,TransactionStatus.SUCCESS,TransactionType.ISSUE);
        Transaction latestTransaction = transactionList.get(transactionList.size()-1);

        LocalDate issueDate = latestTransaction.getCreatedAt();
        LocalDate returnDate = LocalDate.now();
        long no_of_days_issued = ChronoUnit.DAYS.between(issueDate, returnDate);

        int fineAmount = 0;
        if(no_of_days_issued>15){
            fineAmount = (int) ((no_of_days_issued - 15)*5);
        }

        book.setIsAvailable(Boolean.TRUE);
        card.setNoOfBooksIssued(card.getNoOfBooksIssued()-1);

        Transaction transaction = Transaction.builder().transactionStatus(TransactionStatus.SUCCESS).
                transactionType(TransactionType.RETURN).fineAmount(fineAmount).build();

        transaction.setBook(book);
        transaction.setLibraryCard(card);

        Transaction newTransactionWithId = transactionRepository.save(transaction);

        book.getTransactionList().add(newTransactionWithId);
        card.getTransactionList().add(newTransactionWithId);

        //Saving the parents
        bookRepository.save(book);
        cardRepository.save(card);

        //send email
        ReturnBookEmailDTO returnBookEmailDTO = ReturnBookEmailDTO.builder().
                receiverName(card.getStudent().getName()).
                receiverEmail(card.getStudent().getEmailId()).
                bookId(bookId).bookName(book.getTitle()).
                issueDate(issueDate).returnDate(returnDate).fineAmt(fineAmount).
                build();
        emailService.sendBookReturnEmail(returnBookEmailDTO);

        return "Book has successfully been returned";
    }

    public int getFineCollectedForYear(int year)throws Exception {
        if(year <= 1950 && year > 2023)
            throw new InvalidYearException("Entered value out of range");

        List<Transaction> transactionList = transactionRepository.findSuccessfulReturnTransactions();
        int fine = 0;

        for(Transaction transaction : transactionList) {
            LocalDate returnDate = transaction.getUpdatedAt();
            int transactionYear = returnDate.getYear();

            if(transactionYear == year) {
                fine += transaction.getFineAmount();
            }
        }

        return fine;
    }


}
