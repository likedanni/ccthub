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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ccthub.userservice.entity.OrderItem;
import com.ccthub.userservice.repository.OrderItemRepository;

/**
 * 核销服务测试
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@ExtendWith(MockitoExtension.class)
class VerificationServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private VerificationService verificationService;

    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setOrderId(1L);
        testOrderItem.setTicketPriceId(1L);
        testOrderItem.setVisitorName("张三");
        testOrderItem.setVisitorIdCard("110101199001011234");
        testOrderItem.setPrice(new BigDecimal("98.00"));
        testOrderItem.setVerificationCode("test-code-123456");
        testOrderItem.setIsVerified(false);
    }

    @Test
    void testGetVerificationInfo() {
        // 模拟查询
        when(orderItemRepository.findByVerificationCode("test-code-123456"))
                .thenReturn(Optional.of(testOrderItem));

        // 执行查询
        Map<String, Object> info = verificationService.getVerificationInfo("test-code-123456");

        // 验证结果
        assertNotNull(info);
        assertEquals(1L, info.get("id"));
        assertEquals("张三", info.get("visitorName"));
        assertEquals(false, info.get("isVerified"));

        // 验证调用
        verify(orderItemRepository, times(1)).findByVerificationCode("test-code-123456");
    }

    @Test
    void testVerifyTicket_Success() {
        // 模拟查询
        when(orderItemRepository.findByVerificationCode("test-code-123456"))
                .thenReturn(Optional.of(testOrderItem));

        // 模拟保存
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        // 执行核销
        Map<String, Object> result = verificationService.verifyTicket("test-code-123456", 1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(true, result.get("success"));
        assertEquals("核销成功", result.get("message"));
        assertEquals("张三", result.get("visitorName"));

        // 验证订单项状态
        assertTrue(testOrderItem.getIsVerified());
        assertNotNull(testOrderItem.getVerifyTime());
        assertEquals(1L, testOrderItem.getVerifyStaffId());

        // 验证调用
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    void testVerifyTicket_AlreadyVerified() {
        // 设置已核销
        testOrderItem.setIsVerified(true);
        testOrderItem.setVerifyTime(LocalDateTime.now());

        // 模拟查询
        when(orderItemRepository.findByVerificationCode("test-code-123456"))
                .thenReturn(Optional.of(testOrderItem));

        // 执行并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            verificationService.verifyTicket("test-code-123456", 1L);
        });

        assertTrue(exception.getMessage().contains("已核销"));

        // 验证未保存
        verify(orderItemRepository, never()).save(any(OrderItem.class));
    }

    @Test
    void testBatchVerify_Success() {
        // 准备多个订单项
        List<OrderItem> items = new ArrayList<>();
        items.add(testOrderItem);

        OrderItem item2 = new OrderItem();
        item2.setId(2L);
        item2.setOrderId(1L);
        item2.setIsVerified(false);
        items.add(item2);

        // 模拟查询
        when(orderItemRepository.findByOrderIdAndIsVerified(1L, false)).thenReturn(items);

        // 模拟保存
        when(orderItemRepository.saveAll(any())).thenReturn(items);

        // 执行批量核销
        Map<String, Object> result = verificationService.batchVerify(1L, 1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(true, result.get("success"));
        assertEquals(2, result.get("count"));

        // 验证所有项已核销
        for (OrderItem item : items) {
            assertTrue(item.getIsVerified());
            assertNotNull(item.getVerifyTime());
            assertEquals(1L, item.getVerifyStaffId());
        }

        // 验证调用
        verify(orderItemRepository, times(1)).saveAll(any());
    }

    @Test
    void testBatchVerify_NoItemsToVerify() {
        // 模拟空列表
        when(orderItemRepository.findByOrderIdAndIsVerified(1L, false)).thenReturn(new ArrayList<>());

        // 执行并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            verificationService.batchVerify(1L, 1L);
        });

        assertTrue(exception.getMessage().contains("已核销"));

        // 验证未保存
        verify(orderItemRepository, never()).saveAll(any());
    }
}
