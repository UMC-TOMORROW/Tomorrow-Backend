package com.umc.tomorrow.domain.member.dto.request;

import com.umc.tomorrow.domain.member.entity.MemberType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateMemberTypeRequestDTO {

    @Schema(description = "회원 역할 (EMPLOYER|JOB_SEEKER)", example = "EMPLOYER")
    @NotNull
    private MemberType memberType;
}
