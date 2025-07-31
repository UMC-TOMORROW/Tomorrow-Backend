package com.umc.tomorrow.domain.email.enums;

import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.member.entity.User;

public enum EmailContentType implements EmailContentProvider {
    JOB_APPLY {
        @Override
        public String getSubject(User user, Job job) {
            return "[지원 완료] " + user.getName() + "님이 " + job.getTitle() + "에 지원했습니다.";
        }

        @Override
        public String getContent(User user, Job job) {
            return "정상적으로 지원이 완료되었습니다.";
        }
    }
    //여기에 이어서 추가해주시면 됩니다.
}
