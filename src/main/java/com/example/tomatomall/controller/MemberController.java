package com.example.tomatomall.controller;

import com.example.tomatomall.enums.BusinessError;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.service.MemberService;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.member.MemberLevelVO;
import com.example.tomatomall.vo.member.MemberPointsVO;
import com.example.tomatomall.vo.member.PointsRecordVO;
import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.po.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private AccountRepository accountRepository;

    /**
     * 获取用户会员信息
     */
    @GetMapping("/info")
    public Response<Map<String, Object>> getMemberInfo(@RequestAttribute("userId") Integer userId) {
        Account account = accountRepository.findById(userId).orElse(null);

        Map<String, Object> result = new HashMap<>();
        if (account == null) {
            BusinessError error = BusinessError.USER_NOT_FOUND;
            return Response.buildFailure(error.getCode(), error.getMessage());
        }

        boolean isMember = Boolean.TRUE.equals(account.getIsMember());
        result.put("isMember", isMember);
        result.put("memberLevelId", account.getMemberLevelId());

        if (!isMember) {
            // 非会员仅返回状态即可
            return Response.buildSuccess(result);
        }

        MemberPointsVO pointsInfo = null;
        MemberLevelVO levelInfo = null;
        try {
            pointsInfo = memberService.getUserPoints(userId);
            levelInfo = memberService.getMemberLevelByUserId(userId);
        } catch (TomatoMallException e) {
            // 如果等级缺失，尝试自动修复为默认等级
            memberService.repairMemberLevel(userId);
            try {
                pointsInfo = memberService.getUserPoints(userId);
                levelInfo = memberService.getMemberLevelByUserId(userId);
            } catch (Exception ignore) {
                // 仍失败则保持为空，交由前端兜底提示
            }
        }

        result.put("points", pointsInfo);
        result.put("level", levelInfo);

        return Response.buildSuccess(result);
    }

    /**
     * 获取用户当前会员等级
     */
    @GetMapping("/level")
    public Response<MemberLevelVO> getMemberLevel(@RequestAttribute("userId") Integer userId) {
        return Response.buildSuccess(memberService.getMemberLevelByUserId(userId));
    }

    /**
     * 获取所有会员等级列表
     */
    @GetMapping("/levels")
    public Response<List<MemberLevelVO>> getAllLevels() {
        return Response.buildSuccess(memberService.getAllMemberLevels());
    }

    /**
     * 获取用户当前积分
     */
    @GetMapping("/points")
    public Response<MemberPointsVO> getUserPoints(@RequestAttribute("userId") Integer userId) {
        return Response.buildSuccess(memberService.getUserPoints(userId));
    }

    /**
     * 获取用户积分历史记录
     */
    @GetMapping("/points/history")
    public Response<List<PointsRecordVO>> getPointsHistory(@RequestAttribute("userId") Integer userId) {
        return Response.buildSuccess(memberService.getUserPointsHistory(userId));
    }

    /**
     * 兼容旧数据：若会员缺少等级/积分，补全为一级会员
     */
    @PostMapping("/repair")
    public Response<MemberLevelVO> repairMember(@RequestAttribute("userId") Integer userId) {
        return Response.buildSuccess(memberService.repairMemberLevel(userId));
    }
}
