package com.umc.tomorrow.domain.email.enums;

import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.member.entity.User;

public enum EmailContentType implements EmailContentProvider {
    JOB_APPLY {
        @Override
        public String getSubject(User user, Job job) {
            String userName = user.getName() != null ? user.getName() : "지원자";
            String jobTitle = job.getTitle() != null ? job.getTitle() : "공고명 없음";
            return "[내일] " + userName + "님이 " + jobTitle + "에 지원했습니다.";
        }

        @Override
        public String getContent(User user, Job job) {
            return "정상적으로 지원이 완료되었습니다.";
        }
    },

    JOB_ACCEPTED {
        @Override
        public String getSubject(User user, Job job) {
            return "[내일] 전형 결과 안내드립니다.";
        }

        @Override
        public String getContent(User user, Job job) {
            return "축하합니다! 이번 전형에서 합격하셨습니다.";
        }
    },

    JOB_REJECTED {
        @Override
        public String getSubject(User user, Job job) {
            return "[내일] 전형 결과 안내드립니다.";
        }

        @Override
        public String getContent(User user, Job job) {
            return "아쉽지만 이번 전형에서는 함께하지 못하게 되었습니다.";
        }
    };
}
