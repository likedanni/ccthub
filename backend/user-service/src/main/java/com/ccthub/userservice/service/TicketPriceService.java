package com.ccthub.userservice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.dto.TicketPriceRequest;
import com.ccthub.userservice.dto.TicketPriceResponse;
import com.ccthub.userservice.entity.TicketPrice;
import com.ccthub.userservice.repository.TicketPriceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 票价服务类
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TicketPriceService {

    private final TicketPriceRepository ticketPriceRepository;

    /**
     * 创建或更新票价
     */
    @Transactional
    public TicketPriceResponse saveTicketPrice(TicketPriceRequest request) {
        // 检查是否已存在
        TicketPrice ticketPrice = ticketPriceRepository
                .findByTicketIdAndPriceDateAndPriceType(
                        request.getTicketId(),
                        request.getPriceDate(),
                        request.getPriceType())
                .orElse(new TicketPrice());

        ticketPrice.setTicketId(request.getTicketId());
        ticketPrice.setPriceDate(request.getPriceDate());
        ticketPrice.setPriceType(request.getPriceType());
        ticketPrice.setOriginalPrice(request.getOriginalPrice());
        ticketPrice.setSellPrice(request.getSellPrice());
        ticketPrice.setInventoryTotal(request.getInventoryTotal());
        ticketPrice.setInventoryAvailable(request.getInventoryTotal());
        ticketPrice.setIsActive(request.getIsActive());

        TicketPrice saved = ticketPriceRepository.save(ticketPrice);
        log.info("Saved ticket price: id={}, ticketId={}, date={}, type={}",
                saved.getId(), saved.getTicketId(), saved.getPriceDate(), saved.getPriceType());

        return convertToResponse(saved);
    }

    /**
     * 批量设置票价（用于日历批量设置）
     */
    @Transactional
    public List<TicketPriceResponse> batchSaveTicketPrices(List<TicketPriceRequest> requests) {
        return requests.stream()
                .map(this::saveTicketPrice)
                .collect(Collectors.toList());
    }

    /**
     * 删除票价
     */
    @Transactional
    public void deleteTicketPrice(Long id) {
        if (!ticketPriceRepository.existsById(id)) {
            throw new RuntimeException("票价不存在: " + id);
        }
        ticketPriceRepository.deleteById(id);
        log.info("Deleted ticket price: id={}", id);
    }

    /**
     * 根据ID查询票价
     */
    public TicketPriceResponse getTicketPriceById(Long id) {
        TicketPrice ticketPrice = ticketPriceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("票价不存在: " + id));
        return convertToResponse(ticketPrice);
    }

    /**
     * 根据票种ID查询所有票价
     */
    public List<TicketPriceResponse> getTicketPricesByTicketId(Long ticketId) {
        return ticketPriceRepository.findByTicketId(ticketId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据票种ID和日期范围查询票价
     */
    public List<TicketPriceResponse> getTicketPricesByDateRange(
            Long ticketId, LocalDate startDate, LocalDate endDate) {
        return ticketPriceRepository.findByTicketIdAndPriceDateBetween(ticketId, startDate, endDate)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 锁定库存（下单时调用）
     */
    @Transactional
    public void lockInventory(Long ticketId, LocalDate priceDate, Integer priceType, Integer quantity) {
        TicketPrice ticketPrice = ticketPriceRepository
                .findByTicketIdAndPriceDateAndPriceTypeWithLock(ticketId, priceDate, priceType)
                .orElseThrow(() -> new RuntimeException("票价不存在"));

        if (!ticketPrice.hasEnoughInventory(quantity)) {
            throw new RuntimeException("库存不足");
        }

        ticketPrice.lockInventory(quantity);
        ticketPriceRepository.save(ticketPrice);
        log.info("Locked inventory: ticketId={}, date={}, type={}, quantity={}",
                ticketId, priceDate, priceType, quantity);
    }

    /**
     * 释放库存（取消订单/超时未支付）
     */
    @Transactional
    public void releaseInventory(Long ticketId, LocalDate priceDate, Integer priceType, Integer quantity) {
        TicketPrice ticketPrice = ticketPriceRepository
                .findByTicketIdAndPriceDateAndPriceType(ticketId, priceDate, priceType)
                .orElseThrow(() -> new RuntimeException("票价不存在"));

        ticketPrice.releaseInventory(quantity);
        ticketPriceRepository.save(ticketPrice);
        log.info("Released inventory: ticketId={}, date={}, type={}, quantity={}",
                ticketId, priceDate, priceType, quantity);
    }

    /**
     * 扣减库存（支付成功后调用）
     */
    @Transactional
    public void deductInventory(Long ticketId, LocalDate priceDate, Integer priceType, Integer quantity) {
        TicketPrice ticketPrice = ticketPriceRepository
                .findByTicketIdAndPriceDateAndPriceType(ticketId, priceDate, priceType)
                .orElseThrow(() -> new RuntimeException("票价不存在"));

        ticketPrice.deductInventory(quantity);
        ticketPriceRepository.save(ticketPrice);
        log.info("Deducted inventory: ticketId={}, date={}, type={}, quantity={}",
                ticketId, priceDate, priceType, quantity);
    }

    /**
     * 转换为响应DTO
     */
    private TicketPriceResponse convertToResponse(TicketPrice ticketPrice) {
        TicketPriceResponse response = new TicketPriceResponse();
        response.setId(ticketPrice.getId());
        response.setTicketId(ticketPrice.getTicketId());
        response.setPriceDate(ticketPrice.getPriceDate());
        response.setPriceType(ticketPrice.getPriceType());
        response.setOriginalPrice(ticketPrice.getOriginalPrice());
        response.setSellPrice(ticketPrice.getSellPrice());
        response.setInventoryTotal(ticketPrice.getInventoryTotal());
        response.setInventoryAvailable(ticketPrice.getInventoryAvailable());
        response.setInventoryLocked(ticketPrice.getInventoryLocked());
        response.setIsActive(ticketPrice.getIsActive());
        response.setCreateTime(ticketPrice.getCreateTime());
        response.setUpdateTime(ticketPrice.getUpdateTime());

        // 设置文本描述和计算字段
        response.setPriceTypeText();
        response.setInventorySold();

        return response;
    }
}
