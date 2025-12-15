package com.ccthub.userservice.controller;

import com.ccthub.userservice.dto.TicketPriceRequest;
import com.ccthub.userservice.dto.TicketPriceResponse;
import com.ccthub.userservice.service.TicketPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 票价管理Controller
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Tag(name = "票价管理", description = "票价和库存管理接口")
@RestController
@RequestMapping("/api/ticket-prices")
@RequiredArgsConstructor
public class TicketPriceController {
    
    private final TicketPriceService ticketPriceService;
    
    @Operation(summary = "创建或更新票价", description = "设置指定日期的票价和库存")
    @PostMapping
    public ResponseEntity<TicketPriceResponse> saveTicketPrice(@Valid @RequestBody TicketPriceRequest request) {
        TicketPriceResponse response = ticketPriceService.saveTicketPrice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(summary = "批量设置票价", description = "批量设置多个日期的票价")
    @PostMapping("/batch")
    public ResponseEntity<List<TicketPriceResponse>> batchSaveTicketPrices(
            @Valid @RequestBody List<TicketPriceRequest> requests) {
        List<TicketPriceResponse> responses = ticketPriceService.batchSaveTicketPrices(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
    
    @Operation(summary = "删除票价", description = "删除指定ID的票价")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketPrice(
            @Parameter(description = "票价ID") @PathVariable Long id) {
        ticketPriceService.deleteTicketPrice(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "获取票价详情", description = "根据ID获取票价详细信息")
    @GetMapping("/{id}")
    public ResponseEntity<TicketPriceResponse> getTicketPrice(
            @Parameter(description = "票价ID") @PathVariable Long id) {
        TicketPriceResponse response = ticketPriceService.getTicketPriceById(id);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "根据票种查询所有票价", description = "获取指定票种的所有票价")
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<TicketPriceResponse>> getTicketPricesByTicket(
            @Parameter(description = "票种ID") @PathVariable Long ticketId) {
        List<TicketPriceResponse> prices = ticketPriceService.getTicketPricesByTicketId(ticketId);
        return ResponseEntity.ok(prices);
    }
    
    @Operation(summary = "根据日期范围查询票价", description = "获取指定票种在日期范围内的票价")
    @GetMapping("/ticket/{ticketId}/date-range")
    public ResponseEntity<List<TicketPriceResponse>> getTicketPricesByDateRange(
            @Parameter(description = "票种ID") @PathVariable Long ticketId,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<TicketPriceResponse> prices = ticketPriceService.getTicketPricesByDateRange(ticketId, startDate, endDate);
        return ResponseEntity.ok(prices);
    }
}
