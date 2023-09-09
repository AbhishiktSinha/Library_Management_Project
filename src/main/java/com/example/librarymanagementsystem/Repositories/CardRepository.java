package com.example.librarymanagementsystem.Repositories;

import com.example.librarymanagementsystem.Models.LibraryCard;
import com.example.librarymanagementsystem.Models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<LibraryCard,Integer> {

    @Query(value = "select student_roll_no from library_card where card_status = 'BLOCKED'", nativeQuery = true)
    public List<Integer> studentIdWithCardNotActive();

    @Query(value = "select * from library_card where card_status = 'NEW' limit 1", nativeQuery = true)
    public Optional<LibraryCard> findNextAvailableCard();
}
