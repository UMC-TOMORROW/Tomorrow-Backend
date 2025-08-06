/**
 * 메시지 데이터 접근 레이어
 * 작성자: 이승주
 * 작성일: 2025-08-06
 */
package com.umc.tomorrow.domain.chatting.repository;

import com.umc.tomorrow.domain.chatting.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // 최신 메시지부터 조회 (최초 요청 시)
    @Query("SELECT m FROM Message m JOIN FETCH m.chatPart WHERE m.chattingRoom.id = :chattingRoomId ORDER BY m.id DESC")
    Slice<Message> findByChattingRoomIdOrderByIdDesc(@Param("chattingRoomId") Long chattingRoomId, Pageable pageable);

    @Query("SELECT m FROM Message m JOIN FETCH m.chatPart WHERE m.chattingRoom.id = :chattingRoomId AND m.id < :cursor ORDER BY m.id DESC")
    Slice<Message> findByChattingRoomIdAndIdLessThanOrderByIdDesc(@Param("chattingRoomId") Long chattingRoomId, @Param("cursor") Long cursor, Pageable pageable);

}

