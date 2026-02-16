package com.pedro.dev.couponservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedro.dev.couponservice.domain.Coupon;
import com.pedro.dev.couponservice.dto.CouponRequest;
import com.pedro.dev.couponservice.repository.CouponRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CouponIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CouponRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve criar um cupom com sucesso sanitizando o código (Create)")
    void shouldCreateCouponWithSanitizedCode() throws Exception {

        CouponRequest request = new CouponRequest(
                "PROMO@#$1",
                "Desconto de Teste",
                10.0,
                LocalDate.now().plusDays(1)
        );

        mockMvc.perform(post("/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.code").value("PROMO1"))
                .andExpect(jsonPath("$.deleted").value(false));
    }

    @Test
    @DisplayName("Deve retornar erro 400 ao tentar deletar um cupom JÁ DELETADO")
    void shouldReturnBadRequestWhenDeletingAlreadyDeletedCoupon() throws Exception {
        Coupon coupon = createAndSaveCoupon("DEL001", "Para Deletar", 5.0, LocalDate.now().plusDays(5));

        mockMvc.perform(delete("/coupons/" + coupon.getId()))
                .andExpect(status().isNoContent());

        boolean existsFisicamente = repository.isAlreadyDeleted(coupon.getId());
        assertTrue(existsFisicamente, "O registro deveria existir no banco marcado como true");

        mockMvc.perform(delete("/coupons/" + coupon.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Coupon is already deleted."));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar deletar um cupom que NUNCA existiu")
    void shouldReturnNotFoundForNonExistentCoupon() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(delete("/coupons/" + randomId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Coupon not found."));
    }

    @Test
    @DisplayName("Deve validar erro de Negócio: Data no Passado")
    void shouldReturnErrorForPastExpirationDate() throws Exception {
        CouponRequest request = new CouponRequest(
                "FAIL01",
                "Data Passada",
                10.0,
                LocalDate.now().minusDays(1) // Ontem
        );

        mockMvc.perform(post("/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve validar erro de Negócio: Código com tamanho errado após limpeza")
    void shouldReturnErrorForInvalidCodeLength() throws Exception {

        CouponRequest request = new CouponRequest(
                "ABC",
                "Código Curto",
                10.0,
                LocalDate.now().plusDays(1)
        );

        mockMvc.perform(post("/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("O código deve ter exatamente 6 caracteres alfanuméricos após a limpeza."));
    }

    @Test
    @DisplayName("Deve filtrar cupons por código ou descrição")
    void shouldFilterCouponsByName() throws Exception {
        createAndSaveCoupon("NATAL1", "Descrição Padrão", 10.0,LocalDate.now().plusDays(10));

        mockMvc.perform(get("/coupons?search=NAT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1) )
                .andExpect(jsonPath("$.content[0].code").value("NATAL1"));

        mockMvc.perform(get("/coupons?search=padrão"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    private Coupon createAndSaveCoupon(String code, String descripion, Double discountValue, LocalDate expirationDate) {
        Coupon coupon = new Coupon(code, descripion, discountValue, expirationDate);
        return repository.save(coupon);
    }
}