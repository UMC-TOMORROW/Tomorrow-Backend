package com.umc.tomorrow.domain.resume.service;

import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.domain.resume.entity.Resume;
import com.umc.tomorrow.domain.resume.repository.ResumeRepository;
import com.umc.tomorrow.domain.introduction.entity.Introduction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 기존 사용자들의 resumeId 문제를 해결하기 위한 마이그레이션 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeMigrationService {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;

    /**
     * resumeId가 없는 모든 사용자에게 기본 이력서를 생성하고 resumeId를 할당
     */
    @Transactional
    public void migrateUsersWithoutResumeId() {
        log.info("사용자 resumeId 마이그레이션 시작");
        
        List<User> usersWithoutResumeId = userRepository.findByResumeIdIsNull();
        log.info("resumeId가 없는 사용자 수: {}", usersWithoutResumeId.size());
        
        int migratedCount = 0;
        for (User user : usersWithoutResumeId) {
            try {
                // 기본 이력서 생성
                Resume defaultResume = Resume.builder()
                        .user(user)
                        .build();
                
                // 기본 자기소개 생성
                Introduction defaultIntroduction = Introduction.builder()
                        .content("안녕하세요! 저는 " + user.getName() + "입니다.")
                        .resume(defaultResume)
                        .build();
                
                defaultResume.setIntroduction(defaultIntroduction);
                
                // 이력서 저장
                Resume savedResume = resumeRepository.save(defaultResume);
                
                // 사용자의 resumeId 업데이트
                user.setResumeId(savedResume.getId());
                userRepository.save(user);
                
                migratedCount++;
                log.info("사용자 {} (ID: {})에게 기본 이력서 생성 완료, resumeId: {}", 
                        user.getName(), user.getId(), savedResume.getId());
                
            } catch (Exception e) {
                log.error("사용자 {} (ID: {}) 이력서 생성 실패: {}", 
                        user.getName(), user.getId(), e.getMessage(), e);
            }
        }
        
        log.info("사용자 resumeId 마이그레이션 완료. 처리된 사용자 수: {}", migratedCount);
    }

    /**
     * 특정 사용자에게 기본 이력서 생성 및 resumeId 할당
     */
    @Transactional
    public void createDefaultResumeForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        
        if (user.getResumeId() != null) {
            log.info("사용자 {} (ID: {})는 이미 resumeId가 있습니다: {}", 
                    user.getName(), userId, user.getResumeId());
            return;
        }
        
        // 기본 이력서 생성
        Resume defaultResume = Resume.builder()
                .user(user)
                .build();
        
        // 기본 자기소개 생성
        Introduction defaultIntroduction = Introduction.builder()
                .content("안녕하세요! 저는 " + user.getName() + "입니다.")
                .resume(defaultResume)
                .build();
        
        defaultResume.setIntroduction(defaultIntroduction);
        
        // 이력서 저장
        Resume savedResume = resumeRepository.save(defaultResume);
        
        // 사용자의 resumeId 업데이트
        user.setResumeId(savedResume.getId());
        userRepository.save(user);
        
        log.info("사용자 {} (ID: {})에게 기본 이력서 생성 완료, resumeId: {}", 
                user.getName(), userId, savedResume.getId());
    }
}
