package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.InternalDTOs.BookIssueEmailDTO;
import com.example.librarymanagementsystem.InternalDTOs.CardIssueEmailDTO;
import com.example.librarymanagementsystem.InternalDTOs.ReturnBookEmailDTO;
import com.example.librarymanagementsystem.Models.LibraryCard;
import com.example.librarymanagementsystem.Models.Transaction;
import com.example.librarymanagementsystem.Repositories.CardRepository;
import com.example.librarymanagementsystem.Repositories.StudentRepository;
import com.example.librarymanagementsystem.Repositories.TransactionRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CardRepository cardRepository;

    @Value("${book.maxLimit}")
    private Integer maxBookLimit;
    @Value("${libraryCard.blockAfterDays}")
    private Integer blockAfterDays;
    @Value("${book.returnAfterDays}")
    private Integer returnAfterDays;

    private String from = "springbootlibraryproject@gmail.com";

    public void sendBookIssueEmail(BookIssueEmailDTO issueEmailDTO, String receiverEmail) throws MessagingException {

        String to = receiverEmail;

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setSubject("Book Borrowed from SpringBoot Library");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setText(createBookIssueEmailBody(issueEmailDTO), true);

        javaMailSender.send(message);

    }
    private String createBookIssueEmailBody(BookIssueEmailDTO issueEmailDTO) {

        String receiverName = issueEmailDTO.getReceiverName();
        String bookName = issueEmailDTO.getBookName();
        String authorName = issueEmailDTO.getAuthorName();
        int cardId = issueEmailDTO.getCardId();
        int remainingBookLoans = maxBookLimit - issueEmailDTO.getCurrentBooksIssued();
        LocalDate issueDate = issueEmailDTO.getIssueDate();

        LocalDate returnByDate = issueDate.plusDays(returnAfterDays
        );

        String body = "<b>Hi "+receiverName+"!</b><br>" +
                "Thank you for using Spring Boot Library Project.<br>" +
                "<br>Your book issue details are as followed : <br>" +
                "<strong>Book Name</strong> : <em>"+bookName+"</em><br>" +
                "<strong>Author Name</strong> : </em>"+authorName+"</em><br>" +
                "<strong>Issue Date</strong> : <em>"+issueDate+"</em><br>" +
                "<strong>Issued on Card</strong> : <em>"+cardId+"</em><br>" +
                "<strong>Remaining book Loans on your card</strong> : <em>"+remainingBookLoans+"</em><br>" +
                "<br>" +
                "Kindly return the borrowed book by <mark>"+returnByDate+"</mark> to avoid imposition of fine." +
                "<br>" +
                "<br>" +
                "Regards," +
                "<br>" +
                "Spring Boot Library";

        return body;

    }

    public void sendCardIssueEmail(CardIssueEmailDTO cardIssueEmailDTO) throws MessagingException {

        String to = cardIssueEmailDTO.getReceiverEmail().trim();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setSubject("New Library Card Issued");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setText(createCardIssueEmailBody(cardIssueEmailDTO), true);

        javaMailSender.send(message);

    }
    private String createCardIssueEmailBody(CardIssueEmailDTO cardIssueEmailDTO) {
        String receiverName = cardIssueEmailDTO.getReceiverName();
        int cardId = cardIssueEmailDTO.getCardId();

        String body = "<b> Hi "+receiverName+"!</b><br>" +
                "<br>" +
                "<em>Congratulations!</em><br>" +
                "<br>" +
                "You have been issued a new <em>Spring Boot Library</em> Library Card.<br>" +
                "Each library card has a unique id, yours is : <strong>" + cardId + "</strong><br>" +
                "This card permits you to borrow up to <b>"+maxBookLimit+"</b> books at a time.<br>" +
                "<br>" +
                "We look forward to issuing you your first book from Spring Book Library.<br>" +
                "<br>" +
                "Kind Regards,<br>" +
                "<em>Spring Boot Library Project</em>";

        return body;
    }

    public void sendBookReturnEmail(ReturnBookEmailDTO returnBookEmailDTO) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setSubject("Book Returned");
        helper.setFrom(from);
        helper.setTo(returnBookEmailDTO.getReceiverEmail().trim());
        helper.setText(createBookReturnEmailBody(returnBookEmailDTO), true);

        javaMailSender.send(message);
    }
    private String createBookReturnEmailBody(ReturnBookEmailDTO returnBookEmailDTO) {
        String receiverName = returnBookEmailDTO.getReceiverName();
        String receiverEmail = returnBookEmailDTO.getReceiverEmail();

        int fineAmt = returnBookEmailDTO.getFineAmt();
        LocalDate issueDate = returnBookEmailDTO.getIssueDate();
        LocalDate returnDate = returnBookEmailDTO.getReturnDate();

        long daysLoaned = ChronoUnit.DAYS.between(issueDate, returnDate);

        String start = "<b> Hi "+receiverName+"!</b><br>" +
                "<br>" +
                "Your book return details are as follows : <br>" +
                "<b>Book Id :</b> <em>"+returnBookEmailDTO.getBookId()+"</em><br>" +
                "<b>Book Name :</b> <em>"+returnBookEmailDTO.getBookName()+"</em><br>" +
                "<b>Issue Date :</b> <em>"+issueDate+"</em><br>" +
                "<b>Return Date :</b> <em>"+returnDate+"</em> (returned after "+daysLoaned+" days)<br>";

        String mid = "";
        if(fineAmt > 0) {
            mid = "<b>Fine Levied : </b><em>" + fineAmt + "</em><br>" +
                    "<br>" +
                    "Kindly ensure to return borrowed books withing " + returnAfterDays +
                    " days of issue date to avoid fine<br>";
        }

        String end = "<br>" +
                "<em>Thank you for using Spring Boot Library Project</em><br>" +
                "<br>" +
                "Kind Regards,<br>" +
                "Spring Boot Library Project";

        String body = start + mid + end;

        return body;
    }

    public void sendReturnReminderEmail() throws MessagingException {
        /**
         * return deadline to avoid fine is returnAfterDays
         * days
         * find transactions for which return deadline is coming in the next 3 days
         * if remaining days = 3, send 1st reminder
         * if remaining days = 1, send 2nd reminder
         *
         * find all SUCCESSful ISSUE transactions ocurring on reminder1_issueDate and reminder2_issueDate
         * transactions eligible for email will be those which have returns still pending at current date
         *
         * if after reminder_createDate and before current date there is no 'RETURN' transaction for the book card pair,
         * then the transaction is eligible for reminder email
         */

        LocalDate today = LocalDate.now();
        //books issued returnAfterDays-3 days ago, receive reminder 1
        LocalDate reminder1_issueDate = today.minusDays(returnAfterDays-3);
        //books issued returnAfterDays-1 days ago, receive reminder 2
        LocalDate reminder2_issueDate = today.minusDays(returnAfterDays-2);

        log.info("today : {}",today);
        log.info("reminder1_issueDate : {}",reminder1_issueDate);
        log.info("reminder2_issueDate : {}",reminder2_issueDate);

        List<Transaction> reminder1_transactionList = transactionRepository.findAllSuccessfulIssueAtDate(reminder1_issueDate);
        List<Transaction> reminder2_transactionList = transactionRepository.findAllSuccessfulIssueAtDate(reminder2_issueDate);


        //send reminder 1 email
        for(Transaction transaction : reminder1_transactionList) {
            //find transactions which are eligible recipients of reminder1 and reminder2 email
            if(isEligibleForReminder(transaction, reminder1_issueDate, today) == false) continue;

            log.info("Transaction with id is eligible for reminder 1 : {}",transaction.getTransactionId());

            String recipientName = transaction.getLibraryCard().getStudent().getName();
            String recipientEmailId = transaction.getLibraryCard().getStudent().getEmailId().trim();
            String bookName = transaction.getBook().getTitle();
            int bookId = transaction.getBook().getBookId();
            int cardId = transaction.getLibraryCard().getCardNo();
            LocalDate returnByDate = reminder1_issueDate.plusDays(returnAfterDays
            );

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);
            helper.setSubject("Reminder to Return Book Before Deadline");
            helper.setTo(recipientEmailId);
            helper.setText(createReminderEmailBody(recipientName, returnByDate, reminder1_issueDate, bookName, bookId, cardId), true);

            javaMailSender.send(message);

        }

        //send reminder 2 email
        for(Transaction transaction : reminder2_transactionList) {
            //find transactions which are eligible recipients of reminder1 and reminder2 email
            if(isEligibleForReminder(transaction, reminder2_issueDate, today) == false) continue;

            log.info("Transaction with id is eligible for reminder 2 : {}",transaction.getTransactionId());
            String recipientName = transaction.getLibraryCard().getStudent().getName();
            String recipientEmailId = transaction.getLibraryCard().getStudent().getEmailId();

            String bookName = transaction.getBook().getTitle();
            int bookId = transaction.getBook().getBookId();
            int cardId = transaction.getLibraryCard().getCardNo();

            LocalDate returnByDate = reminder2_issueDate.plusDays(returnAfterDays
            );

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);
            helper.setSubject("Final Reminder to Return Book Before Deadline");
            helper.setTo(recipientEmailId);
            helper.setText(createReminderEmailBody(recipientName, returnByDate, reminder2_issueDate, bookName, bookId, cardId), true);

            javaMailSender.send(message);
        }

    }
    /*private boolean  findEligibleTransactions(List<Transaction> reminder_transactionList, LocalDate issueDate, LocalDate today) {
        List<Transaction> eligibleTransactions_reminder = new ArrayList<>();

        for(Transaction transaction : reminder_transactionList) {
            int bookId = transaction.getBook().getBookId();
            int cardId = transaction.getLibraryCard().getCardNo();

            //check if any return transaction exists corresponding to current issue transaction
            Optional<Transaction> optionalTransaction = transactionRepository.findFirstReturnAfterIssueDateAndCurrDate(bookId, cardId, issueDate, today);

            //if no return transaction exists for the book by the card, then it this transaction is eligible for reminder mail
            if(!optionalTransaction.isPresent()) {
                eligibleTransactions_reminder.add(transaction);
            }
        }

        return eligibleTransactions_reminder;
    }*/
    private boolean isEligibleForReminder(Transaction transaction, LocalDate issueDate, LocalDate today) {
        int bookId = transaction.getBook().getBookId();
        int cardId = transaction.getLibraryCard().getCardNo();

        //check if any return transaction exists corresponding to current issue transaction
        Optional<Transaction> optionalTransaction = transactionRepository.findFirstReturnBetweenIssueDateAndCurrDate(bookId, cardId, issueDate, today);

        //if no return transaction exists for the book by the card, then this transaction is eligible for reminder mail
        if(!optionalTransaction.isPresent()) {
            return true;
        }
        else {
            return false;
        }
    }
    private String createReminderEmailBody(String receiverName, LocalDate returnByDate, LocalDate issueDate, String bookName, int bookId, int cardId) {

        String body = "<b>Hello "+receiverName+"!</b><br>" +
                "<br>" +
                "We would like to remind you of the impending return deadline for your recently loaned book. <br>" +
                "<br>" +
                "Your issued book details are as follows : <br>" +
                "<b>Book name :</b> <i>"+bookName+"</i><br>" +
                "<b>Book id :</b> <i>"+bookId+"</i><br>" +
                "<b>Issue date:</b> <i>"+issueDate+"</i><br>" +
                "<b>Issued on card</b> : <em>"+cardId+"</em><br>" +
                "<br>" +
                "Please make sure to return above-mentioned book on or before :<b><mark>"+returnByDate+"</mark></b> to avoid imposition of fine<br>" +
                "<br>" +
                "Kind regards,<br>" +
                "Spring Boot Library Project";

        return body;

    }


    public void sendCardBlockWarningEmail() throws MessagingException {
        /**
         * send email 5 days before card block date
         * if card is blocked after 45 days of not returning loaned books
         * send email after 40 days
         *
         * get current date
         * get date 40 days before current date
         * get all SUCCESSFUL ISSUE transactions which don't have corresponding a RETURN transaction
         *  for the same bookId and cardId pair
         */

        LocalDate today = LocalDate.now();
        LocalDate issueDate = today.minusDays(blockAfterDays-5);
        LocalDate blockDate = issueDate.plusDays(blockAfterDays);

        List<Transaction> issueTransactionsAtDate = transactionRepository.findAllSuccessfulIssueAtDate(issueDate);

        for(Transaction transaction : issueTransactionsAtDate) {
            if((isEligibleForReminder(transaction, issueDate, today) == false))
                continue;

            String recipientName = transaction.getLibraryCard().getStudent().getName();
            String recipientEmailId = transaction.getLibraryCard().getStudent().getEmailId().trim();
            String bookName = transaction.getBook().getTitle();
            int bookId = transaction.getBook().getBookId();
            int cardId = transaction.getLibraryCard().getCardNo();

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);
            helper.setTo(recipientEmailId);
            helper.setSubject("WARNING : Library Card will be Blocked Soon");
            helper.setText(createCardBlockWarningEmailBody(recipientName, bookName, cardId, issueDate, blockDate), true);

            javaMailSender.send(message);
        }
    }
    private String createCardBlockWarningEmailBody(String recipientName, String bookName, Integer cardId, LocalDate issueDate, LocalDate returnByDate) {

        String body = "Dear "+recipientName+",<br>" +
                "<br>" +
                "Kindly note that your Spring Boot Library card, having <b><mark>card id : "+cardId+"</b></mark>, will be blocked on " +
                returnByDate + " unless your loaned book is returned before aforementioned date.<br>" +
                "<br>" +
                "Your book loan details are as follows : <br>" +
                "<b>Book name : </b><i>"+bookName+"</i><br>" +
                "<b>Borrowed on date :</b><i>"+issueDate+"</i><br>" +
                "<br>" +
                "Please make sure to complete the return process for the above-mentioned book on or before "+returnByDate+"." +
                "<br>" +
                "<br>" +
                "Kind regards,<br>" +
                "Spring Boot Library Project";

        return body;
    }

    public void sendCardBlockNotificationEmail(List<Transaction> unreturnedTransactionList, LibraryCard card) throws MessagingException {
        String recipientEmailId = card.getStudent().getEmailId();
        String recieverName = card.getStudent().getName();
        Integer cardId = card.getCardNo();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(from);
        helper.setTo(recipientEmailId);
        helper.setSubject("Library Card has been Blocked");
        helper.setText(createCardBlockNotificationEmailBody(recieverName, unreturnedTransactionList, cardId), true);

        javaMailSender.send(message);

    }
    private String createCardBlockNotificationEmailBody(String receiverName, List<Transaction> unreturnedTransactionList, Integer cardId) {

        String body = "Dear "+receiverName+",<br>" +
                "<br>" +
                "We regret to inform you that your Spring Boot Library Card :  <b><mark>card number "+cardId+"</b></mark>" +
                "has been <i>BLOCKED</i> on account of failure to return your loaned book within "+blockAfterDays+" days.<br>" +
                "<br>" +
                "Kindly note your pending return details : <br>" +
                "<br>" +
                "<table>" +
                "<tr>" +
                "<th>Book Name</th>" +
                "<th>Issued on Date</th>" +
                "</tr>";

        for(Transaction transaction : unreturnedTransactionList) {
            String bookName = transaction.getBook().getTitle();
            LocalDate issueDate = transaction.getCreatedAt();

            String list = "<tr>" +
                    "<td>"+bookName+"</td>" +
                    "<td>"+issueDate+"<td>" +
                    "</tr>";

            body += list;
        }

        body = body.concat("</table>" +
                "<br>" +
                "<br>Please make sure to return above-mentioned books as soon as possible in order to un-block your library card." +
                "<br>" +
                "<br>" +
                "Kind regards,<br>" +
                "Spring Boot Library Project");

        return body;
    }
}
