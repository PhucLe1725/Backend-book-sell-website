package com.example.book_sell_website.controller.ChatController;

import com.example.book_sell_website.entity.*;
import com.example.book_sell_website.entity.chatEntity.ChatGroup;
import com.example.book_sell_website.entity.chatEntity.GroupJoinRequest;
import com.example.book_sell_website.entity.chatEntity.GroupMembers;
import com.example.book_sell_website.entity.chatEntity.SupportMessage;
import com.example.book_sell_website.entity.chatEntity.GroupJoinRequest.RequestStatus;
import com.example.book_sell_website.dto.chatDTO.ApproveRequestDTO;
import com.example.book_sell_website.dto.chatDTO.GroupChatRequestDTO;
import com.example.book_sell_website.dto.chatDTO.GroupHistoryRequestDTO;
import com.example.book_sell_website.dto.chatDTO.JoinRequestDTO;
import com.example.book_sell_website.repository.UserRepo.UserRepository;
import com.example.book_sell_website.repository.chatRepo.ChatGroupRepository;
import com.example.book_sell_website.repository.chatRepo.GroupJoinRequestRepository;
import com.example.book_sell_website.repository.chatRepo.GroupMembersRepository;
import com.example.book_sell_website.service.ChatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat/community")
public class WithCommunity {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatGroupRepository chatGroupRepository;

    @Autowired
    private GroupMembersRepository groupMembersRepository;

    @Autowired
    private GroupJoinRequestRepository groupJoinRequestRepository;

    // 📩 Gửi tin nhắn vào group. Dữ liệu cần sẽ là 

    @PostMapping("/send")
    public ResponseEntity<SupportMessage> sendGroupMessage(@RequestBody GroupChatRequestDTO request) {
        // Kiểm tra xem người gửi có tồn tại không
        User sender = userRepository.findById(request.getSenderId())
            .orElseThrow(() -> new RuntimeException("User with ID " + request.getSenderId() + " not found"));

        // Kiểm tra xem nhóm có tồn tại không
        ChatGroup group = chatGroupRepository.findById(request.getGroupId())
            .orElseThrow(() -> new RuntimeException("Group with ID " + request.getGroupId() + " not found"));

        // Gửi tin nhắn nhóm
        SupportMessage result = chatService.sendGroupMessage(sender, group, request.getMessage());
        return ResponseEntity.ok(result);
    }

    // 📜 Xem lịch sử tin nhắn group
    @PostMapping("/history")
    public ResponseEntity<List<SupportMessage>> getGroupMessages(@RequestBody GroupHistoryRequestDTO request) {
        // Kiểm tra xem nhóm có tồn tại không
        ChatGroup group = chatGroupRepository.findById(request.getGroupId())
            .orElseThrow(() -> new RuntimeException("Group with ID " + request.getGroupId() + " not found"));

        // Lấy tất cả tin nhắn trong nhóm
        List<SupportMessage> messages = chatService.getGroupMessages(group);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/group/{groupId}/joinRequest")
    public ResponseEntity<?> sendJoinRequest(@PathVariable int groupId, @RequestBody JoinRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
    
        ChatGroup group = chatGroupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("Group not found"));
    
        // Kiểm tra nếu người dùng đã là thành viên của nhóm
        if (groupMembersRepository.existsById_GroupIdAndId_UserId(groupId, request.getUserId())) {
            return ResponseEntity.badRequest().body("User is already a member of the group.");
        }
    
        // Lưu yêu cầu tham gia nhóm
        GroupJoinRequest joinRequest = new GroupJoinRequest();
        joinRequest.setUser(user);
        joinRequest.setGroup(group);
        joinRequest.setStatus(RequestStatus.PENDING);
        groupJoinRequestRepository.save(joinRequest);
    
        return ResponseEntity.ok("Request to join group " + groupId + " has been submitted successfully and is pending approval.");
    }
    

    @PostMapping("/group/{groupId}/joinRequest/{requestId}/approve")
    public ResponseEntity<?> approveJoinRequest(@PathVariable int groupId, @PathVariable int requestId, @RequestBody ApproveRequestDTO request) {
        GroupJoinRequest groupJoinRequest = groupJoinRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Request not found"));

        if (groupJoinRequest.getGroup().getGroupId() != groupId) {
            return ResponseEntity.badRequest().body("Request does not belong to this group.");
        }

        // Admin phê duyệt yêu cầu
        groupJoinRequest.setStatus(RequestStatus.valueOf(request.getStatus()));  // Chuyển status từ String sang Enum
        groupJoinRequest.setApprovedBy(userRepository.findById(request.getAdminId())
            .orElseThrow(() -> new RuntimeException("Admin not found")));
        groupJoinRequestRepository.save(groupJoinRequest);
    
        // Nếu được phê duyệt, thêm người vào nhóm
        if (RequestStatus.APPROVED.equals(groupJoinRequest.getStatus())) {
            GroupMembers groupMember = new GroupMembers(groupJoinRequest.getGroup(), groupJoinRequest.getUser());
            groupMembersRepository.save(groupMember);
        }
    
        return ResponseEntity.ok("User request with ID " + requestId + " has been " + groupJoinRequest.getStatus() + " successfully.");
    }

}
