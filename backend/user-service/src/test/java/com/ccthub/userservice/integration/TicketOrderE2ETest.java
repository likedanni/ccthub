package com.ccthub.userservice.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.dto.ticket.TicketOrderCreateRequest;
import com.ccthub.userservice.dto.ticket.TicketOrderResponse;
import com.ccthub.userservice.entity.Order;
import com.ccthub.userservice.entity.OrderItem;
import com.ccthub.userservice.repository.OrderItemRepository;
import com.ccthub.userservice.repository.OrderRepository;
import com.ccthub.userservice.service.TicketOrderService;
import com.ccthub.userservice.service.VerificationService;

/**
 * 门票订单端到端测试
 * 测试流程: 创建订单 → 支付 → 核销 → 完成
 * 
 * @author CCTHub
 * @date 2025-12-16
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TicketOrderE2ETest {

    @Autowired
    private TicketOrderService ticketOrderService;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("P1-E2E-01: 门票订单完整流程测试（创建→支付→核销→完成）")
    void testTicketOrderCompleteFlow() {
        // 1. 创建订单
        TicketOrderCreateRequest createRequest = new TicketOrderCreateRequest();
        createRequest.setUserId(1L);
        createRequest.setScenicSpotId(1L);
        createRequest.setMerchantId(1L);
        createRequest.setVisitDate(LocalDate.now().plusDays(1));
        createRequest.setContactName("张三");
        createRequest.setContactPhone("13800138000");

        TicketOrderCreateRequest.TicketItem ticket1 = new TicketOrderCreateRequest.TicketItem();
        ticket1.setTicketPriceId(1L);
        ticket1.setProductName("成人票");
        ticket1.setPrice(new BigDecimal("98.00"));
        ticket1.setVisitorName("李四");
        ticket1.setVisitorIdCard("110101199001011234");

        TicketOrderCreateRequest.TicketItem ticket2 = new TicketOrderCreateRequest.TicketItem();
        ticket2.setTicketPriceId(1L);
        ticket2.setProductName("成人票");
        ticket2.setPrice(new BigDecimal("98.00"));
        ticket2.setVisitorName("王五");
        ticket2.setVisitorIdCard("110101199002021234");

        createRequest.setTickets(Arrays.asList(ticket1, ticket2));

        TicketOrderResponse orderResponse = ticketOrderService.createOrder(createRequest);

        assertNotNull(orderResponse);
        assertNotNull(orderResponse.getOrderNo());
        assertEquals(Order.OrderStatus.PENDING_PAYMENT, orderResponse.getOrderStatus());
        assertEquals(Order.PaymentStatus.PENDING, orderResponse.getPaymentStatus());
        assertEquals(new BigDecimal("196.00"), orderResponse.getTotalAmount());
        assertEquals(2, orderResponse.getTickets().size());

        String orderNo = orderResponse.getOrderNo();

        // 2. 模拟支付成功（更新订单状态）
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new AssertionError("订单不存在"));

        order.setPaymentStatus(Order.PaymentStatus.SUCCESS);
        order.setOrderStatus(Order.OrderStatus.PENDING_USE);
        orderRepository.save(order);

        // 验证订单状态
        Order paidOrder = orderRepository.findByOrderNo(orderNo).get();
        assertEquals(Order.PaymentStatus.SUCCESS, paidOrder.getPaymentStatus());
        assertEquals(Order.OrderStatus.PENDING_USE, paidOrder.getOrderStatus());

        // 3. 获取核销码并核销第一张票
        List<OrderItem> items = orderItemRepository.findByOrderNo(orderNo);
        assertEquals(2, items.size());

        String verificationCode1 = items.get(0).getVerificationCode();
        assertNotNull(verificationCode1);

        // 查询核销码信息
        Map<String, Object> verificationInfo = verificationService.getVerificationInfo(verificationCode1);
        assertNotNull(verificationInfo);
        assertEquals(OrderItem.VerificationStatus.NOT_VERIFIED, verificationInfo.get("verificationStatus"));

        // 执行核销
        verificationService.verifyTicket(verificationCode1, 1001L);

        // 验证核销结果
        OrderItem verifiedItem = orderItemRepository.findByVerificationCode(verificationCode1).get();
        assertEquals(OrderItem.VerificationStatus.VERIFIED, verifiedItem.getVerificationStatus());

        // 4. 核销第二张票
        String verificationCode2 = items.get(1).getVerificationCode();
        verificationService.verifyTicket(verificationCode2, 1001L);

        // 5. 查询订单核销统计
        Map<String, Object> stats = verificationService.getOrderVerificationStats(orderNo);
        assertEquals(2, stats.get("totalCount"));
        assertEquals(2, stats.get("verifiedCount"));
        assertEquals(0, stats.get("notVerifiedCount"));
        assertEquals(100, stats.get("verificationRate"));

        // 6. 验证订单最终状态（所有票已核销）
        List<OrderItem> allItems = orderItemRepository.findByOrderNo(orderNo);
        assertTrue(allItems.stream()
                .allMatch(item -> item.getVerificationStatus() == OrderItem.VerificationStatus.VERIFIED));
    }

    @Test
    @DisplayName("P1-E2E-02: 批量核销测试")
    void testBatchVerification() {
        // 1. 创建订单（3张票）
        TicketOrderCreateRequest createRequest = new TicketOrderCreateRequest();
        createRequest.setUserId(1L);
        createRequest.setScenicSpotId(1L);
        createRequest.setMerchantId(1L);
        createRequest.setVisitDate(LocalDate.now().plusDays(1));
        createRequest.setContactName("张三");
        createRequest.setContactPhone("13800138000");

        java.util.List<TicketOrderCreateRequest.TicketItem> tickets = new java.util.ArrayList<>();
        for (int i = 0; i < 3; i++) {
            TicketOrderCreateRequest.TicketItem ticket = new TicketOrderCreateRequest.TicketItem();
            ticket.setTicketPriceId(1L);
            ticket.setProductName("成人票");
            ticket.setPrice(new BigDecimal("98.00"));
            ticket.setVisitorName("游客" + (i + 1));
            ticket.setVisitorIdCard("11010119900101" + String.format("%04d", i + 1234));
            tickets.add(ticket);
        }
        createRequest.setTickets(tickets);

        TicketOrderResponse orderResponse = ticketOrderService.createOrder(createRequest);
        String orderNo = orderResponse.getOrderNo();

        // 2. 更新订单为已支付
        Order order = orderRepository.findByOrderNo(orderNo).get();
        order.setPaymentStatus(Order.PaymentStatus.SUCCESS);
        order.setOrderStatus(Order.OrderStatus.PENDING_USE);
        orderRepository.save(order);

        // 3. 获取所有核销码
        List<OrderItem> items = orderItemRepository.findByOrderNo(orderNo);
        List<String> verificationCodes = items.stream()
                .map(OrderItem::getVerificationCode)
                .toList();

        assertEquals(3, verificationCodes.size());

        // 4. 批量核销
        Map<String, Object> batchResult = verificationService.batchVerify(verificationCodes, 1001L);

        assertEquals(3, batchResult.get("totalCount"));
        assertEquals(3, batchResult.get("successCount"));
        assertEquals(0, batchResult.get("failedCount"));

        // 5. 验证所有票已核销
        List<OrderItem> verifiedItems = orderItemRepository.findByOrderNo(orderNo);
        assertTrue(verifiedItems.stream()
                .allMatch(item -> item.getVerificationStatus() == OrderItem.VerificationStatus.VERIFIED));
    }

    @Test
    @DisplayName("P1-E2E-03: 订单状态流转测试（待付款→取消）")
    void testOrderCancellation() {
        // 1. 创建订单
        TicketOrderCreateRequest createRequest = new TicketOrderCreateRequest();
        createRequest.setUserId(1L);
        createRequest.setScenicSpotId(1L);
        createRequest.setMerchantId(1L);
        createRequest.setVisitDate(LocalDate.now().plusDays(1));
        createRequest.setContactName("张三");
        createRequest.setContactPhone("13800138000");

        TicketOrderCreateRequest.TicketItem ticket = new TicketOrderCreateRequest.TicketItem();
        ticket.setTicketPriceId(1L);
        ticket.setProductName("成人票");
        ticket.setPrice(new BigDecimal("98.00"));
        ticket.setVisitorName("李四");
        ticket.setVisitorIdCard("110101199001011234");
        createRequest.setTickets(Arrays.asList(ticket));

        TicketOrderResponse orderResponse = ticketOrderService.createOrder(createRequest);
        String orderNo = orderResponse.getOrderNo();

        // 2. 验证初始状态
        Order order = orderRepository.findByOrderNo(orderNo).get();
        assertEquals(Order.OrderStatus.PENDING_PAYMENT, order.getOrderStatus());
        assertEquals(Order.PaymentStatus.PENDING, order.getPaymentStatus());

        // 3. 取消订单
        order.setOrderStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);

        // 4. 验证取消状态
        Order cancelledOrder = orderRepository.findByOrderNo(orderNo).get();
        assertEquals(Order.OrderStatus.CANCELLED, cancelledOrder.getOrderStatus());
    }

    @Test
    @DisplayName("P1-E2E-04: 核销防重复测试")
    void testVerificationDuplicatePrevention() {
        // 1. 创建并支付订单
        TicketOrderCreateRequest createRequest = new TicketOrderCreateRequest();
        createRequest.setUserId(1L);
        createRequest.setScenicSpotId(1L);
        createRequest.setMerchantId(1L);
        createRequest.setVisitDate(LocalDate.now().plusDays(1));
        createRequest.setContactName("张三");
        createRequest.setContactPhone("13800138000");

        TicketOrderCreateRequest.TicketItem ticket = new TicketOrderCreateRequest.TicketItem();
        ticket.setTicketPriceId(1L);
        ticket.setProductName("成人票");
        ticket.setPrice(new BigDecimal("98.00"));
        ticket.setVisitorName("李四");
        ticket.setVisitorIdCard("110101199001011234");
        createRequest.setTickets(Arrays.asList(ticket));

        TicketOrderResponse orderResponse = ticketOrderService.createOrder(createRequest);
        String orderNo = orderResponse.getOrderNo();

        Order order = orderRepository.findByOrderNo(orderNo).get();
        order.setPaymentStatus(Order.PaymentStatus.SUCCESS);
        order.setOrderStatus(Order.OrderStatus.PENDING_USE);
        orderRepository.save(order);

        // 2. 获取核销码并核销
        OrderItem item = orderItemRepository.findByOrderNo(orderNo).get(0);
        String verificationCode = item.getVerificationCode();

        verificationService.verifyTicket(verificationCode, 1001L);

        // 3. 尝试重复核销（应该抛出异常）
        assertThrows(IllegalStateException.class, () -> {
            verificationService.verifyTicket(verificationCode, 1001L);
        });

        // 4. 验证核销状态未改变
        OrderItem verifiedItem = orderItemRepository.findByVerificationCode(verificationCode).get();
        assertEquals(OrderItem.VerificationStatus.VERIFIED, verifiedItem.getVerificationStatus());
    }
}
