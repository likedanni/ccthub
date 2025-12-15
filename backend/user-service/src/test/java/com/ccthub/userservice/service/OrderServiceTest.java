package com.ccthub.userservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ccthub.userservice.dto.OrderRequest;
import com.ccthub.userservice.dto.OrderResponse;
import com.ccthub.userservice.entity.Order;
import com.ccthub.userservice.entity.OrderItem;
import com.ccthub.userservice.entity.TicketPrice;
import com.ccthub.userservice.repository.OrderItemRepository;
import com.ccthub.userservice.repository.OrderRepository;
import com.ccthub.userservice.repository.TicketPriceRepository;

/**
 * 订单服务测试
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private TicketPriceRepository ticketPriceRepository;

    @InjectMocks
    private OrderService orderService;

    private OrderRequest testOrderRequest;
    private TicketPrice testTicketPrice;
    private Order testOrder;
    private List<OrderItem> testOrderItems;

    @BeforeEach
    void setUp() {
        // 准备测试数据 - 票价
        testTicketPrice = new TicketPrice();
        testTicketPrice.setId(1L);
        testTicketPrice.setTicketId(1L);
        testTicketPrice.setPriceDate(LocalDate.now().plusDays(1));
        testTicketPrice.setPriceType(1);
        testTicketPrice.setOriginalPrice(new BigDecimal("98.00"));
        testTicketPrice.setSellPrice(new BigDecimal("98.00"));
        testTicketPrice.setInventoryTotal(1000);
        testTicketPrice.setInventoryAvailable(1000);
        testTicketPrice.setInventoryLocked(0);

        // 准备测试数据 - 订单请求
        testOrderRequest = new OrderRequest();
        testOrderRequest.setUserId(1L);
        testOrderRequest.setScenicSpotId(1L);
        testOrderRequest.setTicketId(1L);
        testOrderRequest.setVisitDate(LocalDate.now().plusDays(1));
        testOrderRequest.setContactName("张三");
        testOrderRequest.setContactPhone("13800138000");

        OrderRequest.VisitorInfo visitor = new OrderRequest.VisitorInfo();
        visitor.setTicketPriceId(1L);
        visitor.setVisitorName("李四");
        visitor.setVisitorIdCard("110101199001011234");
        visitor.setPrice(new BigDecimal("98.00"));

        List<OrderRequest.VisitorInfo> visitors = new ArrayList<>();
        visitors.add(visitor);
        testOrderRequest.setVisitors(visitors);

        // 准备测试数据 - 订单
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNo("ORD20251215001");
        testOrder.setUserId(1L);
        testOrder.setScenicSpotId(1L);
        testOrder.setTicketId(1L);
        testOrder.setVisitDate(LocalDate.now().plusDays(1));
        testOrder.setVisitorCount(1);
        testOrder.setTotalAmount(new BigDecimal("98.00"));
        testOrder.setActualAmount(new BigDecimal("98.00"));
        testOrder.setStatus(Order.OrderStatus.PENDING_PAYMENT);
        testOrder.setContactName("张三");
        testOrder.setContactPhone("13800138000");

        // 准备测试数据 - 订单项
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrderId(1L);
        orderItem.setTicketPriceId(1L);
        orderItem.setVisitorName("李四");
        orderItem.setVisitorIdCard("110101199001011234");
        orderItem.setPrice(new BigDecimal("98.00"));
        orderItem.setVerificationCode("test-verification-code");
        orderItem.setIsVerified(false);

        testOrderItems = new ArrayList<>();
        testOrderItems.add(orderItem);
    }

    @Test
    void testCreateOrder_Success() {
        // 模拟票价查询
        when(ticketPriceRepository.findById(1L)).thenReturn(Optional.of(testTicketPrice));

        // 模拟保存订单
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // 模拟保存订单项
        when(orderItemRepository.saveAll(any())).thenReturn(testOrderItems);

        // 执行创建订单
        OrderResponse response = orderService.createOrder(testOrderRequest);

        // 验证结果
        assertNotNull(response);
        assertEquals("张三", response.getContactName());
        assertEquals(1, response.getVisitorCount());
        assertEquals(new BigDecimal("98.00"), response.getActualAmount());
        assertEquals(Order.OrderStatus.PENDING_PAYMENT, response.getStatus());

        // 验证调用次数
        verify(ticketPriceRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).saveAll(any());

        // 验证库存扣减
        assertEquals(999, testTicketPrice.getInventoryAvailable());
        assertEquals(1, testTicketPrice.getInventoryLocked());
    }

    @Test
    void testCreateOrder_InsufficientInventory() {
        // 模拟库存不足
        testTicketPrice.setInventoryAvailable(0);
        when(ticketPriceRepository.findById(1L)).thenReturn(Optional.of(testTicketPrice));

        // 执行并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(testOrderRequest);
        });

        assertTrue(exception.getMessage().contains("库存不足"));

        // 验证未创建订单
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testPayOrder_Success() {
        // 模拟查询订单
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // 模拟更新订单（注意：save时会修改对象状态）
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setStatus(Order.OrderStatus.PENDING_USE);
            return order;
        });

        // 模拟查询订单项
        when(orderItemRepository.findByOrderId(1L)).thenReturn(testOrderItems);

        // 模拟查询票价
        when(ticketPriceRepository.findById(1L)).thenReturn(Optional.of(testTicketPrice));

        // 执行支付
        OrderResponse response = orderService.payOrder(1L);

        // 验证结果
        assertNotNull(response);
        assertEquals(Order.OrderStatus.PENDING_USE, response.getStatus());
        assertNotNull(response.getPayTime());

        // 验证调用
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCancelOrder_Success() {
        // 模拟查询订单
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // 模拟保存
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // 模拟查询订单项
        when(orderItemRepository.findByOrderId(1L)).thenReturn(testOrderItems);

        // 模拟查询票价
        testTicketPrice.setInventoryAvailable(999);
        testTicketPrice.setInventoryLocked(1);
        when(ticketPriceRepository.findById(1L)).thenReturn(Optional.of(testTicketPrice));

        // 执行取消
        orderService.cancelOrder(1L);

        // 验证库存释放
        assertEquals(1000, testTicketPrice.getInventoryAvailable());
        assertEquals(0, testTicketPrice.getInventoryLocked());

        // 验证调用
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testGetOrderDetail() {
        // 模拟查询
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(testOrderItems);

        // 执行查询
        OrderResponse response = orderService.getOrderDetail(1L);

        // 验证结果
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1, response.getItems().size());

        // 验证调用
        verify(orderRepository, times(1)).findById(1L);
        verify(orderItemRepository, times(1)).findByOrderId(1L);
    }

    @Test
    void testGetUserOrders() {
        // 模拟查询用户订单
        List<Order> orders = new ArrayList<>();
        orders.add(testOrder);
        when(orderRepository.findByUserIdOrderByCreateTimeDesc(1L)).thenReturn(orders);
        when(orderItemRepository.findByOrderId(1L)).thenReturn(testOrderItems);

        // 执行查询
        List<OrderResponse> responses = orderService.getUserOrders(1L);

        // 验证结果
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getUserId());

        // 验证调用
        verify(orderRepository, times(1)).findByUserIdOrderByCreateTimeDesc(1L);
    }
}
