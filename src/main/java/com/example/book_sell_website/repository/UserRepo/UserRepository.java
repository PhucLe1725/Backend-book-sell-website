package com.example.book_sell_website.repository.UserRepo;

import com.example.book_sell_website.entity.User;
import com.example.book_sell_website.entity.pendingUser;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
        public Optional<User> findUserByPhone(String phone);
        public Optional<User> findUserByMail(String mail);

}
