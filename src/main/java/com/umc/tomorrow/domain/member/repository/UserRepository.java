/**
 * 회원(User) 엔티티의 데이터 접근을 위한 JpaRepository 인터페이스
 * - Spring Data JPA를 이용하여 데이터베이스와의 상호작용을 추상화
 *
 * 작성자: 정여진진
 * 생성일: 2025-07-10
 */
package com.umc.tomorrow.domain.member.repository;

import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
  
    /**
     * 사용자 이름(username)으로 User 엔티티를 조회합니다.
     * @param username 조회할 사용자의 이름
     * @return 주어진 username에 해당하는 User 엔티티. 없으면 null 반환.
     */
    User findByUsername(String username);

    User findByProviderAndProviderUserId(Provider provider, String providerUserId);
    
    /**
     * resumeId가 null인 사용자들을 조회합니다.
     * @return resumeId가 null인 사용자 목록
     */
    List<User> findByResumeIdIsNull();
    
    /**
     * 특정 resumeId를 가진 사용자를 조회합니다.
     * @param resumeId 이력서 ID
     * @return 해당 resumeId를 가진 사용자
     */
    User findByResumeId(Long resumeId);
}
