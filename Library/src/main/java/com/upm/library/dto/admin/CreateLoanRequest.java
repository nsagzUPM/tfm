package com.upm.library.dto.admin;

import jakarta.validation.constraints.NotNull;

public record CreateLoanRequest(@NotNull Long userId, @NotNull Long copyId) {}
