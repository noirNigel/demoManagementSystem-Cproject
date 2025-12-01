package org.example.demomanagementsystemcproject.service;

import org.example.demomanagementsystemcproject.dto.PointsGoodsDTO;
import org.example.demomanagementsystemcproject.dto.PointsExchangeDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PointsMallService {

    // 积分商品管理
    List<PointsGoodsDTO> getAllGoods();
    Page<PointsGoodsDTO> getGoods(int page, int size);
    PointsGoodsDTO getGoodsById(Long id);
    PointsGoodsDTO createGoods(PointsGoodsDTO goodsDTO);
    PointsGoodsDTO updateGoods(Long id, PointsGoodsDTO goodsDTO);
    void deleteGoods(Long id);
    void updateGoodsStatus(Long id, Integer status);

    // 积分兑换
    List<PointsGoodsDTO> getAvailableGoods();
    PointsExchangeDTO exchangeGoods(Long userId, Long goodsId, Integer quantity, String address);
    List<PointsExchangeDTO> getUserExchanges(Long userId);
    Page<PointsExchangeDTO> getUserExchangesPage(Long userId, int page, int size);
    void updateExchangeStatus(Long exchangeId, String status);

    // 工具方法
    boolean canUserExchange(Long userId, Long goodsId, Integer quantity);
}