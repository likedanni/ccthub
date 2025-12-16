package com.ccthub.userservice.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccthub.userservice.entity.UserAddress;
import com.ccthub.userservice.repository.UserAddressRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户地址服务
 * 
 * @author CCTHub
 * @date 2025-12-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAddressService {

    private final UserAddressRepository userAddressRepository;

    /**
     * 创建地址
     */
    @Transactional(rollbackFor = Exception.class)
    public UserAddress createAddress(UserAddress address) {
        // 如果设置为默认地址,先取消用户其他默认地址
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            cancelOtherDefaultAddress(address.getUserId());
        }

        return userAddressRepository.save(address);
    }

    /**
     * 更新地址
     */
    @Transactional(rollbackFor = Exception.class)
    public UserAddress updateAddress(Long id, UserAddress address) {
        UserAddress existing = userAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("地址不存在"));

        // 如果设置为默认地址,先取消用户其他默认地址
        if (Boolean.TRUE.equals(address.getIsDefault()) && !Boolean.TRUE.equals(existing.getIsDefault())) {
            cancelOtherDefaultAddress(existing.getUserId());
        }

        existing.setRecipientName(address.getRecipientName());
        existing.setRecipientPhone(address.getRecipientPhone());
        existing.setProvince(address.getProvince());
        existing.setCity(address.getCity());
        existing.setDistrict(address.getDistrict());
        existing.setDetailAddress(address.getDetailAddress());
        existing.setPostalCode(address.getPostalCode());
        existing.setIsDefault(address.getIsDefault());

        return userAddressRepository.save(existing);
    }

    /**
     * 删除地址
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Long id) {
        UserAddress address = userAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("地址不存在"));

        // 如果删除的是默认地址,需要将第一个地址设为默认
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            userAddressRepository.delete(address);
            List<UserAddress> addresses = userAddressRepository
                    .findByUserIdOrderByIsDefaultDescCreateTimeDesc(address.getUserId());
            if (!addresses.isEmpty()) {
                UserAddress first = addresses.get(0);
                first.setIsDefault(true);
                userAddressRepository.save(first);
            }
        } else {
            userAddressRepository.delete(address);
        }
    }

    /**
     * 设置默认地址
     */
    @Transactional(rollbackFor = Exception.class)
    public UserAddress setDefaultAddress(Long id) {
        UserAddress address = userAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("地址不存在"));

        // 取消用户其他默认地址
        cancelOtherDefaultAddress(address.getUserId());

        address.setIsDefault(true);
        return userAddressRepository.save(address);
    }

    /**
     * 查询用户所有地址
     */
    @Transactional(readOnly = true)
    public List<UserAddress> getUserAddresses(Long userId) {
        return userAddressRepository.findByUserIdOrderByIsDefaultDescCreateTimeDesc(userId);
    }

    /**
     * 查询用户默认地址
     */
    @Transactional(readOnly = true)
    public UserAddress getDefaultAddress(Long userId) {
        return userAddressRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElse(null);
    }

    /**
     * 查询地址详情
     */
    @Transactional(readOnly = true)
    public UserAddress getAddress(Long id) {
        return userAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("地址不存在"));
    }

    /**
     * 取消用户其他默认地址
     */
    private void cancelOtherDefaultAddress(Long userId) {
        List<UserAddress> defaultAddresses = userAddressRepository.findByUserId(userId);
        for (UserAddress addr : defaultAddresses) {
            if (Boolean.TRUE.equals(addr.getIsDefault())) {
                addr.setIsDefault(false);
                userAddressRepository.save(addr);
            }
        }
    }
}
