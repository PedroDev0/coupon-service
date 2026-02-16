package com.pedro.dev.couponservice.controller;

import com.pedro.dev.couponservice.domain.Coupon;
import com.pedro.dev.couponservice.dto.CouponRequest;
import com.pedro.dev.couponservice.services.CreateCoupon;
import com.pedro.dev.couponservice.services.DeleteCoupon;
import com.pedro.dev.couponservice.services.ListCoupons;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupons", description = "Endpoints para gerenciamento de cupons de desconto")
public class CouponController {

    private final CreateCoupon createCoupon;
    private final ListCoupons listCoupons;
    private final DeleteCoupon deleteCoupon;

    @PostMapping
    @Operation(summary = "Criar novo cupom", description = "Cria um cupom aplicando as regras de sanitização de código e validação de data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cupom criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou regra de negócio violada")
    })
    public ResponseEntity<Coupon> create(@RequestBody @Valid CouponRequest request) {
        Coupon createdCoupon = createCoupon.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCoupon);
    }

    @GetMapping
    @Operation(summary = "Listar cupons ativos", description = "Retorna todos os cupons que NÃO foram deletados (Soft Delete).")
    public ResponseEntity<List<Coupon>> listAll() {
        return ResponseEntity.ok(listCoupons.execute());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar cupom", description = "Realiza o Soft Delete. Retorna erro se o cupom já estiver deletado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cupom deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cupom não encontrado"),
            @ApiResponse(responseCode = "400", description = "Cupom já foi deletado anteriormente")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteCoupon.execute(id);
        return ResponseEntity.noContent().build();
    }
}