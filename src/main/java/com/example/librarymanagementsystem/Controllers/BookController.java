package com.example.librarymanagementsystem.Controllers;


import com.example.librarymanagementsystem.Enums.Genre;
import com.example.librarymanagementsystem.RequestDto.AddBookRequestDto;
import com.example.librarymanagementsystem.ResponseDto.BooksByAuthorResponse;
import com.example.librarymanagementsystem.ResponseDto.BooksByGenreResponse;
import com.example.librarymanagementsystem.ResponseDto.BorrowerReturnDTO;
import com.example.librarymanagementsystem.ResponseDto.MostFinedBookResponseDTO;
import com.example.librarymanagementsystem.Services.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/book")
public class BookController {


    @Autowired
    private BookService bookService;

    @PostMapping("/add")
    public ResponseEntity addBook(@RequestBody AddBookRequestDto addBookRequestDto){

        try{

            String result = bookService.addBook(addBookRequestDto);
            return new ResponseEntity(result, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/getByGenre")
    public ResponseEntity getBookListByGenre(@RequestParam("genre")Genre genre){
        List<BooksByGenreResponse> responseDtoList = bookService.getBookListByGenre(genre);
        return new ResponseEntity(responseDtoList,HttpStatus.OK);
    }

    @GetMapping("/getMostFinedBook")
    public ResponseEntity<MostFinedBookResponseDTO> getMostFinedBook() {
        MostFinedBookResponseDTO responseDTO = bookService.getMostFinedBook();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/getBooksByAuthor")
    public ResponseEntity<List<BooksByAuthorResponse>> getBooksByAuthor(@RequestParam("id") Integer id) {
        try {
            List<BooksByAuthorResponse> list = bookService.getBooksListByAuthor(id);
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getClass().toString()+" : "+e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/isBookAvailable")
    public ResponseEntity checkAvailability(@RequestParam("id") Integer id) {
        try{
            Boolean isAvailable = bookService.checkBookAvailability(id);
            return new ResponseEntity(isAvailable, HttpStatus.OK);
        } catch(Exception e){
            log.error(e.getClass().toString()+" : "+e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getStudentCurrentlyLoaningBook")
    public ResponseEntity findCurrentBookLoanDetails(@RequestParam("bookId") Integer bookId) {
        try {
            BorrowerReturnDTO borrowerReturnDTO = bookService.findWhoHasLoanedBook(bookId);
            return new ResponseEntity(borrowerReturnDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getClass() + " : " + e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
