package com.umc.tomorrow.domain.job.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "근무 환경 DTO")
public class WorkEnvironmentRequestDTO {

    @Schema(
            description = "앉아서 근무가능",
            example = "canWorkSitting",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @NotNull
    private Boolean canWorkSitting;

    @Schema(
            description = "서서근무",
            example = "canWorkStanding",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @NotNull
    private Boolean canWorkStanding;

    @Schema(
            description = "물건 문반",
            example = "canCarryObjects",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @NotNull
    private Boolean canCarryObjects;

    @Schema(
            description = "활동적인 환경",
            example = "canMoveActively",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @NotNull
    private Boolean canMoveActively;

    @Schema(
            description = "커뮤니케이션 중심",
            example = "canCommunicate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @NotNull
    private Boolean canCommunicate;
}
