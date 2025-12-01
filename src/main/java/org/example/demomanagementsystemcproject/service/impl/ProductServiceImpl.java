package org.example.demomanagementsystemcproject.service.impl;

import org.example.demomanagementsystemcproject.dto.ProductDTO;
import org.example.demomanagementsystemcproject.dto.ProductQueryDTO;
import org.example.demomanagementsystemcproject.entity.ProductEntity;
import org.example.demomanagementsystemcproject.repo.ProductRepository;
import org.example.demomanagementsystemcproject.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Page<ProductDTO> getProducts(ProductQueryDTO query) {
        Pageable pageable = PageRequest.of(query.getPage() - 1, query.getSize());

        Specification<ProductEntity> spec = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query.getName() != null && !query.getName().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + query.getName() + "%"));
            }

            if (query.getSku() != null && !query.getSku().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("sku"), "%" + query.getSku() + "%"));
            }

            if (query.getCategoryId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoryId"), query.getCategoryId()));
            }

            if (query.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), query.getStatus()));
            }

            if (query.getLowStock() != null && query.getLowStock() == 1) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("stock"), root.get("warningThreshold")));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return productRepository.findAll(spec, pageable).map(this::convertToDTO);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        return convertToDTO(entity);
    }

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        // 检查SKU是否重复
        ProductEntity existingProduct = productRepository.findBySku(productDTO.getSku());
        if (existingProduct != null) {
            throw new RuntimeException("SKU已存在");
        }

        ProductEntity entity = new ProductEntity();
        BeanUtils.copyProperties(productDTO, entity);
        ProductEntity saved = productRepository.save(entity);
        return convertToDTO(saved);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 检查SKU是否重复（排除自己）
        ProductEntity skuProduct = productRepository.findBySku(productDTO.getSku());
        if (skuProduct != null && !skuProduct.getId().equals(id)) {
            throw new RuntimeException("SKU已存在");
        }

        BeanUtils.copyProperties(productDTO, entity, "id", "createdAt");
        ProductEntity saved = productRepository.save(entity);
        return convertToDTO(saved);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("商品不存在");
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        productRepository.deleteAllById(ids);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        entity.setStatus(status);
        productRepository.save(entity);
    }

    @Override
    @Transactional
    public void batchUpdateStatus(List<Long> ids, Integer status) {
        for (Long id : ids) {
            updateStatus(id, status);
        }
    }

    @Override
    @Transactional
    public void updateStock(Long id, Integer stock) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        entity.setStock(stock);
        productRepository.save(entity);
    }

    @Override
    @Transactional
    public void batchUpdateStock(Map<Long, Integer> stockMap) {
        for (Map.Entry<Long, Integer> entry : stockMap.entrySet()) {
            updateStock(entry.getKey(), entry.getValue());
        }
    }

    @Override
    @Transactional
    public void setWarningThreshold(Long id, Integer threshold) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        entity.setWarningThreshold(threshold);
        productRepository.save(entity);
    }

    @Override
    public List<ProductDTO> getLowStockProducts() {
        return productRepository.findLowStockProducts().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ProductDTO convertToDTO(ProductEntity entity) {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}