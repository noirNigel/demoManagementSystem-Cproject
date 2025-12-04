package org.example.demomanagementsystemcproject.service;

import org.example.demomanagementsystemcproject.dto.OrderDTO;
import org.example.demomanagementsystemcproject.dto.OrderQueryDTO;
import org.example.demomanagementsystemcproject.dto.RefundRequestDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    // 订单列表（分页+搜索）
    Page<OrderDTO> getOrders(OrderQueryDTO query);

    // 创建订单（小程序）
    OrderDTO createOrder(OrderDTO request);

    // 获取订单详情
    OrderDTO getOrderById(Long id);

    // 获取订单详情（通过订单号）
    OrderDTO getOrderByNo(String orderNo);

    // 确认订单
    void confirmOrder(Long id);

    // 批量确认订单
    void batchConfirmOrders(List<Long> ids);

    // 完成订单
    void completeOrder(Long id);

    // 取消订单
    void cancelOrder(Long id, String reason);

    // 申请退款
    void requestRefund(RefundRequestDTO request);

    // 审核退款
    void approveRefund(Long orderId);

    // 拒绝退款
    void rejectRefund(Long orderId, String reason);

    // 获取新订单提醒
    List<OrderDTO> getNewOrderAlerts();

    // 导出订单
    byte[] exportOrders(OrderQueryDTO query);
}