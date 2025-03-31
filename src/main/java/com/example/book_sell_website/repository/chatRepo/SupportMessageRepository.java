package com.example.book_sell_website.repository.chatRepo;
import com.example.book_sell_website.entity.User;
import com.example.book_sell_website.entity.chatEntity.ChatGroup;
import com.example.book_sell_website.entity.chatEntity.SupportMessage;

import org.springframework.data.jpa.repository.JpaRepository;
    import java.util.List;

    public interface SupportMessageRepository extends JpaRepository<SupportMessage, Integer> {
        List<SupportMessage> findBySenderAndReceiver(User sender, User receiver);
        List<SupportMessage> findByChatGroup(ChatGroup chatGroup);
        List<SupportMessage> findBySender(User sender);
        List<SupportMessage> findByReceiver(User receiver);
        
    }
