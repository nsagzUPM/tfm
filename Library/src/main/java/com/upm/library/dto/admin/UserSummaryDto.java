package com.upm.library.dto.admin;

import com.upm.library.domain.Role;
import com.upm.library.domain.User;

public record UserSummaryDto(Long id, String email, String name, Role role) {
    public static com.upm.library.dto.admin.UserSummaryDto from(User u) {
        return new com.upm.library.dto.admin.UserSummaryDto(u.getId(), u.getExternalId(), u.getName(), u.getRol());
    }
}
