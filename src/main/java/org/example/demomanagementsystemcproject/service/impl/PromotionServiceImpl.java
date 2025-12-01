package org.example.demomanagementsystemcproject.service.impl;

import org.example.demomanagementsystemcproject.dto.PromotionDTO;
import org.example.demomanagementsystemcproject.entity.PromotionEntity;
import org.example.demomanagementsystemcproject.repo.PromotionRepository;
import org.example.demomanagementsystemcproject.service.PromotionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromotionServiceImpl implements PromotionService {

    private static final Logger logger = LoggerFactory.getLogger(PromotionServiceImpl.class);

    private final PromotionRepository promotionRepository;

    public PromotionServiceImpl(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @Override
    public List<PromotionDTO> getAllPromotions() {
        logger.info("开始查询所有促销活动");

        // 修改：查询所有促销活动，不限制状态
        List<PromotionEntity> entities = promotionRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        logger.info("查询到促销活动数量: {}", entities.size());

        List<PromotionDTO> result = entities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        logger.info("转换后返回 {} 条DTO", result.size());
        return result;
    }

    @Override
    public PromotionDTO getPromotionById(Long id) {
        PromotionEntity entity = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("促销活动不存在"));
        return convertToDTO(entity);
    }

    @Override
    @Transactional
    public PromotionDTO createPromotion(PromotionDTO promotionDTO) {
        logger.info("开始创建促销活动: {}", promotionDTO.getName());

        validatePromotion(promotionDTO);

        PromotionEntity entity = new PromotionEntity();
        BeanUtils.copyProperties(promotionDTO, entity);

        // 确保状态不为空，默认为启用状态
        if (entity.getStatus() == null) {
            entity.setStatus(1);
            logger.info("设置默认状态为: 1");
        }

        PromotionEntity saved = promotionRepository.save(entity);
        logger.info("促销活动创建成功, ID: {}", saved.getId());

        return convertToDTO(saved);
    }

    @Override
    @Transactional
    public PromotionDTO updatePromotion(Long id, PromotionDTO promotionDTO) {
        PromotionEntity entity = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("促销活动不存在"));

        validatePromotion(promotionDTO);

        BeanUtils.copyProperties(promotionDTO, entity, "id");
        PromotionEntity saved = promotionRepository.save(entity);
        return convertToDTO(saved);
    }

    @Override
    @Transactional
    public void deletePromotion(Long id) {
        if (!promotionRepository.existsById(id)) {
            throw new RuntimeException("促销活动不存在");
        }
        promotionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updatePromotionStatus(Long id, Integer status) {
        PromotionEntity entity = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("促销活动不存在"));
        entity.setStatus(status);
        promotionRepository.save(entity);
    }

    @Override
    public List<PromotionDTO> getActivePromotions() {
        LocalDateTime now = LocalDateTime.now();
        // 修改：查询状态为1或null的活动
        List<PromotionEntity> entities = promotionRepository.findActiveAndNullStatusPromotions(now);
        return entities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PromotionDTO> getPromotionsByType(String type) {
        // 修改：查询指定类型的所有活动，不限制状态
        List<PromotionEntity> entities = promotionRepository.findByType(type);
        return entities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal calculatePromotionPrice(BigDecimal originalPrice, Integer quantity, Long productId, Long categoryId) {
        List<PromotionDTO> activePromotions = getActivePromotions();
        BigDecimal finalPrice = originalPrice.multiply(BigDecimal.valueOf(quantity));

        for (PromotionDTO promotion : activePromotions) {
            // 检查商品是否适用
            if (!isProductApplicable(promotion, productId, categoryId)) {
                continue;
            }

            // 计算促销价格
            switch (promotion.getType()) {
                case "FULL_REDUCE":
                    if (finalPrice.compareTo(promotion.getConditionAmount()) >= 0) {
                        finalPrice = finalPrice.subtract(promotion.getReduceAmount());
                    }
                    break;
                case "SECOND_HALF":
                    if (quantity >= 2) {
                        int halfPriceItems = quantity / 2;
                        BigDecimal discount = originalPrice.multiply(BigDecimal.valueOf(0.5))
                                .multiply(BigDecimal.valueOf(halfPriceItems));
                        finalPrice = finalPrice.subtract(discount);
                    }
                    break;
                case "DISCOUNT":
                    finalPrice = finalPrice.multiply(promotion.getDiscountRate());
                    break;
            }
        }

        return finalPrice.max(BigDecimal.ZERO);
    }

    private boolean isProductApplicable(PromotionDTO promotion, Long productId, Long categoryId) {
        // 如果未设置商品和分类限制，则所有商品都适用
        if ((promotion.getProductIds() == null || promotion.getProductIds().isEmpty()) &&
                (promotion.getCategoryIds() == null || promotion.getCategoryIds().isEmpty())) {
            return true;
        }

        // 检查商品ID
        if (promotion.getProductIds() != null && !promotion.getProductIds().isEmpty()) {
            // 这里需要解析JSON数组，简化处理
            if (promotion.getProductIds().contains(productId.toString())) {
                return true;
            }
        }

        // 检查分类ID
        if (promotion.getCategoryIds() != null && !promotion.getCategoryIds().isEmpty()) {
            // 这里需要解析JSON数组，简化处理
            if (promotion.getCategoryIds().contains(categoryId.toString())) {
                return true;
            }
        }

        return false;
    }

    private void validatePromotion(PromotionDTO promotionDTO) {
        if (promotionDTO.getName() == null || promotionDTO.getName().trim().isEmpty()) {
            throw new RuntimeException("活动名称不能为空");
        }

        if (promotionDTO.getType() == null) {
            throw new RuntimeException("活动类型不能为空");
        }

        if (promotionDTO.getStartTime() == null || promotionDTO.getEndTime() == null) {
            throw new RuntimeException("活动时间不能为空");
        }

        if (promotionDTO.getEndTime().isBefore(promotionDTO.getStartTime())) {
            throw new RuntimeException("结束时间不能早于开始时间");
        }

        // 根据类型验证参数
        switch (promotionDTO.getType()) {
            case "FULL_REDUCE":
                if (promotionDTO.getConditionAmount() == null ||
                        promotionDTO.getConditionAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new RuntimeException("满减活动必须设置条件金额");
                }
                if (promotionDTO.getReduceAmount() == null ||
                        promotionDTO.getReduceAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new RuntimeException("满减活动必须设置减免金额");
                }
                break;
            case "DISCOUNT":
                if (promotionDTO.getDiscountRate() == null ||
                        promotionDTO.getDiscountRate().compareTo(BigDecimal.ZERO) <= 0 ||
                        promotionDTO.getDiscountRate().compareTo(BigDecimal.ONE) > 0) {
                    throw new RuntimeException("折扣活动必须设置有效的折扣率(0-1)");
                }
                break;
        }
    }

    private PromotionDTO convertToDTO(PromotionEntity entity) {
        PromotionDTO dto = new PromotionDTO();
        BeanUtils.copyProperties(entity, dto);

        // 处理状态显示
        Integer status = entity.getStatus();
        if (status != null) {
            dto.setStatusText(status == 1 ? "启用" : "禁用");
        } else {
            dto.setStatusText("未知"); // 处理 null 状态
        }

        // 检查是否进行中
        LocalDateTime now = LocalDateTime.now();
        dto.setIsActive(now.isAfter(entity.getStartTime()) && now.isBefore(entity.getEndTime()));

        return dto;
    }
}