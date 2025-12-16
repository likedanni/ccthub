package com.ccthub.userservice.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.dto.payment.RefundRequest;
import com.ccthub.userservice.dto.payment.RefundResponse;
import com.ccthub.userservice.dto.ticket.TicketOrderCreateRequest;
import com.ccthub.userservice.dto.ticket.TicketOrderResponse;
import com.ccthub.userservice.entity.Order;
import com.ccthub.userservice.entity.OrderRefund;
import com.ccthub.userservice.repository.OrderRepository;
import com.ccthub.userservice.service.RefundService;
import com.ccthub.userservice.service.TicketOrderService;

/**
 * 订单状态流转测试
 * 
 * @author CCTHub
 * @date 2025-12-16
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderStatusFlowTest {

    @Autowired
    private TicketOrderService ticketOrderService;

    @Autowired
    private RefundService refundService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("P1-FLOW-01: 正常流程（待付款→待使用→已完成）")
    void testNormalOrderFlow() {
        // 1. 创建订单（待付款）
        TicketOrderCreateRequest createRequest = buildOrderRequest();
        TicketOrderResponse orderResponse = ticketOrderService.createOrder(createRequest);
        String orderNo = orderResponse.getOrderNo();

        Order order = orderRepository.findByOrderNo(orderNo).get();
        assertEquals(Order.OrderStatus.PENDING_PAYMENT, order.getOrderStatus());
        assertEquals(Order.PaymentStatus.PENDING, order.getPaymentStatus());

        // 2. 支付成功（待使用）
        order.setPaymentStatus(Order.PaymentStatus.SUCCESS);
        order.setOrderStatus(Order.OrderStatus.PENDING_USE);
        orderRepository.save(order);

        order = orderRepository.findByOrderNo(orderNo).get();
        assertEquals(Order.OrderStatus.PENDING_USE, order.getOrderStatus());
        assertEquals(Order.PaymentStatus.SUCCESS, order.getPaymentStatus());

        // 3. 使用完成（已完成）
        order.setOrderStatus(Order.OrderStatus.COMPLETED);
        orderRepository.save(order);

        order = orderRepository.findByOrderNo(orderNo).get();
        assertEquals(Order.OrderStatus.COMPLETED, order.getOrderStatus());
    }

    @Test
    @DisplayName("P1-FLOW-02: 取消流程（待付款→已取消）")
    void testCancellationFlow() {
        // 1. 创建订单（待付款）
        TicketOrderCreateRequest createRequest = buildOrderRequest();
        TicketOrderResponse orderResponse = ticketOrderService.createOrder(createRequest);
        String orderNo = orderResponse.getOrderNo();

        // 2. 取消订单
        Order order = orderRepository.findByOrderNo(orderNo).get();
        order.setOrderStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);

        order = orderRepository.findByOrderNo(orderNo).get();
        assertEquals(Order.OrderStatus.CANCELLED, order.getOrderStatus());
        assertEquals(Order.PaymentStatus.PENDING, order.getPaymentStatus());
    }

    @Test
    @DisplayName("P1-FLOW-03: 退款流程（待使用→退款中→已完成）")
    void testRefundFlow() {
        // 1. 创建并支付订单
        TicketOrderCreateRequest createRequest = buildOrderRequest();
        TicketOrderResponse orderResponse = ticketOrderService.createOrder(createRequest);
        String orderNo = orderResponse.getOrderNo();

        Order order = orderRepository.findByOrderNo(orderNo).get();
        order.setPaymentStatus(Order.PaymentStatus.SUCCESS);
        order.setOrderStatus(Order.OrderStatus.PENDING_USE);
        orderRepository.save(order);

        // 验证初始状态
        order = orderRepository.findByOrderNo(orderNo).get();
        assertEquals(Order.OrderStatus.PENDING_USE, order.getOrderStatus());

        // 2. 创建退款申请（退款中）
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setOrderNo(orderNo);
        refundRequest.setUserId(1L);
        refundRequest.setRefundType(OrderRefund.TYPE_FULL);
        refundRequest.setRefundReason("行程取消");

        RefundResponse refundResponse = refundService.createRefund(refundRequest);
        assertNotNull(refundResponse);
        assertEquals(OrderRefund.STATUS_PENDING_AUDIT, refundResponse.getStatus());

        // 验证订单状态变为退款中
        order = orderRepository.findByOrderNo(orderNo).get();
        assertEquals(Order.OrderStatus.REFUNDING, order.getOrderStatus());

        // 3. 退款成功（已完成）
        // 模拟审核通过和退款成功
        refundService.updateRefundStatus(
                refundResponse.getRefundNo(),
                OrderRefund.STATUS_SUCCESS,
                "REFUND_TEST_123");

        // 验证订单最终状态
        order = orderRepository.findByOrderNo(orderNo).get();
        assertEquals(Order.OrderStatus.COMPLETED, order.getOrderStatus());
    }

    @Test
    @DisplayName("P1-FLOW-04: 退款失败流程（退款中→待使用）")
    void testRefundFailureFlow() {
        // 1. 创建并支付订单
        TicketOrderCreateRequest createRequest = buildOrderRequest();
        TicketOrderResponse orderResponse = ticketOrderService.createOrder(createRequest);
        String orderNo = orderResponse.getOrderNo();

        Order order = orderRepository.findByOrderNo(orderNo).get();
        order.setPaymentStatus(Order.PaymentStatus.SUCCESS);
        order.setOrderStatus(Order.OrderStatus.PENDING_USE);
        orderRepository.save(order);

        // 2. 创建退款申请
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setOrderNo(orderNo);
        refundRequest.setUserId(1L);
        refundRequest.setRefundType(OrderRefund.TYPE_FULL);
        refundRequest.setRefundReason("行程取消");

        RefundResponse refundResponse = refundService.createRefund(refundRequest);

        // 3. 退款失败（恢复到待使用）
        refundService.updateRefundStatus(
                refundResponse.getRefundNo(),
                OrderRefund.STATUS_FAILED,
                null);

        // 验证订单状态恢复
        order = orderRepository.findByOrderNo(orderNo).get();
        assertEquals(Order.OrderStatus.PENDING_USE, order.getOrderStatus());
    }

    @Test
    @DisplayName("P1-FLOW-05: 支付状态转换测试")
    void testPaymentStatusTransitions() {
        // 1. 创建订单（待支付）
        TicketOrderCreateRequest createRequest = buildOrderRequest();
        TicketOrderResponse orderResponse = ticketOrderService.createOrder(createRequest);
        String orderNo = orderResponse.getOrderNo();

        Order order = orderRepository.findByOrderNo(orderNo).get();
        assertEquals(Order.PaymentStatus.PENDING, order.getPaymentStatus());

        // 2. 支付成功
        order.setPaymentStatus(Order.PaymentStatus.SUCCESS);
        orderRepository.save(order);

        order = orderRepository.findByOrderNo(orderNo).get();
        assertEquals(Order.PaymentStatus.SUCCESS, order.getPaymentStatus());

        // 3. 退款后（已退款）
        order.setPaymentStatus(Order.PaymentStatus.REFUNDED);
        orderRepository.save(order);

        order = orderRepository.findByOrderNo(orderNo).get();
        assertEquals(Order.PaymentStatus.REFUNDED, order.getPaymentStatus());
    }

    /**
     * 构建测试订单请求
     */
    private TicketOrderCreateRequest buildOrderRequest() {
        TicketOrderCreateRequest request = new TicketOrderCreateRequest();
        request.setUserId(1L);
        request.setScenicSpotId(1L);
        request.setMerchantId(1L);
        request.setVisitDate(LocalDate.now().plusDays(7)); // 7天后，满足退款规则
        request.setContactName("张三");
        request.setContactPhone("13800138000");

        TicketOrderCreateRequest.TicketItem ticket = new TicketOrderCreateRequest.TicketItem();
        ticket.setTicketPriceId(1L);
        ticket.setProductName("成人票");
        ticket.setPrice(new BigDecimal("98.00"));
        ticket.setVisitorName("李四");
        ticket.setVisitorIdCard("110101199001011234");
        request.setTickets(Arrays.asList(ticket));

        return request;
    }
}
