package com.pedro.dev.couponservice.repository;

import com.pedro.dev.couponservice.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    @Query(value = "SELECT count(*) > 0 FROM coupon WHERE id = :id AND deleted = true", nativeQuery = true)
    boolean isAlreadyDeleted(UUID id);

    Page<Coupon> findByCodeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String code, String description, Pageable pageable
    );
}