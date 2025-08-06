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
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // 최신 메시지부터 조회 (최초 요청 시)
    Slice<Message> findByChattingRoomIdOrderByIdDesc(Long chattingRoomId, Pageable pageable);

    // 커서 기반 메시지 조회 (무한 스크롤)
    Slice<Message> findByChattingRoomIdAndIdLessThanOrderByIdDesc(Long chattingRoomId, Long cursor, Pageable pageable);
}

