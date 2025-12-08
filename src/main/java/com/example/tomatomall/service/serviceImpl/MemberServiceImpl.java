package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.po.MemberLevel;
import com.example.tomatomall.po.MemberPoints;
import com.example.tomatomall.po.PointsRecord;
import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.repository.MemberLevelRepository;
import com.example.tomatomall.repository.MemberPointsRepository;
import com.example.tomatomall.repository.PointsRecordRepository;
import com.example.tomatomall.service.MemberService;
import com.example.tomatomall.util.MyBeanUtil;
import com.example.tomatomall.vo.member.MemberLevelVO;
import com.example.tomatomall.vo.member.MemberPointsVO;
import com.example.tomatomall.vo.member.PointsRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MemberServiceImpl implements MemberService
{
    @Autowired
    private MemberLevelRepository memberLevelRepository;

    @Autowired
    private MemberPointsRepository memberPointsRepository;

    @Autowired
    private PointsRecordRepository pointsRecordRepository;

    @Autowired
    private AccountRepository accountRepository;

    // 默认的会员等级编号（member_level 字段）
    private static final Integer DEFAULT_LEVEL_CODE = 1;

    // 获取所有会员等级
    @Override
    public List<MemberLevelVO> getAllMemberLevels() {
        List<MemberLevel> levels = memberLevelRepository.findByIsActiveTrue();
        return levels.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    // 根据ID获取会员等级
    @Override
    public MemberLevelVO getMemberLevelById(Integer levelId) {
        MemberLevel level = fetchLevelByCodeOrId(levelId);
        return convertToVO(level);
    }

    // 根据用户ID获取会员等级
    @Override
    public MemberLevelVO getMemberLevelByUserId(Integer userId) {
        // 查询用户当前会员等级ID
        Optional<Account> account = accountRepository.findById(userId);
        if (!account.isPresent()) {
            throw new TomatoMallException("用户不存在");
        }

        Integer levelCode = account.get().getMemberLevelId();
        MemberLevel level = fetchLevelByCodeOrId(levelCode);
        return convertToVO(level);
    }

    // 创建会员等级
    @Override
    public MemberLevelVO createMemberLevel(MemberLevelVO levelVO) {
        // 检查同名等级是否存在
        Optional<MemberLevel> existingLevel = memberLevelRepository.findByLevelName(levelVO.getLevelName());
        if (existingLevel.isPresent()) {
            throw new TomatoMallException("同名会员等级已存在");
        }

        MemberLevel level = new MemberLevel();
        BeanUtils.copyProperties(levelVO, level);
        level.setCreateTime(new Date());
        level.setUpdateTime(new Date());
        memberLevelRepository.save(level);

        return convertToVO(level);
    }

    // 更新会员等级
    @Override
    public MemberLevelVO updateMemberLevel(Integer levelId, MemberLevelVO levelVO) {
        MemberLevel level = memberLevelRepository.findById(levelId)
            .orElseThrow(() -> new TomatoMallException("会员等级不存在"));

        BeanUtils.copyProperties(levelVO, level, MyBeanUtil.getNullPropertyNames(levelVO));
        level.setUpdateTime(new Date());
        memberLevelRepository.save(level);

        return convertToVO(level);
    }

    @Override
    public MemberLevelVO repairMemberLevel(Integer userId) {
        Account account = accountRepository.findById(userId)
            .orElseThrow(() -> new TomatoMallException("用户不存在"));

        // 默认等级记录
        MemberLevel defaultLevel = fetchLevelByCodeOrId(DEFAULT_LEVEL_CODE);

        // 1. 补全账号的会员等级与标志
        if (account.getMemberLevelId() == null || account.getMemberLevelId() <= 0) {
            account.setMemberLevelId(defaultLevel.getMemberLevel());
        }
        account.setIsMember(true);
        accountRepository.save(account);

        // 2. 补全积分记录
        MemberPoints points = memberPointsRepository.findByUserId(userId)
            .orElseGet(() -> {
                MemberPoints newPoints = new MemberPoints();
                newPoints.setUserId(userId);
                newPoints.setCreateTime(new Date());
                newPoints.setUpdateTime(new Date());
                newPoints.setCurrentPoints(0);
                newPoints.setTotalPoints(0);
                newPoints.setCurrentLevelId(defaultLevel.getMemberLevel());
                return newPoints;
            });

        if (points.getCurrentLevelId() == null || points.getCurrentLevelId() <= 0) {
            points.setCurrentLevelId(defaultLevel.getMemberLevel());
        }
        points.setUpdateTime(new Date());
        memberPointsRepository.save(points);

        return convertToVO(defaultLevel);
    }

    // 根据 member_level（优先）或主键ID 获取等级
    private MemberLevel fetchLevelByCodeOrId(Integer codeOrId) {
        if (codeOrId == null) {
            throw new TomatoMallException("会员等级不存在");
        }
        return memberLevelRepository.findByMemberLevel(codeOrId)
            .orElseGet(() -> memberLevelRepository.findById(codeOrId)
                .orElseThrow(() -> new TomatoMallException("会员等级不存在")));
    }

    // 删除会员等级（确保未被使用）
    @Override
    public void deleteMemberLevel(Integer levelId) {
        MemberLevel level = memberLevelRepository.findById(levelId)
            .orElseThrow(() -> new TomatoMallException("会员等级不存在"));

        // 默认最低等级不允许删除
        if (level.getMemberLevel() != null && level.getMemberLevel() == 1) {
            throw new TomatoMallException("默认等级不可删除");
        }

        long accountCount = accountRepository.countByMemberLevelId(levelId);
        long pointsCount = memberPointsRepository.countByCurrentLevelId(levelId);
        if (accountCount > 0 || pointsCount > 0) {
            throw new TomatoMallException("有用户正在使用该等级，无法删除");
        }

        memberLevelRepository.delete(level);
    }

    // 获取用户积分信息
    @Override
    public MemberPointsVO getUserPoints(Integer userId) {
        MemberPoints points = memberPointsRepository.findByUserId(userId)
            .orElseGet(() -> {
                // 如果不存在则创建初始积分记录
                MemberPoints newPoints = new MemberPoints();
                newPoints.setUserId(userId);
                newPoints.setCreateTime(new Date());
                newPoints.setUpdateTime(new Date());
                memberPointsRepository.save(newPoints);
                return newPoints;
            });

        return convertToVO(points);
    }

    // 添加用户积分
    @Override
    public PointsRecordVO addUserPoints(Integer userId, Integer points, String recordType,
                                        Integer referenceId, String description) {
        // 1. 获取用户当前积分
        MemberPoints memberPoints = memberPointsRepository.findByUserId(userId)
            .orElseGet(() -> {
                MemberPoints newPoints = new MemberPoints();
                newPoints.setUserId(userId);
                newPoints.setCreateTime(new Date());
                return newPoints;
            });

        // 2. 更新积分
        memberPoints.setCurrentPoints(memberPoints.getCurrentPoints() + points);
        memberPoints.setTotalPoints(memberPoints.getTotalPoints() + points);
        memberPoints.setUpdateTime(new Date());
        memberPointsRepository.save(memberPoints);

        // 3. 创建积分记录
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPointsChange(points);
        record.setRecordType(recordType);
        record.setReferenceId(referenceId);
        record.setDescription(description);
        record.setCreateTime(new Date());
        pointsRecordRepository.save(record);

        // 4. 检查是否可以升级
        checkAndUpgradeMemberLevel(userId);

        return convertToVO(record);
    }

    // 获取用户积分历史
    @Override
    public List<PointsRecordVO> getUserPointsHistory(Integer userId) {
        List<PointsRecord> records = pointsRecordRepository.findByUserIdOrderByCreateTimeDesc(userId);
        return records.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    // 检查并升级会员等级
    @Override
    public boolean checkAndUpgradeMemberLevel(Integer userId) {
        MemberPoints memberPoints = memberPointsRepository.findByUserId(userId)
            .orElseThrow(() -> new TomatoMallException("用户积分信息不存在"));

        // 查找符合条件的最高等级
        Optional<MemberLevel> eligibleLevel = memberLevelRepository
            .findFirstByPointsRequiredLessThanEqualOrderByPointsRequiredDesc(memberPoints.getTotalPoints());

        if (eligibleLevel.isPresent() && !eligibleLevel.get().getMemberLevel().equals(memberPoints.getCurrentLevelId())) {
            // 升级会员等级
            memberPoints.setCurrentLevelId(eligibleLevel.get().getMemberLevel());
            memberPoints.setUpdateTime(new Date());
            memberPointsRepository.save(memberPoints);

            // 更新用户会员状态
            Optional<Account> account = accountRepository.findById(userId);
            if (account.isPresent()) {
                Account user = account.get();
                user.setMemberLevelId(eligibleLevel.get().getMemberLevel());
                user.setIsMember(true);
                accountRepository.save(user);
            }

            // 记录升级历史
            PointsRecord record = new PointsRecord();
            record.setUserId(userId);
            record.setPointsChange(0); // 升级不改变积分
            record.setRecordType("LEVEL_UP");
            record.setDescription("会员升级到" + eligibleLevel.get().getLevelName());
            record.setCreateTime(new Date());
            pointsRecordRepository.save(record);

            return true;
        }

        return false;
    }

    // 手动升级会员
    @Override
    public MemberLevelVO upgradeMemberLevel(Integer userId, Integer targetLevelId) {
        // 检查目标等级是否存在
        MemberLevel targetLevel = memberLevelRepository.findByMemberLevel(targetLevelId)
            .orElseThrow(() -> new TomatoMallException("目标会员等级不存在"));

        // 更新用户会员状态
        Account account = accountRepository.findById(userId)
            .orElseThrow(() -> new TomatoMallException("用户不存在"));
        account.setMemberLevelId(targetLevel.getMemberLevel());
        account.setIsMember(true);
        accountRepository.save(account);

        // 更新用户积分记录
        MemberPoints memberPoints = memberPointsRepository.findByUserId(userId)
            .orElseGet(() -> {
                MemberPoints newPoints = new MemberPoints();
                newPoints.setUserId(userId);
                newPoints.setCreateTime(new Date());
                return newPoints;
            });
        memberPoints.setCurrentLevelId(targetLevel.getMemberLevel());
        memberPoints.setUpdateTime(new Date());
        memberPointsRepository.save(memberPoints);

        // 记录升级历史
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPointsChange(0);
        record.setRecordType("LEVEL_UP_MANUAL");
        record.setDescription("会员手动升级到" + targetLevel.getLevelName());
        record.setCreateTime(new Date());
        pointsRecordRepository.save(record);

        return convertToVO(targetLevel);
    }

    // 转换工具方法
    private MemberLevelVO convertToVO(MemberLevel level) {
        MemberLevelVO vo = new MemberLevelVO();
        BeanUtils.copyProperties(level, vo);
        return vo;
    }

    private MemberPointsVO convertToVO(MemberPoints points) {
        MemberPointsVO vo = new MemberPointsVO();
        BeanUtils.copyProperties(points, vo);

        // 添加会员等级名称
        if (points.getCurrentLevelId() != null) {
            Optional<MemberLevel> level = memberLevelRepository.findByMemberLevel(points.getCurrentLevelId());
            level.ifPresent(l -> vo.setCurrentLevelName(l.getLevelName()));
        }

        return vo;
    }

    private PointsRecordVO convertToVO(PointsRecord record) {
        PointsRecordVO vo = new PointsRecordVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }
}
