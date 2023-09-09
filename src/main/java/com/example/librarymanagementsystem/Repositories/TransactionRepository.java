package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.Enums.TransactionStatus;
import com.example.librarymanagementsystem.Enums.TransactionType;
import com.example.librarymanagementsystem.Models.Book;
import com.example.librarymanagementsystem.Models.LibraryCard;
import com.example.librarymanagementsystem.Models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Integer> {


    List<Transaction> findTransactionsByBookAndLibraryCardAndTransactionStatusAndTransactionType(Book book, LibraryCard card, TransactionStatus transactionStatus, TransactionType transactionType);

    @Query(value = "select * from transaction where transaction_status = 'SUCCESS' and transaction_type = 'RETURN'", nativeQuery = true)
    List<Transaction> findSuccessfulReturnTransactions();

    List<Transaction> findTransactionsByLibraryCardAndTransactionStatusAndTransactionType(LibraryCard card, TransactionStatus transactionStatus, TransactionType transactionType);

    @Query(value = "select * from transaction where transaction_type = 'ISSUE' and " +
            "transaction_status = 'SUCCESS' and created_at =:date order by library_card_card_no", nativeQuery = true)
    List<Transaction> findAllSuccessfulIssueAtDate(@Param("date") LocalDate date);

    @Query(value = "select * from transaction where book_book_id=:bookId and " +
            "library_card_card_no =:cardId and " +
            "transaction_type = 'RETURN' and transaction_status = 'SUCCESS' and " +
            "created_at >=:issueDate and created_at <=:currDate " +
            "limit 1", nativeQuery = true)
    Optional<Transaction> findFirstReturnBetweenIssueDateAndCurrDate(@Param("bookId") int bookId, @Param("cardId") int cardId, @Param("issueDate") LocalDate issueDate, @Param("currDate") LocalDate currDate);

    @Query(value = "select * from transaction where book_book_id =:bookId and " +
            "transaction_type = 'ISSUE' and transaction_status = 'SUCCESS' " +
            "order by transaction_id desc limit 1", nativeQuery = true)
    Optional<Transaction> findLatestTransactionByBookId(@Param("bookId") Integer bookId);

    @Query(value = "select * from transaction where library_card_card_no =:cardId and " +
            "transaction_type = 'ISSUE' and transaction_status = 'SUCCESS' and " +
            "created_at =:issueDate", nativeQuery = true)
    List<Transaction> findIssueTransactionsByCardAtDate(@Param("cardId") Integer cardId, @Param("issueDate") LocalDate issueDate);
}
