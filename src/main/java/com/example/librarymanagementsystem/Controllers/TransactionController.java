package com.example.librarymanagementsystem.Controllers;


import com.example.librarymanagementsystem.Services.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/issueBook")
    public ResponseEntity issueBook(@RequestParam("bookId")Integer bookId,@RequestParam("cardId")Integer cardId){

        try{
            String result = transactionService.issueBook(bookId,cardId);
            return new ResponseEntity(result,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/returnBook")
    public ResponseEntity returnBook(@RequestParam("bookId")Integer bookId,@RequestParam("cardId")Integer cardId) {
        try {
            String response = transactionService.returnBook(bookId, cardId);
            return new ResponseEntity(response, HttpStatus.OK);
        } catch(Exception e) {
            log.error(e.getClass().toString() + " : " + e.getMessage());
            return new ResponseEntity("FAILURE : "+e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/total-fine-collected")
    public ResponseEntity<String> getFineInYear(@RequestParam("year") Integer year) {
        try{
            int fine = transactionService.getFineCollectedForYear(year);
            return new ResponseEntity<>("fine collected : "+fine, HttpStatus.OK);
        } catch(Exception e) {
            log.error("Invalid Year {}",e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }



}


