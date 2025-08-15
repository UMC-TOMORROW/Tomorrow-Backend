/**
 * 채팅방 참여 데이터 접근 레이어
 * 작성자: 이승주
 * 작성일: 2025-08-06
 */
package com.umc.tomorrow.domain.chatting.repository;

import com.umc.tomorrow.domain.chatting.entity.ChatPart;
import com.umc.tomorrow.domain.chatting.entity.ChattingRoom;
import com.umc.tomorrow.domain.member.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatPartRepository extends JpaRepository<ChatPart, Long> {
    Optional<ChatPart> findByUserIdAndChattingRoomId(Long userId, Long chattingRoomId);
    boolean existsByUserAndChattingRoom(User user, ChattingRoom room);
    int countByChattingRoom(ChattingRoom room);
}
