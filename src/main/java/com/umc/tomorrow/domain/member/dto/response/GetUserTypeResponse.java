package com.umc.tomorrow.domain.member.dto.response;

import com.umc.tomorrow.domain.member.entity.MemberType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetUserTypeResponse {

    private MemberType memberType;

}
