package com.ccthub.userservice.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.entity.Merchant;
import com.ccthub.userservice.repository.MerchantRepository;

/**
 * 商户业务逻辑层
 */
@Service
public class MerchantService {

    @Autowired
    private MerchantRepository merchantRepository;

    /**
     * 创建商户
     */
    @Transactional
    public Merchant createMerchant(Merchant merchant) {
        // 新创建的商户默认待审核状态
        merchant.setAuditStatus(0);
        merchant.setStatus(1);
        return merchantRepository.save(merchant);
    }

    /**
     * 更新商户信息
     */
    @Transactional
    public Merchant updateMerchant(Long id, Merchant merchant) {
        Merchant existing = merchantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商户不存在"));

        // 更新基本信息
        existing.setName(merchant.getName());
        existing.setType(merchant.getType());
        existing.setCooperationType(merchant.getCooperationType());
        existing.setContactPerson(merchant.getContactPerson());
        existing.setContactPhone(merchant.getContactPhone());
        existing.setBusinessLicense(merchant.getBusinessLicense());
        existing.setProvince(merchant.getProvince());
        existing.setCity(merchant.getCity());
        existing.setDistrict(merchant.getDistrict());
        existing.setAddress(merchant.getAddress());
        existing.setLongitude(merchant.getLongitude());
        existing.setLatitude(merchant.getLatitude());
        existing.setSettlementRate(merchant.getSettlementRate());

        return merchantRepository.save(existing);
    }

    /**
     * 审核商户
     */
    @Transactional
    public Merchant auditMerchant(Long id, Integer auditStatus) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商户不存在"));

        if (auditStatus != 1 && auditStatus != 2) {
            throw new RuntimeException("审核状态无效");
        }

        merchant.setAuditStatus(auditStatus);
        return merchantRepository.save(merchant);
    }

    /**
     * 启用/停用商户
     */
    @Transactional
    public Merchant toggleStatus(Long id, Integer status) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商户不存在"));

        merchant.setStatus(status);
        return merchantRepository.save(merchant);
    }

    /**
     * 删除商户
     */
    @Transactional
    public void deleteMerchant(Long id) {
        if (!merchantRepository.existsById(id)) {
            throw new RuntimeException("商户不存在");
        }
        merchantRepository.deleteById(id);
    }

    /**
     * 获取商户详情
     */
    public Merchant getMerchantDetail(Long id) {
        return merchantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商户不存在"));
    }

    /**
     * 分页查询商户列表（支持多条件筛选）
     */
    public Page<Merchant> getMerchantList(
            String name,
            Integer type,
            Integer cooperationType,
            Integer auditStatus,
            Integer status,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));

        return merchantRepository.findByConditions(
                name,
                type,
                cooperationType,
                auditStatus,
                status,
                pageable);
    }

    /**
     * 获取待审核商户列表
     */
    public Page<Merchant> getPendingMerchants(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createTime"));
        return merchantRepository.findByAuditStatus(0, pageable);
    }

    /**
     * 根据区域查询商户
     */
    public List<Merchant> getMerchantsByRegion(String province, String city) {
        return merchantRepository.findByProvinceAndCity(province, city);
    }

    /**
     * 统计商户数据
     */
    public Map<String, Object> getMerchantStatistics() {
        List<Object[]> typeStats = merchantRepository.countByType();
        List<Object[]> auditStats = merchantRepository.countByAuditStatus();

        long totalCount = merchantRepository.count();
        long activeCount = merchantRepository.findByStatus(1, PageRequest.of(0, 1)).getTotalElements();

        return Map.of(
                "total", totalCount,
                "active", activeCount,
                "typeStatistics", typeStats,
                "auditStatistics", auditStats);
    }
}
