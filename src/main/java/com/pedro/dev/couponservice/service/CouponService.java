package com.pedro.dev.couponservice.service;

import com.pedro.dev.couponservice.domain.Coupon;
import com.pedro.dev.couponservice.dto.CouponRequest;
import com.pedro.dev.couponservice.repository.CouponRepository;
import jakarta.transaction.Transactional; // Importante para garantir atomicidade
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository repository;

    @Transactional
    public Coupon create(CouponRequest request) {
        Coupon newCoupon = new Coupon(
                request.code(),
                request.description(),
                request.discountValue(),
                request.expirationDate()
        );

        return repository.save(newCoupon);
    }

    public List<Coupon> listAll() {
        return repository.findAll();
    }

    @Transactional
    public void delete(UUID id) {

        Optional<Coupon> coupon = repository.findById(id);

        if (coupon.isPresent()) {
            repository.delete(coupon.get());
            return;
        }

        if (repository.isAlreadyDeleted(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon is already deleted.");
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon not found.");
    }
}