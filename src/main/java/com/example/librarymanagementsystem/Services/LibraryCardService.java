package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.Enums.CardStatus;
import com.example.librarymanagementsystem.InternalDTOs.CardIssueEmailDTO;
import com.example.librarymanagementsystem.Models.LibraryCard;
import com.example.librarymanagementsystem.Models.Student;
import com.example.librarymanagementsystem.Models.Transaction;
import com.example.librarymanagementsystem.Repositories.CardRepository;
import com.example.librarymanagementsystem.Repositories.StudentRepository;
import com.example.librarymanagementsystem.CustomExceptions.LibararyCardNotFoundException;
import com.example.librarymanagementsystem.CustomExceptions.CanNotBlockCardException;
import com.example.librarymanagementsystem.Repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class LibraryCardService {

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EmailService emailService;

    @Value("${libraryCard.blockAfterDays}")
    private Integer blockAfterDays;


    public String createCard(){
        LibraryCard newCard = new LibraryCard();
        newCard.setCardStatus(CardStatus.NEW);
        newCard.setNoOfBooksIssued(0);

        cardRepository.save(newCard);
        return "Card has successfully been added to the database";
    }

    public String associateToStudent(Integer rollNo)throws Exception{

        //Student should exist
        if(!studentRepository.existsById(rollNo)){
            throw new Exception("Student Id is Invalid");
        }

        //Card should also exist
        Optional<LibraryCard> optionalLibraryCard = cardRepository.findNextAvailableCard();
        if(!optionalLibraryCard.isPresent()){
            throw new LibararyCardNotFoundException("No Library Card Available");
        }
        LibraryCard libraryCard = optionalLibraryCard.get();

        Optional<Student> optional = studentRepository.findById(rollNo);
        Student studentObj = optional.get();

        //Set the studentObj object in card object
        libraryCard.setStudent(studentObj);
        libraryCard.setCardStatus(CardStatus.ACTIVE);

        //Since its a bidirectional mapping
        //In the studentObj object also we need to set the libraryCard Object
        studentObj.setLibraryCard(libraryCard);

        studentRepository.save(studentObj);

        //send email to user
        CardIssueEmailDTO cardIssueEmailDTO = CardIssueEmailDTO.builder().
                receiverName(studentObj.getName()).receiverEmail(studentObj.getEmailId().trim()).
                cardId(libraryCard.getCardNo()).
                build();

        emailService.sendCardIssueEmail(cardIssueEmailDTO);

        return "Student and card saved successfully";

    }

    public void blockCard(Integer cardId) throws Exception {
        Optional<LibraryCard> optionalLibraryCard = cardRepository.findById(cardId);
        if(!optionalLibraryCard.isPresent()) {
            throw new LibararyCardNotFoundException("Invalid card id");
        }

        LibraryCard libraryCard = optionalLibraryCard.get();

        List<Transaction> unreturnedTransactionList = isEligibleToBeBlocked(libraryCard);
        if(unreturnedTransactionList.size() == 0) {
            throw new CanNotBlockCardException("Card not eligible to be blocked");
        }

        // now change card status and send email notification
        libraryCard.setCardStatus(CardStatus.BLOCKED);
        emailService.sendCardBlockNotificationEmail(unreturnedTransactionList, libraryCard);

    }
    private List<Transaction> isEligibleToBeBlocked(LibraryCard card) {
        //if has not returned book loaned blockAfterDays ago
        LocalDate today = LocalDate.now();
        LocalDate issueDate = today.minusDays(blockAfterDays);
        List<Transaction> unreturnedList = new ArrayList<>();

        //find all issue transactions on this card at date
        List<Transaction> issueTransactionAtDateList = transactionRepository.findIssueTransactionsByCardAtDate(card.getCardNo(), issueDate);
        if(issueTransactionAtDateList.size() == 0)
            return new ArrayList<>();

        //check if any book loaned at issueDate has not been returned after blockAfterDays, if so block the card
        for(Transaction transaction : issueTransactionAtDateList) {
            int bookId = transaction.getBook().getBookId();
            int cardId = transaction.getLibraryCard().getCardNo();

            //find corresponding return transaction if any exists
            Optional<Transaction> optionalTransaction = transactionRepository.findFirstReturnBetweenIssueDateAndCurrDate(bookId, cardId, issueDate, today);

            //if there is no return transaction, this means card can be blocked
            if(!optionalTransaction.isPresent()) unreturnedList.add(transaction);
        }
        return unreturnedList;

    }


}
