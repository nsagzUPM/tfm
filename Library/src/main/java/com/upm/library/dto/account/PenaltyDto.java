package com.upm.library.dto.account;

import com.upm.library.domain.Penalty;

public record PenaltyDto(Long id, String startDate, String endDate, boolean active, String reason) {
    public static PenaltyDto from(Penalty p) {
        return new PenaltyDto(
                p.getId(),
                p.getStartDate().toString(),
                p.getEndDate().toString(),
                p.isActive(),
                p.getReason()
        );
    }
}

