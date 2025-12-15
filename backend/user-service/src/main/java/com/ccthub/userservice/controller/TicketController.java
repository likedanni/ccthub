package com.ccthub.userservice.controller;

import com.ccthub.userservice.dto.TicketRequest;
import com.ccthub.userservice.dto.TicketResponse;
import com.ccthub.userservice.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 票种管理Controller
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Tag(name = "票种管理", description = "票种的增删改查接口")
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    
    private final TicketService ticketService;
    
    @Operation(summary = "创建票种", description = "创建新的门票产品")
    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketRequest request) {
        TicketResponse response = ticketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(summary = "更新票种", description = "更新指定ID的票种信息")
    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> updateTicket(
            @Parameter(description = "票种ID") @PathVariable Long id,
            @Valid @RequestBody TicketRequest request) {
        TicketResponse response = ticketService.updateTicket(id, request);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "删除票种", description = "删除指定ID的票种")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(
            @Parameter(description = "票种ID") @PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "获取票种详情", description = "根据ID获取票种详细信息")
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicket(
            @Parameter(description = "票种ID") @PathVariable Long id) {
        TicketResponse response = ticketService.getTicketById(id);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "分页查询票种", description = "分页获取所有票种列表")
    @GetMapping
    public ResponseEntity<Page<TicketResponse>> getTickets(
            @PageableDefault(size = 10, sort = "createTime", direction = Sort.Direction.DESC) 
            Pageable pageable) {
        Page<TicketResponse> page = ticketService.getTickets(pageable);
        return ResponseEntity.ok(page);
    }
    
    @Operation(summary = "根据景区查询票种", description = "获取指定景区的所有票种")
    @GetMapping("/scenic-spot/{scenicSpotId}")
    public ResponseEntity<List<TicketResponse>> getTicketsByScenicSpot(
            @Parameter(description = "景区ID") @PathVariable Long scenicSpotId) {
        List<TicketResponse> tickets = ticketService.getTicketsByScenicSpotId(scenicSpotId);
        return ResponseEntity.ok(tickets);
    }
    
    @Operation(summary = "根据景区和状态查询票种", description = "获取指定景区指定状态的票种")
    @GetMapping("/scenic-spot/{scenicSpotId}/status/{status}")
    public ResponseEntity<List<TicketResponse>> getTicketsByScenicSpotAndStatus(
            @Parameter(description = "景区ID") @PathVariable Long scenicSpotId,
            @Parameter(description = "状态：0-下架，1-上架") @PathVariable Integer status) {
        List<TicketResponse> tickets = ticketService.getTicketsByScenicSpotIdAndStatus(scenicSpotId, status);
        return ResponseEntity.ok(tickets);
    }
    
    @Operation(summary = "更新票种状态", description = "上架或下架票种")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateTicketStatus(
            @Parameter(description = "票种ID") @PathVariable Long id,
            @Parameter(description = "状态：0-下架，1-上架") @RequestParam Integer status) {
        ticketService.updateTicketStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
