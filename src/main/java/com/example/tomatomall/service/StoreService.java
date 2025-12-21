package com.example.tomatomall.service;

import com.example.tomatomall.po.Store;
import com.example.tomatomall.vo.PageResultVO;

import java.util.List;

public interface StoreService {
    Store createStore(Store store);
    Store getStoreById(Integer id);
    List<Store> findStoresByMerchantId(Integer merchantId);
    PageResultVO<Store> getStoresByMerchantPaginated(Integer merchantId, Integer page, Integer pageSize);
    /**
     * 管理员：分页获取所有店铺
     * @param page 分页页码（0-based）
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResultVO<Store> getAllStores(Integer page, Integer pageSize);
    /**
     * 根据当前请求的用户创建店铺
     * - 商家创建时自动设置 merchantId 为当前用户
     * - 管理员可以为任意 merchantId 创建（需在请求体中传入 merchantId）
     */
    Store createStoreByCurrentUser(Store store);

    /**
     * 根据当前请求的用户更新店铺
     * - 商家只能更新自己的店铺（且不能更改 merchantId）
     * - 管理员可以更新任意店铺
     */
    Store updateStoreByCurrentUser(Integer id, Store store);

    /**
     * 根据当前请求的用户删除店铺
     * - 商家只能删除自己的店铺
     * - 管理员可以删除任意店铺
     */
    void deleteStoreByCurrentUser(Integer id);
}


