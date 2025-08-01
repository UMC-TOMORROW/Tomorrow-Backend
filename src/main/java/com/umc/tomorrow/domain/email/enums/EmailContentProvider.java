package com.umc.tomorrow.domain.email.enums;

import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.member.entity.User;

public interface EmailContentProvider {
    String getSubject(User user, Job job);
    String getContent(User user, Job job);
}
