package com.example.librarymanagementsystem.Controllers;


import com.example.librarymanagementsystem.Models.LibraryCard;
import com.example.librarymanagementsystem.Services.EmailService;
import com.example.librarymanagementsystem.Services.LibraryCardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/card")
@Slf4j
public class CardController {

    @Autowired
    private LibraryCardService cardService;
    @Autowired
    private EmailService emailService;


    @PostMapping("/create")
    public String addCard(){

        return cardService.createCard();
    }

    @PutMapping("/issueToStudent")
    public ResponseEntity issueToStudent(@RequestParam("rollNo")Integer rollNo){

        try{

            String result =  cardService.associateToStudent(rollNo);
            return new ResponseEntity(result,HttpStatus.OK);
        }catch (Exception e){
            log.error("Error in associating card to student",e.getMessage());
            return new ResponseEntity(e.getClass().toString() + e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }

    }

    @PutMapping("/blockCard")
    public ResponseEntity blockCard(@RequestParam("cardId") Integer cardId) {
        try {
            cardService.blockCard(cardId);
            return new ResponseEntity("Card blocked SUCCESSFULLY", HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getClass().toString() + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
