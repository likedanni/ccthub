package com.ccthub.userservice.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.dto.ticket.TicketOrderCreateRequest;
import com.ccthub.userservice.dto.ticket.TicketOrderResponse;
import com.ccthub.userservice.entity.Order;
import com.ccthub.userservice.entity.OrderItem;
import com.ccthub.userservice.repository.OrderItemRepository;
import com.ccthub.userservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

/**
 * 门票订单服务
 * 
 * @author CCTHub
 * @date 2025-12-16
 */
@Service
@RequiredArgsConstructor
public class TicketOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * 创建门票订单
     */
    @Transactional(rollbackFor = Exception.class)
    public TicketOrderResponse createTicketOrder(TicketOrderCreateRequest request) {
        // 1. 创建订单主表
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setMerchantId(request.getMerchantId());
        order.setOrderType(Order.OrderType.TICKET);

        // 计算金额
        BigDecimal totalAmount = request.getTickets().stream()
                .map(TicketOrderCreateRequest.TicketItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setPayAmount(totalAmount);
        order.setPaymentStatus(Order.PaymentStatus.PENDING);
        order.setOrderStatus(Order.OrderStatus.PENDING_PAYMENT);
        order.setRefundStatus(Order.RefundStatus.NO_REFUND);

        Order savedOrder = orderRepository.save(order);

        // 2. 创建订单明细（门票）
        List<OrderItem> orderItems = new ArrayList<>();
        for (TicketOrderCreateRequest.TicketItem ticket : request.getTickets()) {
            OrderItem item = new OrderItem();
            item.setOrderNo(savedOrder.getOrderNo());
            item.setProductId(ticket.getTicketPriceId()); // 暂时用ticketPriceId作为productId
            item.setProductName(ticket.getProductName());
            item.setUnitPrice(ticket.getPrice());
            item.setQuantity(1);
            item.setSubtotal(ticket.getPrice());
            item.setTicketDate(request.getVisitDate());
            item.setVisitorName(ticket.getVisitorName());
            item.setVerificationStatus(OrderItem.VerificationStatus.NOT_VERIFIED);
            orderItems.add(item);
        }

        List<OrderItem> savedItems = orderItemRepository.saveAll(orderItems);

        // 3. 返回订单响应
        return convertToResponse(savedOrder, savedItems, request);
    }

    /**
     * 查询门票订单详情
     */
    public TicketOrderResponse getTicketOrderByOrderNo(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!Order.OrderType.TICKET.equals(order.getOrderType())) {
            throw new RuntimeException("订单类型错误，不是门票订单");
        }

        List<OrderItem> items = orderItemRepository.findByOrderNo(orderNo);
        return convertToResponse(order, items, null);
    }

    /**
     * 根据订单ID查询订单详情
     */
    public TicketOrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        List<OrderItem> items = orderItemRepository.findByOrderNo(order.getOrderNo());
        return convertToResponse(order, items, null);
    }

    /**
     * 查询用户门票订单列表
     */
    public List<TicketOrderResponse> getUserTicketOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserIdAndOrderTypeOrderByCreateTimeDesc(
                userId, Order.OrderType.TICKET);

        return orders.stream()
                .map(order -> {
                    List<OrderItem> items = orderItemRepository.findByOrderNo(order.getOrderNo());
                    return convertToResponse(order, items, null);
                })
                .collect(Collectors.toList());
    }

    /**
     * 支付门票订单
     */
    @Transactional(rollbackFor = Exception.class)
    public TicketOrderResponse payTicketOrder(String orderNo, String paymentMethod) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!Order.OrderStatus.PENDING_PAYMENT.equals(order.getOrderStatus())) {
            throw new RuntimeException("订单状态不正确，无法支付");
        }

        // 更新订单状态
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus(Order.PaymentStatus.SUCCESS);
        order.setOrderStatus(Order.OrderStatus.PENDING_USE);
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> items = orderItemRepository.findByOrderNo(orderNo);
        return convertToResponse(savedOrder, items, null);
    }

    /**
     * 取消门票订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelTicketOrder(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!Order.OrderStatus.PENDING_PAYMENT.equals(order.getOrderStatus())) {
            throw new RuntimeException("只有待支付订单才能取消");
        }

        order.setOrderStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    /**
     * 转换为门票订单响应对象
     */
    private TicketOrderResponse convertToResponse(Order order, List<OrderItem> items,
            TicketOrderCreateRequest request) {
        TicketOrderResponse response = new TicketOrderResponse();
        response.setId(order.getId());
        response.setOrderNo(order.getOrderNo());
        response.setUserId(order.getUserId());
        response.setMerchantId(order.getMerchantId());

        // 从Order实体读取基本信息
        response.setScenicSpotId(order.getScenicSpotId());
        response.setVisitDate(order.getVisitDate());
        response.setVisitorCount(order.getVisitorCount());
        response.setContactName(order.getContactName());
        response.setContactPhone(order.getContactPhone());
        response.setRemark(order.getRemark());
        response.setStatus(order.getStatus());

        // 如果有request参数，覆盖上述字段（用于创建订单时）
        if (request != null) {
            if (request.getScenicSpotId() != null) {
                response.setScenicSpotId(request.getScenicSpotId());
            }
            if (request.getVisitDate() != null) {
                response.setVisitDate(request.getVisitDate());
            }
            if (request.getContactName() != null) {
                response.setContactName(request.getContactName());
            }
            if (request.getContactPhone() != null) {
                response.setContactPhone(request.getContactPhone());
            }
            if (request.getRemark() != null) {
                response.setRemark(request.getRemark());
            }
        }

        response.setTotalAmount(order.getTotalAmount());
        response.setDiscountAmount(order.getDiscountAmount());
        response.setPayAmount(order.getPayAmount());
        response.setActualAmount(order.getActualAmount());
        response.setPointAmount(order.getPointAmount());
        response.setPointEarned(order.getPointEarned());

        response.setPaymentMethod(order.getPaymentMethod());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setPaymentStatusText(getPaymentStatusText(order.getPaymentStatus()));
        response.setOrderStatus(order.getOrderStatus());
        response.setOrderStatusText(getOrderStatusText(order.getOrderStatus()));

        response.setCreateTime(order.getCreateTime());
        response.setUpdateTime(order.getUpdateTime());

        // 转换门票明细
        List<TicketOrderResponse.TicketItemResponse> ticketResponses = items.stream()
                .map(item -> {
                    TicketOrderResponse.TicketItemResponse ticketResponse = new TicketOrderResponse.TicketItemResponse();
                    ticketResponse.setId(item.getId());
                    ticketResponse.setProductName(item.getProductName());
                    ticketResponse.setUnitPrice(item.getUnitPrice());
                    ticketResponse.setVisitorName(item.getVisitorName());
                    ticketResponse.setVerificationCode(item.getVerificationCode());
                    ticketResponse.setVerificationStatus(item.getVerificationStatus());
                    ticketResponse.setVerificationStatusText(getVerificationStatusText(item.getVerificationStatus()));
                    ticketResponse.setTicketDate(item.getTicketDate());
                    return ticketResponse;
                })
                .collect(Collectors.toList());

        response.setTickets(ticketResponses);
        return response;
    }

    /**
     * 获取支付状态文本
     */
    private String getPaymentStatusText(Integer status) {
        if (status == null)
            return "未知";
        switch (status) {
            case 0:
                return "待支付";
            case 1:
                return "支付成功";
            case 2:
                return "支付失败";
            case 3:
                return "已退款";
            case 4:
                return "处理中";
            default:
                return "未知";
        }
    }

    /**
     * 获取订单状态文本
     */
    private String getOrderStatusText(Integer status) {
        if (status == null)
            return "未知";
        switch (status) {
            case 0:
                return "待付款";
            case 1:
                return "待使用";
            case 2:
                return "已完成";
            case 3:
                return "已取消";
            case 4:
                return "退款中";
            default:
                return "未知";
        }
    }

    /**
     * 获取核销状态文本
     */
    private String getVerificationStatusText(Integer status) {
        if (status == null)
            return "未知";
        switch (status) {
            case 0:
                return "未核销";
            case 1:
                return "已核销";
            case 2:
                return "已过期";
            default:
                return "未知";
        }
    }

    // ===== 统一订单查询API支持方法 =====

    /**
     * 分页查询订单（支持多条件筛选）
     */
    public org.springframework.data.domain.Page<TicketOrderResponse> queryOrders(
            Long userId,
            Integer orderStatus,
            Integer paymentStatus,
            java.time.LocalDate startDate,
            java.time.LocalDate endDate,
            org.springframework.data.domain.Pageable pageable) {

        // 查询订单：如果userId为空，查询所有门票订单
        List<Order> orders;
        if (userId != null) {
            orders = orderRepository.findByUserIdAndOrderType(userId, Order.OrderType.TICKET);
        } else {
            orders = orderRepository.findByOrderType(Order.OrderType.TICKET);
        }

        // 按条件过滤
        if (orderStatus != null) {
            orders = orders.stream()
                    .filter(o -> o.getOrderStatus().equals(orderStatus))
                    .collect(Collectors.toList());
        }
        if (paymentStatus != null) {
            orders = orders.stream()
                    .filter(o -> o.getPaymentStatus().equals(paymentStatus))
                    .collect(Collectors.toList());
        }

        // 转换为Response（需要查询每个订单的OrderItem）
        List<TicketOrderResponse> responses = orders.stream()
                .map(order -> {
                    List<OrderItem> items = orderItemRepository.findByOrderNo(order.getOrderNo());
                    return convertToResponse(order, items, null);
                })
                .collect(Collectors.toList());

        // 分页处理
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        List<TicketOrderResponse> pageContent = responses.subList(start, end);

        return new org.springframework.data.domain.PageImpl<>(
                pageContent, pageable, responses.size());
    }

    /**
     * 根据订单号获取订单（别名方法，供UnifiedOrderController使用）
     */
    public TicketOrderResponse getOrderByOrderNo(String orderNo) {
        return getTicketOrderByOrderNo(orderNo);
    }

    /**
     * 统计用户订单总数
     */
    public long countByUserId(Long userId) {
        return orderRepository.countByUserIdAndOrderType(userId, Order.OrderType.TICKET);
    }

    /**
     * 统计用户指定状态的订单数
     */
    public long countByUserIdAndStatus(Long userId, Integer orderStatus) {
        return orderRepository.findByUserIdAndOrderType(userId, Order.OrderType.TICKET)
                .stream()
                .filter(o -> o.getOrderStatus().equals(orderStatus))
                .count();
    }

    /**
     * 取消订单（带原因）
     */
    public void cancelOrder(String orderNo, String reason) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        if (order.getOrderStatus() != Order.OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("只能取消待付款订单");
        }

        order.setOrderStatus(Order.OrderStatus.CANCELLED);
        // TODO: 保存取消原因（需要在Order实体中添加cancelReason字段）
        orderRepository.save(order);
    }

    /**
     * 创建订单（别名方法，供UnifiedOrderController使用）
     */
    public TicketOrderResponse createOrder(TicketOrderCreateRequest request) {
        return createTicketOrder(request);
    }
}
