package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.Services.EmailService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/email-sender")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/send-return-reminder")
    public ResponseEntity<String> sendAutomatedReturnReminder() {
        try {
            emailService.sendReturnReminderEmail();
            return new ResponseEntity<>("EMAILS SENT", HttpStatus.OK);
        } catch (MessagingException e) {
            log.error(e.getClass().toString()+" : "+e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/send-card-block-warning")
    public ResponseEntity sendCardBlockWarningEmail() {
        try {
            emailService.sendCardBlockWarningEmail();
            return new ResponseEntity("EMAILS SENT", HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getClass().toString() + " : " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
