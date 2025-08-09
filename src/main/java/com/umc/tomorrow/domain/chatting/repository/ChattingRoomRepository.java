/**
 * 채팅방 데이터 접근 레이어
 * 작성자: 이승주
 * 작성일: 2025-08-06
 */
package com.umc.tomorrow.domain.chatting.repository;

import com.umc.tomorrow.domain.chatting.entity.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long> {
}
