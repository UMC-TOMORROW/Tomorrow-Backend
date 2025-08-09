/**
 * 커리어톡 Entity
 * - 자신의 커리어를 기록
 * 작성자: 이승주
 * 생성일: 2025-07-10
 * 수정일: 2025-07-20
 */
package com.umc.tomorrow.domain.careertalk.entity;

import com.umc.tomorrow.domain.chatting.entity.ChattingRoom;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.global.common.base.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Careertalk extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category", length = 20, nullable = false)
    private String category;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "careertalk", cascade = CascadeType.ALL, orphanRemoval = true)
    private ChattingRoom chattingRoom;

    public void update(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }


}
