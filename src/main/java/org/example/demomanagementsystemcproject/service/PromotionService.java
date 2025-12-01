package org.example.demomanagementsystemcproject.service;

import org.example.demomanagementsystemcproject.dto.PromotionDTO;

import java.math.BigDecimal;
import java.util.List;

public interface PromotionService {

    List<PromotionDTO> getAllPromotions();
    PromotionDTO getPromotionById(Long id);
    PromotionDTO createPromotion(PromotionDTO promotionDTO);
    PromotionDTO updatePromotion(Long id, PromotionDTO promotionDTO);
    void deletePromotion(Long id);
    void updatePromotionStatus(Long id, Integer status);

    List<PromotionDTO> getActivePromotions();
    List<PromotionDTO> getPromotionsByType(String type);

    BigDecimal calculatePromotionPrice(BigDecimal originalPrice, Integer quantity, Long productId, Long categoryId);
}