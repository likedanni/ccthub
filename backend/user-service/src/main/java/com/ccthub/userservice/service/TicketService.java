package com.ccthub.userservice.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.dto.TicketRequest;
import com.ccthub.userservice.dto.TicketResponse;
import com.ccthub.userservice.entity.Ticket;
import com.ccthub.userservice.repository.ScenicSpotRepository;
import com.ccthub.userservice.repository.TicketRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 票种服务类
 * 
 * @author CCTHub
 * @date 2025-12-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ScenicSpotRepository scenicSpotRepository;
    private final ObjectMapper objectMapper;

    /**
     * 创建票种
     */
    @Transactional
    public TicketResponse createTicket(TicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setScenicSpotId(request.getScenicSpotId());
        ticket.setName(request.getName());
        ticket.setType(request.getType());
        ticket.setDescription(request.getDescription());
        ticket.setValidityType(request.getValidityType());
        ticket.setValidDays(request.getValidDays());
        ticket.setAdvanceDays(request.getAdvanceDays());
        ticket.setRefundPolicy(convertMapToJson(request.getRefundPolicy()));
        ticket.setChangePolicy(convertMapToJson(request.getChangePolicy()));
        ticket.setLimitPerUser(request.getLimitPerUser());
        ticket.setLimitPerOrder(request.getLimitPerOrder());
        ticket.setLimitPerDay(request.getLimitPerDay());
        ticket.setRequireRealName(request.getRequireRealName());
        ticket.setRequireIdCard(request.getRequireIdCard());
        ticket.setVerificationMode(request.getVerificationMode());
        ticket.setStatus(request.getStatus());

        Ticket saved = ticketRepository.save(ticket);
        log.info("Created ticket: id={}, name={}", saved.getId(), saved.getName());

        return convertToResponse(saved);
    }

    /**
     * 更新票种
     */
    @Transactional
    public TicketResponse updateTicket(Long id, TicketRequest request) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("票种不存在: " + id));

        ticket.setName(request.getName());
        ticket.setType(request.getType());
        ticket.setDescription(request.getDescription());
        ticket.setValidityType(request.getValidityType());
        ticket.setValidDays(request.getValidDays());
        ticket.setAdvanceDays(request.getAdvanceDays());
        ticket.setRefundPolicy(convertMapToJson(request.getRefundPolicy()));
        ticket.setChangePolicy(convertMapToJson(request.getChangePolicy()));
        ticket.setLimitPerUser(request.getLimitPerUser());
        ticket.setLimitPerOrder(request.getLimitPerOrder());
        ticket.setLimitPerDay(request.getLimitPerDay());
        ticket.setRequireRealName(request.getRequireRealName());
        ticket.setRequireIdCard(request.getRequireIdCard());
        ticket.setVerificationMode(request.getVerificationMode());
        ticket.setStatus(request.getStatus());

        Ticket updated = ticketRepository.save(ticket);
        log.info("Updated ticket: id={}, name={}", updated.getId(), updated.getName());

        return convertToResponse(updated);
    }

    /**
     * 删除票种
     */
    @Transactional
    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new RuntimeException("票种不存在: " + id);
        }
        ticketRepository.deleteById(id);
        log.info("Deleted ticket: id={}", id);
    }

    /**
     * 根据ID查询票种
     */
    public TicketResponse getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("票种不存在: " + id));
        return convertToResponse(ticket);
    }

    /**
     * 分页查询票种（支持按景区、名称、状态过滤）
     */
    public Page<TicketResponse> getTickets(Pageable pageable, Long scenicSpotId, String name, Integer status) {
        Page<Ticket> page = ticketRepository.findByFilters(scenicSpotId, name, status, pageable);
        return page.map(this::convertToResponse);
    }

    /**
     * 根据景区ID查询票种
     */
    public List<TicketResponse> getTicketsByScenicSpotId(Long scenicSpotId) {
        return ticketRepository.findByScenicSpotId(scenicSpotId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据景区ID和状态查询票种
     */
    public List<TicketResponse> getTicketsByScenicSpotIdAndStatus(Long scenicSpotId, Integer status) {
        return ticketRepository.findByScenicSpotIdAndStatus(scenicSpotId, status)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 更新票种状态
     */
    @Transactional
    public void updateTicketStatus(Long id, Integer status) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("票种不存在: " + id));
        ticket.setStatus(status);
        ticketRepository.save(ticket);
        log.info("Updated ticket status: id={}, status={}", id, status);
    }

    /**
     * 转换为响应DTO
     */
    private TicketResponse convertToResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setScenicSpotId(ticket.getScenicSpotId());

        // 查询并设置景区名称
        scenicSpotRepository.findById(ticket.getScenicSpotId())
                .ifPresent(spot -> response.setScenicSpotName(spot.getName()));

        response.setName(ticket.getName());
        response.setType(ticket.getType());
        response.setDescription(ticket.getDescription());
        response.setValidityType(ticket.getValidityType());
        response.setValidDays(ticket.getValidDays());
        response.setAdvanceDays(ticket.getAdvanceDays());
        response.setRefundPolicy(convertJsonToMap(ticket.getRefundPolicy()));
        response.setChangePolicy(convertJsonToMap(ticket.getChangePolicy()));
        response.setLimitPerUser(ticket.getLimitPerUser());
        response.setLimitPerOrder(ticket.getLimitPerOrder());
        response.setLimitPerDay(ticket.getLimitPerDay());
        response.setRequireRealName(ticket.getRequireRealName());
        response.setRequireIdCard(ticket.getRequireIdCard());
        response.setVerificationMode(ticket.getVerificationMode());
        response.setStatus(ticket.getStatus());
        response.setCreateTime(ticket.getCreateTime());
        response.setUpdateTime(ticket.getUpdateTime());

        // 设置文本描述
        response.setTypeText();
        response.setValidityTypeText();
        response.setVerificationModeText();
        response.setStatusText();

        return response;
    }

    /**
     * Map转JSON字符串
     */
    private String convertMapToJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert map to JSON", e);
            throw new RuntimeException("JSON转换失败", e);
        }
    }

    /**
     * JSON字符串转Map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertJsonToMap(String json) {
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert JSON to map", e);
            throw new RuntimeException("JSON解析失败", e);
        }
    }
}
