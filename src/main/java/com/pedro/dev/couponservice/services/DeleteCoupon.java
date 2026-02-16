package com.pedro.dev.couponservice.services;

import com.pedro.dev.couponservice.repository.CouponRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteCoupon {

    private final CouponRepository repository;

    @Transactional
    public void execute(UUID id) {
        var coupon = repository.findById(id);

        if (coupon.isPresent()) {
            repository.delete(coupon.get());
            return; // Sucesso, sai do método.
        }

        if (repository.isAlreadyDeleted(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon is already deleted.");
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon not found.");
    }
}