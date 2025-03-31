package com.example.book_sell_website.controller.ChatController;

import com.example.book_sell_website.entity.User;
import com.example.book_sell_website.entity.chatEntity.SupportMessage;
import com.example.book_sell_website.repository.UserRepo.UserRepository;
import com.example.book_sell_website.dto.chatDTO.ChatHistoryRequestDTO;
import com.example.book_sell_website.dto.chatDTO.ChatReplyRequestDTO;
import com.example.book_sell_website.dto.chatDTO.ChatSendRequestDTO;
import com.example.book_sell_website.service.ChatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat/admin")
public class WithAdminController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserRepository userRepository;

    // 📨 1. Người dùng gửi tin nhắn cho admin
    @PostMapping("/send")
    public ResponseEntity<SupportMessage> sendMessageToAdmin(@RequestBody ChatSendRequestDTO request) {
        User sender = userRepository.findById(request.getSenderId()).orElseThrow();
        User admin = userRepository.findAll()
            .stream().filter(User::isAdmin).findFirst()
            .orElseThrow(() -> new RuntimeException("Admin not found"));

        SupportMessage result = chatService.sendPrivateMessage(sender, admin, request.getMessage());
        return ResponseEntity.ok(result);
    }

    // 📥 2. Admin phản hồi người dùng
    @PostMapping("/reply")
    public ResponseEntity<SupportMessage> adminReplyToUser(@RequestBody ChatReplyRequestDTO request) {
        User admin = userRepository.findById(request.getAdminId()).orElseThrow();
        User user = userRepository.findById(request.getUserId()).orElseThrow();

        if (!admin.isAdmin()) {
            throw new RuntimeException("Sender is not admin");
        }

        SupportMessage result = chatService.sendPrivateMessage(admin, user, request.getMessage());
        return ResponseEntity.ok(result);
    }

    // 📄 3. Lấy lịch sử chat giữa user và admin
    @PostMapping("/history")
    public ResponseEntity<List<SupportMessage>> getChatWithAdmin(@RequestBody ChatHistoryRequestDTO request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow();
        User admin = userRepository.findAll().stream().filter(User::isAdmin).findFirst()
            .orElseThrow(() -> new RuntimeException("Admin not found"));

        List<SupportMessage> history = chatService.getPrivateMessages(user, admin);
        return ResponseEntity.ok(history);
    }
}
