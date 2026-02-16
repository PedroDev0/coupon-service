package com.pedro.dev.couponservice.services;

import com.pedro.dev.couponservice.domain.Coupon;
import com.pedro.dev.couponservice.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListCoupons {

    private final CouponRepository repository;

    public List<Coupon> execute() {
        return repository.findAll();
    }
}