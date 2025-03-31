package com.example.book_sell_website.repository.BookRepo;

import com.example.book_sell_website.entity.Book;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
        List<Book> findByCategory(String category);

}
