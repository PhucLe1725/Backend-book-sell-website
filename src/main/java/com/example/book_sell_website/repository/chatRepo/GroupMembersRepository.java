package com.example.book_sell_website.repository.chatRepo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.book_sell_website.entity.chatEntity.GroupMembers;
import com.example.book_sell_website.entity.chatEntity.GroupMembersId;
public interface GroupMembersRepository extends JpaRepository<GroupMembers, GroupMembersId> {

    boolean existsById_GroupIdAndId_UserId(int groupId, int userId);
}
