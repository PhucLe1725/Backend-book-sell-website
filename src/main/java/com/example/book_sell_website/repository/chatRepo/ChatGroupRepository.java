package com.example.book_sell_website.repository.chatRepo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.book_sell_website.entity.chatEntity.ChatGroup;

public interface ChatGroupRepository extends JpaRepository<ChatGroup, Integer> {
}
