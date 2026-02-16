package com.pedro.dev.couponservice.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CouponRequest(
        @NotNull String code,
        @NotNull String description,
        @NotNull Double discountValue,
        @NotNull @FutureOrPresent LocalDate expirationDate
) {}