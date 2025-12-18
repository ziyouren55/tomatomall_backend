package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.*;
import com.example.tomatomall.repository.*;
import com.example.tomatomall.service.CouponService;
import com.example.tomatomall.util.MyBeanUtil;
import com.example.tomatomall.vo.coupon.CouponVO;
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
        List<Coupon> coupons = couponRepository.findByIsActiveTrueAndValidFromBeforeAndValidToAfter(now, now);
        return coupons.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    // 创建优惠券
    @Override
    public CouponVO createCoupon(CouponVO couponVO) {
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(couponVO, coupon);
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
            couponId = userCoupon.getCouponId();
        } else {
            Optional<UserCoupon> userCouponOpt = userCouponRepository.findFirstByUserIdAndCouponIdAndIsUsedFalse(userId, couponId);
            if (!userCouponOpt.isPresent()) {
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

        // 5. 获取优惠券详情
        Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new TomatoMallException("优惠券不存在"));

        Date now = new Date();
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

        // 6. 验证优惠券是否满足最低消费
        if (coupon.getMinimumPurchase() != null &&
            order.getTotalAmount().compareTo(coupon.getMinimumPurchase()) < 0) {
            throw new TomatoMallException("订单金额未达到优惠券使用条件");
        }

        // 7. 应用优惠券并更新订单金额
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (coupon.getDiscountAmount() != null) {
            discountAmount = coupon.getDiscountAmount();
        } else if (coupon.getDiscountPercentage() != null) {
            discountAmount = order.getTotalAmount()
                .multiply(coupon.getDiscountPercentage())
                .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        }

        BigDecimal newAmount = order.getTotalAmount().subtract(discountAmount);
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
            .collect(Collectors.toList());

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
        }

        return vo;
    }
}
