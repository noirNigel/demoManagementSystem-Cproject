package org.example.demomanagementsystemcproject.controller;

import org.example.demomanagementsystemcproject.dto.PromotionDTO;
import org.example.demomanagementsystemcproject.service.PromotionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/marketing/promotions", "/api/promotions"})
public class PromotionController {

    private static final Logger logger = LoggerFactory.getLogger(PromotionController.class);

    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping
    public ResponseEntity<List<PromotionDTO>> getAllPromotions() {
        logger.info("开始查询所有促销活动");

        List<PromotionDTO> promotions = promotionService.getAllPromotions();
        logger.info("查询到促销活动数量: {}", promotions.size());

        for (PromotionDTO promotion : promotions) {
            logger.info("促销活动: ID={}, 名称={}, 状态={}",
                    promotion.getId(), promotion.getName(), promotion.getStatus());
        }

        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromotionDTO> getPromotionById(@PathVariable Long id) {
        return ResponseEntity.ok(promotionService.getPromotionById(id));
    }

    @PostMapping
    public ResponseEntity<PromotionDTO> createPromotion(@RequestBody PromotionDTO promotionDTO) {
        return ResponseEntity.ok(promotionService.createPromotion(promotionDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromotionDTO> updatePromotion(@PathVariable Long id, @RequestBody PromotionDTO promotionDTO) {
        return ResponseEntity.ok(promotionService.updatePromotion(id, promotionDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updatePromotionStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        promotionService.updatePromotionStatus(id, body.get("status"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/active")
    public ResponseEntity<List<PromotionDTO>> getActivePromotions() {
        return ResponseEntity.ok(promotionService.getActivePromotions());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<PromotionDTO>> getPromotionsByType(@PathVariable String type) {
        return ResponseEntity.ok(promotionService.getPromotionsByType(type));
    }

    @PostMapping("/calculate-price")
    public ResponseEntity<Map<String, Object>> calculatePromotionPrice(@RequestBody Map<String, Object> body) {
        try {
            BigDecimal originalPrice = new BigDecimal(body.get("originalPrice").toString());
            Integer quantity = (Integer) body.get("quantity");
            Long productId = body.get("productId") != null ? Long.valueOf(body.get("productId").toString()) : null;
            Long categoryId = body.get("categoryId") != null ? Long.valueOf(body.get("categoryId").toString()) : null;

            BigDecimal promotionPrice = promotionService.calculatePromotionPrice(originalPrice, quantity, productId, categoryId);

            return ResponseEntity.ok(Map.of(
                    "originalPrice", originalPrice,
                    "promotionPrice", promotionPrice,
                    "discount", originalPrice.multiply(BigDecimal.valueOf(quantity)).subtract(promotionPrice),
                    "quantity", quantity
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }
}