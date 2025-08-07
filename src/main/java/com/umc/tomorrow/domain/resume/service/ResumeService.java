/**
 * 이력서 서비스
 * - 이력서 저장 비즈니스 로직 작성
 * 작성자: 정여진
 * 생성일: 2025-07-20
 */
package com.umc.tomorrow.domain.resume.service;

import com.umc.tomorrow.domain.member.exception.MemberException;
import com.umc.tomorrow.domain.member.exception.code.MemberStatus;
import com.umc.tomorrow.domain.resume.dto.request.ResumeSaveRequestDTO;
import com.umc.tomorrow.domain.resume.entity.Resume;
import com.umc.tomorrow.domain.resume.repository.ResumeRepository;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.domain.resume.converter.ResumeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    @Autowired
    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
    }

    /**
     * 이력서 저장
     * @param userId 사용자 ID
     * @param dto 저장 요청 DTO
     * @return 저장된 Resume 엔티티
     */
    @Transactional
    public Resume saveResume(Long userId, ResumeSaveRequestDTO dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new MemberException(MemberStatus.MEMBER_NOT_FOUND));
        Resume resume = ResumeConverter.toEntity(dto, user);
        return resumeRepository.save(resume);
    }
} 