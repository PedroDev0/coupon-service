package com.pedro.dev.couponservice.services;

import com.pedro.dev.couponservice.domain.Coupon;
import com.pedro.dev.couponservice.dto.CouponRequest;
import com.pedro.dev.couponservice.repository.CouponRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCoupon {

    private final CouponRepository repository;

    @Transactional
    public Coupon execute(CouponRequest request) {
        Coupon newCoupon = new Coupon(
                request.code(),
                request.description(),
                request.discountValue(),
                request.expirationDate()
        );

        return repository.save(newCoupon);
    }
}