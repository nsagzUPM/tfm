package com.upm.library.dto.admin;

import com.upm.library.dto.catalog.CopyDetailsDto;

import java.util.List;

public record AdminLoanView(String userQ,
                            List<UserSummaryDto> users,
                            UserSummaryDto selectedUser,
                            CopyDetailsDto copy,
                            boolean reservedForSelectedUser,
                            boolean userHasPenalty) {



}
