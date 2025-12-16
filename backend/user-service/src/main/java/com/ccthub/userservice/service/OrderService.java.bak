package com.ccthub.userservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.dto.OrderRequest;
import com.ccthub.userservice.dto.OrderResponse;
import com.ccthub.userservice.entity.Order;
import com.ccthub.userservice.entity.OrderItem;
import com.ccthub.userservice.entity.TicketPrice;
import com.ccthub.userservice.repository.OrderItemRepository;
import com.ccthub.userservice.repository.OrderRepository;
import com.ccthub.userservice.repository.TicketPriceRepository;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;

/**
 * 订单服务
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final TicketPriceRepository ticketPriceRepository;

    /**
     * 创建订单（购票流程）
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrder(OrderRequest request) {
        // 1. 校验库存并锁定
        List<TicketPrice> ticketPrices = new ArrayList<>();
        for (OrderRequest.VisitorInfo visitor : request.getVisitors()) {
            TicketPrice ticketPrice = ticketPriceRepository.findById(visitor.getTicketPriceId())
                    .orElseThrow(() -> new RuntimeException("票价不存在：" + visitor.getTicketPriceId()));

            // 检查库存
            if (ticketPrice.getInventoryAvailable() <= 0) {
                throw new RuntimeException("库存不足：" + ticketPrice.getId());
            }

            // 扣减库存（乐观锁）
            try {
                ticketPrice.setInventoryAvailable(ticketPrice.getInventoryAvailable() - 1);
                ticketPrice.setInventoryLocked(ticketPrice.getInventoryLocked() + 1);
                ticketPriceRepository.save(ticketPrice);
                ticketPrices.add(ticketPrice);
            } catch (OptimisticLockException e) {
                throw new RuntimeException("库存扣减失败，请重试");
            }
        }

        // 2. 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(request.getUserId());
        order.setScenicSpotId(request.getScenicSpotId());
        order.setTicketId(request.getTicketId());
        order.setVisitDate(request.getVisitDate());
        order.setVisitorCount(request.getVisitors().size());
        order.setContactName(request.getContactName());
        order.setContactPhone(request.getContactPhone());
        order.setRemark(request.getRemark());

        // 计算金额
        BigDecimal totalAmount = request.getVisitors().stream()
                .map(OrderRequest.VisitorInfo::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setActualAmount(totalAmount);
        order.setStatus(Order.OrderStatus.PENDING_PAYMENT);

        Order savedOrder = orderRepository.save(order);

        // 3. 创建订单项（电子票券）
        List<OrderItem> orderItems = new ArrayList<>();
        for (int i = 0; i < request.getVisitors().size(); i++) {
            OrderRequest.VisitorInfo visitor = request.getVisitors().get(i);
            OrderItem item = new OrderItem();
            item.setOrderId(savedOrder.getId());
            item.setTicketPriceId(visitor.getTicketPriceId());
            item.setVisitorName(visitor.getVisitorName());
            item.setVisitorIdCard(visitor.getVisitorIdCard());
            item.setVisitorPhone(visitor.getVisitorPhone());
            item.setPrice(visitor.getPrice());
            // 核销码在 @PrePersist 中自动生成
            orderItems.add(item);
        }

        List<OrderItem> savedItems = orderItemRepository.saveAll(orderItems);

        // 4. 返回订单信息
        return convertToResponse(savedOrder, savedItems);
    }

    /**
     * 支付订单
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse payOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!Order.OrderStatus.PENDING_PAYMENT.equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }

        // 更新订单状态
        order.setStatus(Order.OrderStatus.PENDING_USE);
        order.setPayTime(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);

        // 释放锁定库存（转为已售）
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : items) {
            TicketPrice ticketPrice = ticketPriceRepository.findById(item.getTicketPriceId())
                    .orElseThrow(() -> new RuntimeException("票价不存在"));
            ticketPrice.setInventoryLocked(ticketPrice.getInventoryLocked() - 1);
            ticketPriceRepository.save(ticketPrice);
        }

        return convertToResponse(savedOrder, items);
    }

    /**
     * 取消订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!Order.OrderStatus.PENDING_PAYMENT.equals(order.getStatus())) {
            throw new RuntimeException("只有待支付订单才能取消");
        }

        // 更新订单状态
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCancelTime(LocalDateTime.now());
        orderRepository.save(order);

        // 释放锁定库存
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : items) {
            TicketPrice ticketPrice = ticketPriceRepository.findById(item.getTicketPriceId())
                    .orElseThrow(() -> new RuntimeException("票价不存在"));
            ticketPrice.setInventoryAvailable(ticketPrice.getInventoryAvailable() + 1);
            ticketPrice.setInventoryLocked(ticketPrice.getInventoryLocked() - 1);
            ticketPriceRepository.save(ticketPrice);
        }
    }

    /**
     * 查询订单详情
     */
    public OrderResponse getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return convertToResponse(order, items);
    }

    /**
     * 根据订单号查询订单
     */
    public OrderResponse getOrderByOrderNo(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        return convertToResponse(order, items);
    }

    /**
     * 查询用户订单列表
     */
    public List<OrderResponse> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreateTimeDesc(userId);
        return orders.stream()
                .map(order -> {
                    List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
                    return convertToResponse(order, items);
                })
                .collect(Collectors.toList());
    }

    /**
     * 查询所有订单列表（管理后台）
     */
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(order -> {
                    List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
                    return convertToResponse(order, items);
                })
                .collect(Collectors.toList());
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.valueOf((int) (Math.random() * 1000000));
        return "ORD" + timestamp + String.format("%06d", Integer.parseInt(random));
    }

    /**
     * 转换为响应对象
     */
    private OrderResponse convertToResponse(Order order, List<OrderItem> items) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNo(order.getOrderNo());
        response.setUserId(order.getUserId());
        response.setScenicSpotId(order.getScenicSpotId());
        response.setTicketId(order.getTicketId());
        response.setVisitDate(order.getVisitDate());
        response.setVisitorCount(order.getVisitorCount());
        response.setTotalAmount(order.getTotalAmount());
        response.setDiscountAmount(order.getDiscountAmount());
        response.setActualAmount(order.getActualAmount());
        response.setStatus(order.getStatus());
        response.setContactName(order.getContactName());
        response.setContactPhone(order.getContactPhone());
        response.setRemark(order.getRemark());
        response.setCreateTime(order.getCreateTime());
        response.setUpdateTime(order.getUpdateTime());
        response.setPayTime(order.getPayTime());
        response.setCancelTime(order.getCancelTime());
        response.setRefundTime(order.getRefundTime());

        // 转换订单项
        List<OrderResponse.OrderItemResponse> itemResponses = items.stream()
                .map(item -> {
                    OrderResponse.OrderItemResponse itemResponse = new OrderResponse.OrderItemResponse();
                    itemResponse.setId(item.getId());
                    itemResponse.setOrderId(item.getOrderId());
                    itemResponse.setTicketPriceId(item.getTicketPriceId());
                    itemResponse.setVisitorName(item.getVisitorName());
                    itemResponse.setVisitorIdCard(item.getVisitorIdCard());
                    itemResponse.setVisitorPhone(item.getVisitorPhone());
                    itemResponse.setPrice(item.getPrice());
                    itemResponse.setVerificationCode(item.getVerificationCode());
                    itemResponse.setQrCodeUrl(item.getQrCodeUrl());
                    itemResponse.setIsVerified(item.getIsVerified());
                    itemResponse.setVerifyTime(item.getVerifyTime());
                    itemResponse.setVerifyStaffId(item.getVerifyStaffId());
                    itemResponse.setCreateTime(item.getCreateTime());
                    return itemResponse;
                })
                .collect(Collectors.toList());

        response.setItems(itemResponses);
        return response;
    }
}
