/**
 * JobBookmarkQueryServiceImpl
 * - JobBookmark 조회(Read) 로직을 처리하는 서비스 구현체
 * - 사용자의 북마크 목록을 가져와
 * 작성자 : 정여진
 * 작성일 : 2025-08-05
 */
package com.umc.tomorrow.domain.jobbookmark.service.query;

import com.umc.tomorrow.domain.jobbookmark.dto.response.GetJobBookmarkListResponseDTO;
import com.umc.tomorrow.domain.jobbookmark.dto.response.JobBookmarkResponseDTO;
import com.umc.tomorrow.domain.jobbookmark.entity.JobBookmark;
import com.umc.tomorrow.domain.jobbookmark.repository.JobBookmarkRepository;
import com.umc.tomorrow.domain.member.exception.code.MemberErrorStatus;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobBookmarkQueryServiceImpl implements JobBookmarkQueryService {

    private final JobBookmarkRepository jobBookmarkRepository;
    private final UserRepository userRepository;

    @Override
    public GetJobBookmarkListResponseDTO getList(Long userId, Long cursor, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(MemberErrorStatus.MEMBER_NOT_FOUND));

        PageRequest pageRequest = PageRequest.of(0, size);

        Slice<JobBookmark> jobBookmarks = jobBookmarkRepository.findByUserIdAndIdLessThanOrderByIdDesc(userId, cursor, pageRequest);

        List<JobBookmarkResponseDTO> bookmarkDTOs = jobBookmarks.getContent().stream()
                .map(bookmark -> JobBookmarkResponseDTO.builder()
                        .id(bookmark.getId())
                        .jobId(bookmark.getJob().getId())
                        .jobTitle(bookmark.getJob().getTitle())
                        .companyName(bookmark.getJob().getCompanyName())
                        .bookmarkedAt(String.valueOf(bookmark.getCreatedAt()))
                        .build())
                .collect(Collectors.toList());

        Long lastCursor = null;
        if (!jobBookmarks.isEmpty()) {
            lastCursor = jobBookmarks.getContent().get(jobBookmarks.getContent().size() - 1).getId();
        }

        return GetJobBookmarkListResponseDTO.builder()
                .bookmarks(bookmarkDTOs)
                .lastCursor(lastCursor)
                .hasNext(jobBookmarks.hasNext())
                .build();
    }
}
