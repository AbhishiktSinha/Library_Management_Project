package com.example.librarymanagementsystem.Controllers;

import com.example.librarymanagementsystem.Models.Author;
import com.example.librarymanagementsystem.RequestDto.AddAuthorDTO;
import com.example.librarymanagementsystem.RequestDto.UpdateAuthorDTO;
import com.example.librarymanagementsystem.RequestDto.UpdateNameAndPenNameRequest;
import com.example.librarymanagementsystem.Services.AuthorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/author")
@Slf4j
public class AuthorController {

    @Autowired
    private AuthorService authorService;



    @PostMapping("/add")
    public ResponseEntity<String> addAuthor(@RequestBody AddAuthorDTO addAuthorDTO){

        try{

            String result = authorService.addAuthor(addAuthorDTO);
            return new ResponseEntity<String>(result,HttpStatus.OK);

        }catch (Exception e){
            log.error("Author couldnt be added to the db {}",e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/updateAuthorDetails")
    public ResponseEntity<String> updateAuthorDetails(@RequestBody UpdateAuthorDTO updateAuthorDTO){
        try {
            authorService.updateAuthorDetails(updateAuthorDTO);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Name or email not provided {}",e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getAuthor")
    public Author getAuthor(@RequestParam("authorId")Integer authorId){

        return authorService.getAuthorById(authorId);

    }

    @GetMapping("/findMostPopularAuthor")
    public ResponseEntity<String> getMostPopluarAuthor() {
        String authorName = authorService.getMostPopularAuthor();
        return new ResponseEntity<>(authorName, HttpStatus.OK);
    }


}
