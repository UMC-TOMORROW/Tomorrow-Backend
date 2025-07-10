/**
 * 회원 정보 서비스
 * - 회원 정보 수정 등 비즈니스 로직 처리
 *
 * 작성자: 정여진
 * 생성일: 2025-07-10
 */
package com.umc.tomorrow.domain.member.service;

import com.umc.tomorrow.domain.member.dto.UserDTO;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final UserRepository userRepository;

    @Autowired
    public MemberService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 회원 정보 수정
     * @param currentUser 현재 로그인한 회원의 DTO (id 포함)
     * @param userDTO 수정할 정보가 담긴 DTO
     * @return 수정된 회원 정보 DTO
     */
    @Transactional
    public UserDTO updateUser(UserDTO currentUser, UserDTO userDTO) {
        Long userId = currentUser.getId();
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        user.setUsername(userDTO.getUsername());
        user.setName(userDTO.getName());
        user.setRole(userDTO.getRole());
        user.setRefreshToken(userDTO.getRefreshToken());
        // 변경된 엔티티를 저장 
        return toDTO(user);
    }

    /**
     * User 엔티티를 UserDTO로 변환
     */
    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setRole(user.getRole());
        dto.setRefreshToken(user.getRefreshToken());
        return dto;
    }
} 