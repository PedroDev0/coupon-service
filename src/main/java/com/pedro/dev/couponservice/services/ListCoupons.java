package com.pedro.dev.couponservice.services;

import com.pedro.dev.couponservice.domain.Coupon;
import com.pedro.dev.couponservice.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListCoupons {

    private final CouponRepository repository;

    public Page<Coupon> execute(String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return repository.findByCodeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    search, search, pageable
            );
        }
        return repository.findAll(pageable);
    }
}