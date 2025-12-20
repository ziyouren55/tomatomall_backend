package com.example.tomatomall.controller;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Store;
import com.example.tomatomall.repository.StoreRepository;
import com.example.tomatomall.service.StoreService;
import com.example.tomatomall.util.UserContext;
import com.example.tomatomall.vo.PageResultVO;
import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    @Resource
    private StoreService storeService;

    @Resource
    private StoreRepository storeRepository;

    /**
     * 获取当前商家的店铺（分页）
     */
    @GetMapping("/merchant")
    public Response<PageResultVO<Store>> getMerchantStores(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) throw TomatoMallException.notLogin();
        return Response.buildSuccess(storeService.getStoresByMerchantPaginated(currentUserId, page, pageSize));
    }

    /**
     * 根据ID获取店铺
     */
    @GetMapping("/{id}")
    public Response<Store> getStoreById(@PathVariable Integer id) {
        return Response.buildSuccess(storeService.getStoreById(id));
    }

    /**
     * 创建店铺
     * - 商家创建时，自动设置 merchantId 为当前用户
     * - 管理员可以为任意商家创建店铺，需在请求体中传 merchantId
     */
    @PostMapping
    public Response<Store> createStore(@RequestBody Store store) {
        // 调用 Service 层，Service 从请求上下文中获取当前用户信息并进行权限校验
        return Response.buildSuccess(storeService.createStoreByCurrentUser(store));
    }

    /**
     * 更新店铺（管理员可以更新任意店铺；商家只能更新自己的店铺）
     */
    @PutMapping("/{id}")
    public Response<Store> updateStore(@PathVariable Integer id, @RequestBody Store store) {
        // 调用 Service 层处理权限与更新逻辑
        return Response.buildSuccess(storeService.updateStoreByCurrentUser(id, store));
    }

    /**
     * 删除店铺（管理员可删除任意；商家只能删除自己的）
     */
    @DeleteMapping("/{id}")
    public Response<String> deleteStore(@PathVariable Integer id) {
        // 调用 Service 层处理权限与删除
        storeService.deleteStoreByCurrentUser(id);
        return Response.buildSuccess("删除成功");
    }
}


