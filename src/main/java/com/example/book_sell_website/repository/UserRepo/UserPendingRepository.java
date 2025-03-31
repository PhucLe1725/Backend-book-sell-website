package com.example.book_sell_website.repository.UserRepo;

import com.example.book_sell_website.entity.User;
import com.example.book_sell_website.entity.pendingUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPendingRepository extends JpaRepository<pendingUser, Integer> {
    pendingUser findUserByPhone(String phone);
    pendingUser findUserByMail(String mail);
    void delete(pendingUser user);
}
