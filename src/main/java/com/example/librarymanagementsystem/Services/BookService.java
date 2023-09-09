package com.example.librarymanagementsystem.Services;

import com.example.librarymanagementsystem.CustomExceptions.BookNotFoundException;
import com.example.librarymanagementsystem.Enums.Genre;
import com.example.librarymanagementsystem.CustomExceptions.AuthorNotFoundException;
import com.example.librarymanagementsystem.CustomExceptions.TransactionNotFoundException;
import com.example.librarymanagementsystem.Models.Author;
import com.example.librarymanagementsystem.Models.Book;
import com.example.librarymanagementsystem.Models.Transaction;
import com.example.librarymanagementsystem.Repositories.AuthorRepository;
import com.example.librarymanagementsystem.Repositories.BookRepository;
import com.example.librarymanagementsystem.Repositories.TransactionRepository;
import com.example.librarymanagementsystem.RequestDto.AddBookRequestDto;
import com.example.librarymanagementsystem.ResponseDto.BooksByAuthorResponse;
import com.example.librarymanagementsystem.ResponseDto.BooksByGenreResponse;
import com.example.librarymanagementsystem.ResponseDto.BorrowerReturnDTO;
import com.example.librarymanagementsystem.ResponseDto.MostFinedBookResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;

@Service
public class BookService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public String addBook(AddBookRequestDto request)throws Exception{

        Optional<Author> optionalAuthor = authorRepository.findById(request.getAuthorId());
        if(!optionalAuthor.isPresent()){
            throw new AuthorNotFoundException("Author Id Entered is Incorrect");
        }

        Author author = optionalAuthor.get();

        Book book = Book.builder().
                title(request.getTitle()).price(request.getPrice()).
                genre(request.getGenre()).
                publicationDate(request.getPublicationDate()).
                isAvailable(true).
                build();

        book.setAuthor(author);

        List<Book> authorBookList = author.getBookList();
        authorBookList.add(book);
        author.setBookList(authorBookList);

        authorRepository.save(author);

        return "SUCCESS";

    }

    public List<BooksByGenreResponse> getBookListByGenre(Genre genre){

        List<Book> bookList = bookRepository.findBooksByGenre(genre);
        List<BooksByGenreResponse> responseList = new ArrayList<>();

        for(Book book : bookList){

            BooksByGenreResponse booksByGenreResponse = new BooksByGenreResponse(book.getTitle(), book.getGenre(),
                                                book.getPublicationDate(),book.getPrice(), book.getAuthor().getName());

            responseList.add(booksByGenreResponse);
        }
        return responseList;
    }

    public List<BooksByAuthorResponse> getBooksListByAuthor(Integer authorId) throws Exception {
        Optional<Author> optionalAuthor = authorRepository.findById(authorId);
        if(!optionalAuthor.isPresent()) {
            throw new AuthorNotFoundException("Check author id");
        }

        Author author = optionalAuthor.get();
        List<Book> bookList = bookRepository.findBooksByAuthor(author);

        List<BooksByAuthorResponse> authorBookDTOList = new ArrayList<>();
        for(Book book : bookList) {

            BooksByAuthorResponse responseBook = BooksByAuthorResponse.builder().title(book.getTitle()).
                    genre(book.getGenre()).publicationDate(book.getPublicationDate()).isAvailable(book.getIsAvailable()).build();

            authorBookDTOList.add(responseBook);
        }

        return authorBookDTOList;
    }

    public MostFinedBookResponseDTO getMostFinedBook() {
        List<Transaction> transactionList = transactionRepository.findAll();
        HashMap<Integer, Integer> bookFineMap = new HashMap<>();

        for(Transaction txn : transactionList) {
            int bookId = txn.getBook().getBookId();
            int fineAmt = txn.getFineAmount();

            bookFineMap.put(bookId, bookFineMap.getOrDefault(bookId, 0)+fineAmt);
        }

        int mostFinedId = -1;
        int maxFine = -1;
        for(int bookId : bookFineMap.keySet()) {
            int bookFine = bookFineMap.get(bookId);

            if(bookFine > maxFine) {
                maxFine = bookFine;
                if(mostFinedId == -1) {
                    mostFinedId = bookId;
                } else {
                    String newContender = bookRepository.findById(bookId).get().getTitle();
                    String oldBook = bookRepository.findById(mostFinedId).get().getTitle();
                    if(newContender.compareTo(oldBook) < 0) {
                        mostFinedId = bookId;
                    }
                }
            }
        }
        String title = bookRepository.findById(mostFinedId).get().getTitle();
        MostFinedBookResponseDTO responseDTO = new MostFinedBookResponseDTO(title, maxFine);
        return responseDTO;
    }

    public boolean checkBookAvailability(Integer id) throws BookNotFoundException {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if(!optionalBook.isPresent()) {
            throw new BookNotFoundException("Incorrect book id");
        }

        return optionalBook.get().getIsAvailable();
    }

    public BorrowerReturnDTO findWhoHasLoanedBook(Integer bookId) throws Exception {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if(!optionalBook.isPresent()) {
            throw new BookNotFoundException("Invalid book id");
        }
        if(optionalBook.get().getIsAvailable() == true) {
            throw new BookNotFoundException("Book is not loaned to any student");
        }

        Optional<Transaction> optionalTransaction = transactionRepository.findLatestTransactionByBookId(bookId);
        if(!optionalTransaction.isPresent()) {
            throw new TransactionNotFoundException("Some error occurred, please check book status database");
        }

        Transaction transaction = optionalTransaction.get();
        BorrowerReturnDTO borrowerReturnDTO = BorrowerReturnDTO.builder().
                studentId(transaction.getLibraryCard().getStudent().getRollNo()).
                studentName(transaction.getLibraryCard().getStudent().getName()).
                studentLibraryCardId(transaction.getLibraryCard().getCardNo()).
                build();

        return borrowerReturnDTO;
    }

}
