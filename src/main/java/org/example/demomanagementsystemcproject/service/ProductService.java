package org.example.demomanagementsystemcproject.service;

import org.example.demomanagementsystemcproject.dto.ProductDTO;
import org.example.demomanagementsystemcproject.dto.ProductQueryDTO;
import org.example.demomanagementsystemcproject.entity.ProductEntity;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ProductService {

    // 商品列表（分页+搜索）
    Page<ProductDTO> getProducts(ProductQueryDTO query);

    // 获取商品详情
    ProductDTO getProductById(Long id);

    // 添加商品
    ProductDTO createProduct(ProductDTO productDTO);

    // 更新商品
    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    // 删除商品
    void deleteProduct(Long id);

    // 批量删除
    void batchDelete(List<Long> ids);

    // 上架/下架商品
    void updateStatus(Long id, Integer status);

    // 批量更新状态
    void batchUpdateStatus(List<Long> ids, Integer status);

    // 更新库存
    void updateStock(Long id, Integer stock);

    // 批量更新库存
    void batchUpdateStock(Map<Long, Integer> stockMap);

    // 设置库存预警阈值
    void setWarningThreshold(Long id, Integer threshold);

    // 获取低库存商品
    List<ProductDTO> getLowStockProducts();
}