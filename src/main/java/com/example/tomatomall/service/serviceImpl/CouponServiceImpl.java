package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.enums.CouponType;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.*;
import com.example.tomatomall.repository.*;
import com.example.tomatomall.service.CouponService;
import com.example.tomatomall.util.MyBeanUtil;
import com.example.tomatomall.vo.coupon.CouponVO;
import com.example.tomatomall.vo.coupon.IssueChatCouponVO;
import com.example.tomatomall.vo.coupon.UserCouponVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CouponServiceImpl implements CouponService
{
    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private MemberPointsRepository memberPointsRepository;

    @Autowired
    private PointsRecordRepository pointsRecordRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    // 获取所有优惠券
    @Override
    public List<CouponVO> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAll();
        return coupons.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    // 根据ID获取优惠券
    @Override
    public CouponVO getCouponById(Integer couponId) {
        Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new TomatoMallException("优惠券不存在"));
        return convertToVO(coupon);
    }

    // 获取所有可用优惠券
    @Override
    public List<CouponVO> getAvailableCoupons() {
        Date now = new Date();
        // 只返回可重复兑换的优惠券（type = REPEAT），排除私人优惠券
        // 使用大写枚举值进行查询，确保兼容性
        List<Coupon> coupons = couponRepository.findByIsActiveTrueAndTypeAndValidFromBeforeAndValidToAfter(CouponType.REPEAT, now, now);
        return coupons.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    // 创建优惠券
    @Override
    public CouponVO createCoupon(CouponVO couponVO) {
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(couponVO, coupon);

        // 标准化优惠券类型：统一转换为大写，确保数据库存储一致性
        // 支持前端传入各种格式（repeat, REPEAT, private, PRIVATE等）
        if (coupon.getType() != null) {
            coupon.setType(CouponType.fromValue(coupon.getType().name().toUpperCase()));
        } else {
            // 默认设置为REPEAT类型（兼容旧数据）
            coupon.setType(CouponType.REPEAT);
        }

        coupon.setCreateTime(new Date());
        couponRepository.save(coupon);
        return convertToVO(coupon);
    }

    // 更新优惠券
    @Override
    public CouponVO updateCoupon(CouponVO couponVO) {
        Coupon coupon = couponRepository.findById(couponVO.getId())
            .orElseThrow(() -> new TomatoMallException("优惠券不存在"));

        BeanUtils.copyProperties(couponVO, coupon, MyBeanUtil.getNullPropertyNames(couponVO));
        couponRepository.save(coupon);
        return convertToVO(coupon);
    }

    // 获取用户优惠券
    @Override
    public List<UserCouponVO> getUserCoupons(Integer userId) {
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);
        return userCoupons.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    // 兑换优惠券
    @Override
    public UserCouponVO exchangeCoupon(Integer userId, Integer couponId) {
        // 1. 检查优惠券是否存在且有效
        Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new TomatoMallException("优惠券不存在"));

        Date now = new Date();
        if (!coupon.getIsActive() || coupon.getValidFrom().after(now) || coupon.getValidTo().before(now)) {
            throw new TomatoMallException("优惠券已失效或未到生效时间");
        }

        // 2. 检查用户积分是否足够
        MemberPoints memberPoints = memberPointsRepository.findByUserId(userId)
            .orElseThrow(() -> new TomatoMallException("用户积分信息不存在"));

        if (memberPoints.getCurrentPoints() < coupon.getPointsRequired()) {
            throw new TomatoMallException("积分不足，无法兑换该优惠券");
        }

        // 3. 扣减积分
        memberPoints.setCurrentPoints(memberPoints.getCurrentPoints() - coupon.getPointsRequired());
        memberPoints.setUpdateTime(new Date());
        memberPointsRepository.save(memberPoints);

        // 4. 记录积分变动
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPointsChange(-coupon.getPointsRequired());
        record.setRecordType("EXCHANGE");
        record.setReferenceId(couponId);
        record.setDescription("兑换优惠券：" + coupon.getName());
        record.setCreateTime(new Date());
        pointsRecordRepository.save(record);

        // 5. 创建用户优惠券
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setCreateTime(new Date());
        userCouponRepository.save(userCoupon);

        return convertToVO(userCoupon);
    }


    // 应用优惠券到订单
    @Override
    @Transactional
    public boolean applyCouponToOrder(Integer userId, Integer userCouponId, Integer couponId, Integer orderId) {
        // 1. 检查用户优惠券是否存在且未使用
        UserCoupon userCoupon;
        if (userCouponId != null) {
            userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new TomatoMallException("无效的用户优惠券"));
            if (!userCoupon.getUserId().equals(userId)) {
                throw new TomatoMallException("无权使用该优惠券");
            }
            if (Boolean.TRUE.equals(userCoupon.getIsUsed())) {
                throw new TomatoMallException("优惠券已被使用");
            }
        } else {
            Optional<UserCoupon> userCouponOpt = userCouponRepository.findFirstByUserIdAndCouponIdAndIsUsedFalse(userId, couponId);
            if (userCouponOpt.isEmpty()) {
                throw new TomatoMallException("无效的优惠券或优惠券已使用");
            }
            userCoupon = userCouponOpt.get();
        }

        // 2. 检查订单是否存在
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new TomatoMallException("订单不存在"));

        // 3. 验证用户是否是订单所有者
        if (!order.getUserId().equals(userId)) {
            throw new TomatoMallException("无权操作此订单");
        }

        // 4. 检查订单状态
        if (!"PENDING".equals(order.getStatus())) {
            throw new TomatoMallException("只能对待支付订单使用优惠券");
        }

        // 5. 获取优惠券详情和验证有效性
        Coupon coupon = couponRepository.findById(userCoupon.getCouponId())
            .orElseThrow(() -> new TomatoMallException("优惠券不存在"));

        Date now = new Date();
        if (!Boolean.TRUE.equals(coupon.getIsActive())) {
            throw new TomatoMallException("优惠券未生效");
        }

        if(coupon.getValidFrom().after(now)) {
            throw new TomatoMallException("优惠券未到达指定时间");
        }

        if(coupon.getValidTo().before(now)) {
            throw new TomatoMallException("优惠券已过期");
        }

        BigDecimal discountAmount = coupon.getDiscountAmount();
        BigDecimal minimumPurchase = coupon.getMinimumPurchase();
        BigDecimal discountPercentage = coupon.getDiscountPercentage();

        // 6. 验证优惠券是否满足最低消费
        if (minimumPurchase != null &&
            order.getTotalAmount().compareTo(minimumPurchase) < 0) {
            throw new TomatoMallException("订单金额未达到优惠券使用条件");
        }

        // 6.5. 验证商品限制（如果优惠券有商品限制）
        if (userCoupon.hasProductRestriction()) {
            validateCouponProductRestriction(userCoupon, orderId);
        }

        // 7. 应用优惠券并更新订单金额
        BigDecimal actualDiscountAmount = BigDecimal.ZERO;

        if (discountAmount != null) {
            actualDiscountAmount = discountAmount;
        } else if (discountPercentage != null) {
            actualDiscountAmount = order.getTotalAmount()
                .multiply(discountPercentage)
                .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
        }

        BigDecimal newAmount = order.getTotalAmount().subtract(actualDiscountAmount);
        if (newAmount.compareTo(BigDecimal.ZERO) < 0) {
            newAmount = BigDecimal.ZERO;
        }

        order.setTotalAmount(newAmount);
        orderRepository.save(order);

        // 8. 更新优惠券状态为已使用
        userCoupon.setIsUsed(true);
        userCoupon.setUsedTime(new Date());
        userCoupon.setOrderId(orderId);
        userCouponRepository.save(userCoupon);

        return true;
    }

    @Override
    @Transactional
    public boolean releaseCoupon(Integer userId, Integer userCouponId, Integer orderId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
            .orElseThrow(() -> new TomatoMallException("用户优惠券不存在"));

        if (!userCoupon.getUserId().equals(userId)) {
            throw new TomatoMallException("无权操作此优惠券");
        }

        if (!Boolean.TRUE.equals(userCoupon.getIsUsed())) {
            return true;
        }

        if (userCoupon.getOrderId() != null && orderId != null && !userCoupon.getOrderId().equals(orderId)) {
            throw new TomatoMallException("订单信息不匹配，无法释放优惠券");
        }

        userCoupon.setIsUsed(false);
        userCoupon.setOrderId(null);
        userCoupon.setUsedTime(null);
        userCouponRepository.save(userCoupon);

        return true;
    }

    // 根据ID获取用户优惠券
    @Override
    public UserCouponVO getUserCouponById(Integer userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
            .orElseThrow(() -> new TomatoMallException("用户优惠券不存在"));
        return convertToVO(userCoupon);
    }

    @Override
    public UserCouponVO issueCouponToUser(Integer couponId, Integer userId, String remark) {
        // 1. 检查优惠券是否存在
        couponRepository.findById(couponId)
            .orElseThrow(() -> new TomatoMallException("优惠券不存在"));

        // 2. 创建用户优惠券记录（无需扣减积分）
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setCreateTime(new Date());
        userCouponRepository.save(userCoupon);

        // 3. 添加操作记录（可选）
        // ...

        return convertToVO(userCoupon);
    }

    @Override
    @Transactional
    public Integer issueCouponToAllUsers(Integer couponId, String remark) {
        Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new TomatoMallException("优惠券不存在"));

        // 校验券是否可发
        Date now = new Date();
        System.out.println(now);
        if (!Boolean.TRUE.equals(coupon.getIsActive())) {
            throw new TomatoMallException("优惠券未生效");
        }

        if(coupon.getValidFrom().after(now))
        {
            throw new TomatoMallException("优惠券未到达指定时间");
        }

        if(coupon.getValidTo().before(now))
        {
            throw new TomatoMallException("优惠券已过期");
        }

        // 拉取所有普通用户（排除管理员）
        List<Account> accounts = accountRepository.findAll()
            .stream()
            .filter(acc -> acc.getRole() == null || !"ADMIN".equalsIgnoreCase(acc.getRole().name()))
            .toList();

        if (accounts.isEmpty()) {
            return 0;
        }

        // 批量构建用户券
        List<UserCoupon> batch = accounts.stream().map(acc -> {
            UserCoupon uc = new UserCoupon();
            uc.setUserId(acc.getId());
            uc.setCouponId(couponId);
            uc.setCreateTime(new Date());
            return uc;
        }).collect(Collectors.toList());

        userCouponRepository.saveAll(batch);
        return batch.size();
    }

    // 转换工具方法
    private CouponVO convertToVO(Coupon coupon) {
        CouponVO vo = new CouponVO();
        BeanUtils.copyProperties(coupon, vo);
        return vo;
    }

    private UserCouponVO convertToVO(UserCoupon userCoupon) {
        UserCouponVO vo = new UserCouponVO();
        BeanUtils.copyProperties(userCoupon, vo);

        // 添加优惠券详情
        if (userCoupon.getCouponId() != null) {
            // 从Coupon模板获取信息
            Optional<Coupon> coupon = couponRepository.findById(userCoupon.getCouponId());
            if (coupon.isPresent()) {
                vo.setCouponName(coupon.get().getName());
                vo.setCouponDescription(coupon.get().getDescription());
                vo.setDiscountAmount(coupon.get().getDiscountAmount());
                vo.setDiscountPercentage(coupon.get().getDiscountPercentage());
                vo.setMinimumPurchase(coupon.get().getMinimumPurchase());
                vo.setPointsRequired(coupon.get().getPointsRequired());
                vo.setValidFrom(coupon.get().getValidFrom());
                vo.setValidTo(coupon.get().getValidTo());
                vo.setIsActive(coupon.get().getIsActive());
                // 确保返回的类型总是枚举值
                vo.setType(coupon.get().getType());
            }
        }

        return vo;
    }

    /**
     * 创建私人优惠券模板（商家在聊天中发放的优惠券）
     */
    private CouponVO createPrivateCouponTemplate(IssueChatCouponVO request, Integer merchantId) {
        CouponVO couponVO = new CouponVO();

        // 获取商品信息用于优惠券命名
        Optional<Product> productOpt = productRepository.findById(request.getProductId());
        String productName = productOpt.isPresent() ? productOpt.get().getTitle() : "指定商品";

        // 设置优惠券基本信息
        couponVO.setName(productName + "专属优惠券");
        couponVO.setDescription("商家在聊天中发放的专属优惠券，仅限" + productName + "使用");
        couponVO.setDiscountAmount(request.getDiscountAmount());
        couponVO.setDiscountPercentage(request.getDiscountPercentage());
        couponVO.setMinimumPurchase(request.getMinimumPurchase());
        couponVO.setPointsRequired(0); // 私人优惠券不需要积分
        couponVO.setType(CouponType.PRIVATE); // 标记为私人优惠券

        // 设置有效期
        Date now = new Date();
        couponVO.setValidFrom(now);
        couponVO.setValidTo(new Date(now.getTime() + (request.getValidDays() * 24 * 60 * 60 * 1000L)));

        couponVO.setIsActive(true);

        return couponVO;
    }

    // 商家聊天优惠券功能实现
    @Override
    @Transactional
    public UserCouponVO createAndIssueChatCoupon(Integer merchantId, IssueChatCouponVO request) {
        // 1. 验证商家权限
        if (!validateMerchantProductPermission(merchantId, request.getProductId())) {
            throw new TomatoMallException("您没有权限为此商品发放优惠券");
        }

        // 2. 获取聊天会话信息，确认客户身份
        Optional<ChatSession> sessionOpt = chatSessionRepository.findById(request.getSessionId());
        if (sessionOpt.isEmpty()) {
            throw new TomatoMallException("聊天会话不存在");
        }

        ChatSession session = sessionOpt.get();
        if (!session.getMerchantId().equals(merchantId)) {
            throw new TomatoMallException("您不是此会话的商家");
        }

        Integer customerId = session.getCustomerId();

        // 3. 创建私人优惠券模板（type = "private"）
        CouponVO privateCouponVO = createPrivateCouponTemplate(request, merchantId);
        CouponVO createdCoupon = createCoupon(privateCouponVO);

        // 4. 发放优惠券给用户
        UserCouponVO userCoupon = issueCouponToUser(createdCoupon.getId(), customerId, request.getRemark());

        // 5. 设置商品限制和商家信息
        Optional<UserCoupon> userCouponEntityOpt = userCouponRepository.findById(userCoupon.getId());
        if (userCouponEntityOpt.isPresent()) {
            UserCoupon userCouponEntity = userCouponEntityOpt.get();
            userCouponEntity.setProductId(request.getProductId());
            userCouponEntity.setMerchantId(merchantId);
            userCouponEntity.setIssuedRemark(request.getRemark());
            userCouponRepository.save(userCouponEntity);

            // 更新VO
            userCoupon.setProductId(request.getProductId());
            userCoupon.setMerchantId(merchantId);
            userCoupon.setIssuedRemark(request.getRemark());
        }

        return userCoupon;
    }

    @Override
    public boolean validateMerchantProductPermission(Integer merchantId, Integer productId) {
        // 获取商品信息
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return false;
        }

        Product product = productOpt.get();

        // 检查商品是否属于该商家的店铺
        return product.getStoreId() != null &&
               storeRepository.existsByIdAndMerchantId(product.getStoreId(), merchantId);
    }

    @Override
    public List<UserCouponVO> getCouponsIssuedByMerchant(Integer merchantId) {
        List<UserCoupon> userCoupons = userCouponRepository.findByMerchantId(merchantId);
        return userCoupons.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }



    /**
     * 验证优惠券的商品限制
     */
    private void validateCouponProductRestriction(UserCoupon userCoupon, Integer orderId) {
        // 获取订单中的商品项
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        // 检查订单中是否包含限制的商品
        boolean hasRestrictedProduct = orderItems.stream()
            .anyMatch(item -> userCoupon.canUseForProduct(item.getProductId()));

        if (!hasRestrictedProduct) {
            // 获取商品名称用于错误提示
            Optional<Product> productOpt = productRepository.findById(userCoupon.getProductId());
            String productName = productOpt.isPresent() ? productOpt.get().getTitle() : "指定商品";

            throw new TomatoMallException("该优惠券只能用于商品：" + productName);
        }
    }
}
