# 后端白名单配置说明

## 概述

后端拦截器配置了白名单机制，允许未登录用户访问某些公开接口。

## 配置位置

### 1. 拦截器配置 (`MyWebMvcConfig.java`)

在 `excludePathPatterns` 中配置路径级别的排除规则。

### 2. 拦截器白名单逻辑 (`LoginInterceptor.java`)

在 `isWhitelistedRequest` 方法中配置更精确的白名单规则（支持HTTP方法匹配）。

## 当前白名单接口

### 完全公开（不需要登录）

1. **登录接口**
   - `POST /api/accounts/login`
   - 配置位置：`MyWebMvcConfig.excludePathPatterns`

2. **注册接口**
   - `POST /api/accounts`
   - 配置位置：`LoginInterceptor.isWhitelistedRequest`

3. **商品列表接口**
   - `GET /api/products`
   - 说明：允许未登录用户浏览商品列表
   - 注意：商品详情 `GET /api/products/{id}` **需要登录**
   - 配置位置：`LoginInterceptor.isWhitelistedRequest`

4. **论坛列表接口**
   - `GET /api/forums`
   - 说明：允许未登录用户浏览论坛列表
   - 注意：论坛详情 `GET /api/forums/{id}` **需要登录**
   - 配置位置：`LoginInterceptor.isWhitelistedRequest`

5. **活跃论坛接口**
   - `GET /api/forums/active`
   - 配置位置：`LoginInterceptor.isWhitelistedRequest`

6. **广告列表接口**
   - `GET /api/advertisements`
   - 配置位置：`LoginInterceptor.isWhitelistedRequest`

## 需要登录的接口

以下接口**不在白名单中**，需要登录才能访问：

- `GET /api/products/{id}` - 商品详情
- `GET /api/cart` - 购物车
- `GET /api/orders` - 订单列表
- `GET /api/orders/{id}` - 订单详情
- `POST /api/cart` - 添加到购物车
- `PUT /api/cart/{id}` - 更新购物车
- `DELETE /api/cart/{id}` - 删除购物车项
- 所有用户相关接口
- 所有管理员接口

## 配置原则

1. **商品列表公开**：允许未登录用户浏览商品，提升用户体验
2. **商品详情需要登录**：点击查看详情时要求登录，引导用户注册
3. **购物车和订单需要登录**：涉及用户数据，必须登录
4. **精确匹配**：使用HTTP方法+路径的精确匹配，避免误放行

## 添加新白名单接口

如果需要添加新的公开接口，有两种方式：

### 方式1：在 `MyWebMvcConfig.java` 中添加（路径级别）

```java
.excludePathPatterns(
    "/api/accounts/login",
    "/api/your-new-endpoint"  // 添加新路径
)
```

**注意**：这种方式会排除所有HTTP方法和该路径下的所有子路径。

### 方式2：在 `LoginInterceptor.isWhitelistedRequest` 中添加（精确匹配）

```java
// 新接口：GET /api/your-endpoint
if ("GET".equalsIgnoreCase(method) && "/api/your-endpoint".equals(path)) {
    return true;
}
```

**推荐使用方式2**，因为可以精确控制HTTP方法和路径。

## 测试建议

1. **未登录状态测试**：
   - ✅ 访问 `GET /api/products` 应该成功
   - ❌ 访问 `GET /api/products/1` 应该返回401
   - ❌ 访问 `GET /api/cart` 应该返回401

2. **已登录状态测试**：
   - ✅ 所有接口都应该正常工作

