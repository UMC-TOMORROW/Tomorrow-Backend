/**
 * 채팅방 엔티티 클래스
 * 작성자: 이승주
 * 작성일: 2025-08-06
 */
package com.umc.tomorrow.domain.chatting.entity;

import com.umc.tomorrow.domain.careertalk.entity.Careertalk;
import com.umc.tomorrow.global.common.base.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChattingRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "careertalk_id", unique = true)
    private Careertalk careertalk;

    @OneToMany(mappedBy = "chattingRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatPart> chatParts = new ArrayList<>(); //채팅방 삭제시 채팅방 참여도 삭제
}
