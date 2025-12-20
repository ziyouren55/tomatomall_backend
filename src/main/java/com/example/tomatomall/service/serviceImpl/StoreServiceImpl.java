package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Store;
import com.example.tomatomall.repository.StoreRepository;
import com.example.tomatomall.service.StoreService;
import com.example.tomatomall.util.UserContext;
import com.example.tomatomall.enums.UserRole;
import com.example.tomatomall.util.MyBeanUtil;
import com.example.tomatomall.vo.PageResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {
    @Autowired
    private StoreRepository storeRepository;

    @Override
    public Store createStore(Store store) {
        // 保持原有行为：直接保存（要求 caller 设置正确的 merchantId）
        if (store.getMerchantId() == null) {
            throw TomatoMallException.invalidRole();
        }
        return storeRepository.save(store);
    }

    @Override
    public Store getStoreById(Integer id) {
        return storeRepository.findById(id).orElseThrow(() -> TomatoMallException.storeNotFind());
    }

    @Override
    public List<Store> findStoresByMerchantId(Integer merchantId) {
        return storeRepository.findByMerchantId(merchantId);
    }

    @Override
    public PageResultVO<Store> getStoresByMerchantPaginated(Integer merchantId, Integer page, Integer pageSize) {
        if (page == null || page < 0) page = 0;
        if (pageSize == null || pageSize <= 0) pageSize = 20;
        Page<Store> p = storeRepository.findByMerchantId(merchantId, PageRequest.of(page, pageSize));
        return new PageResultVO<>(p.getContent(), p.getTotalElements(), page, pageSize);
    }

    @Override
    public Store createStoreByCurrentUser(Store store) {
        Integer currentUserId = UserContext.getCurrentUserId();
        UserRole currentRole = UserContext.getCurrentUserRole();
        if (currentRole == UserRole.MERCHANT && currentUserId != null) {
            store.setMerchantId(currentUserId);
        } else if (currentRole == UserRole.ADMIN) {
            if (store.getMerchantId() == null) throw TomatoMallException.invalidRole();
        } else {
            throw TomatoMallException.permissionDenied();
        }
        return storeRepository.save(store);
    }

    @Override
    public Store updateStoreByCurrentUser(Integer id, Store store) {
        Store existing = getStoreById(id);
        Integer currentUserId = UserContext.getCurrentUserId();
        UserRole currentRole = UserContext.getCurrentUserRole();
        if (currentRole == UserRole.MERCHANT) {
            if (!existing.getMerchantId().equals(currentUserId)) throw TomatoMallException.permissionDenied();
            // 商家不能更改归属
            store.setMerchantId(null);
        }
        BeanUtils.copyProperties(store, existing, MyBeanUtil.getNullPropertyNames(store));
        return storeRepository.save(existing);
    }

    @Override
    public void deleteStoreByCurrentUser(Integer id) {
        Store existing = getStoreById(id);
        Integer currentUserId = UserContext.getCurrentUserId();
        UserRole currentRole = UserContext.getCurrentUserRole();
        if (currentRole == UserRole.MERCHANT) {
            if (!existing.getMerchantId().equals(currentUserId)) throw TomatoMallException.permissionDenied();
        }
        storeRepository.deleteById(id);
    }
}


