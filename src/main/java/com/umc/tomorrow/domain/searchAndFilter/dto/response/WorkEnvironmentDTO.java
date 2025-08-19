package com.umc.tomorrow.domain.searchAndFilter.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkEnvironmentDTO {

    private boolean canWorkSitting;
    private boolean canWorkStanding;
    private boolean canCarryObjects;
    private boolean canMoveActively;
    private boolean canCommunicate;
}
