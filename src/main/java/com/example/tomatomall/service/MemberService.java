package com.example.tomatomall.service;

import com.example.tomatomall.vo.member.MemberLevelVO;
import com.example.tomatomall.vo.member.MemberPointsVO;
import com.example.tomatomall.vo.member.PointsRecordVO;

import java.util.List;

public interface MemberService {
    // 会员等级相关
    List<MemberLevelVO> getAllMemberLevels();
    MemberLevelVO getMemberLevelById(Integer levelId);
    MemberLevelVO getMemberLevelByUserId(Integer userId);
    MemberLevelVO createMemberLevel(MemberLevelVO levelVO);
    MemberLevelVO updateMemberLevel(MemberLevelVO levelVO);

    // 会员积分相关
    MemberPointsVO getUserPoints(Integer userId);
    PointsRecordVO addUserPoints(Integer userId, Integer points, String recordType, Integer referenceId, String description);
    List<PointsRecordVO> getUserPointsHistory(Integer userId);

    // 会员升级相关
    boolean checkAndUpgradeMemberLevel(Integer userId);
    MemberLevelVO upgradeMemberLevel(Integer userId, Integer targetLevelId);
}
