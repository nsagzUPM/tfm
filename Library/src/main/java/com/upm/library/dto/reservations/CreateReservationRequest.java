package com.upm.library.dto.reservations;

import jakarta.validation.constraints.NotNull;

public record CreateReservationRequest(@NotNull Long copyId) {}
