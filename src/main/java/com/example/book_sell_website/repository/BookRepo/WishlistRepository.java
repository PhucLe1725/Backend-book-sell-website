package com.example.book_sell_website.repository.BookRepo;

import com.example.book_sell_website.entity.Book;
import com.example.book_sell_website.entity.WishList.Wishlist;
import com.example.book_sell_website.entity.WishList.WishlistId;

import jakarta.transaction.Transactional;

import com.example.book_sell_website.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, WishlistId> {

    // Kiểm tra xem sách đã có trong danh sách yêu thích chưa
    boolean existsByUserAndBook(User user, Book book);

    // Tìm danh sách sách yêu thích của người dùng
    @Query("SELECT b FROM Book b JOIN Wishlist w ON b.ID = w.book.ID WHERE w.user.ID = ?1")
    List<Book> findBooksByUserId(int userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Wishlist w WHERE w.user.ID = ?1 AND w.book.ID = ?2")
    void deleteByUserIdAndBookId(int userId, int bookId);
}
