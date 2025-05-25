package com.example.tomatomall.controller;

import com.example.tomatomall.service.MemberService;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.member.MemberLevelVO;
import com.example.tomatomall.vo.member.PointsAdjustmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/member")
public class AdminMemberController {
    @Autowired
    private MemberService memberService;

    /**
     * 获取所有会员等级
     */
    @GetMapping("/levels")
    public Response getAllLevels() {
        return Response.buildSuccess(memberService.getAllMemberLevels());
    }

    /**
     * 根据ID获取会员等级
     */
    @GetMapping("/levels/{levelId}")
    public Response getLevelById(@PathVariable Integer levelId) {
        return Response.buildSuccess(memberService.getMemberLevelById(levelId));
    }

    /**
     * 创建会员等级
     */
    @PostMapping("/levels")
    public Response createLevel(@RequestBody MemberLevelVO levelVO) {
        return Response.buildSuccess(memberService.createMemberLevel(levelVO));
    }

    /**
     * 更新会员等级
     */
    @PutMapping("/levels/{levelId}")
    public Response updateLevel(@PathVariable Integer levelId, @RequestBody MemberLevelVO levelVO) {
        return Response.buildSuccess(memberService.updateMemberLevel(levelId, levelVO));
    }

    /**
     * 手动升级用户会员等级
     */
    @PostMapping("/upgrade/{userId}")
    public Response upgradeUserLevel(@PathVariable Integer userId, @RequestParam Integer targetLevelId) {
        return Response.buildSuccess(memberService.upgradeMemberLevel(userId, targetLevelId));
    }

    /**
     * 查看用户积分记录
     */
    @GetMapping("/points/{userId}")
    public Response getUserPointsHistory(@PathVariable Integer userId) {
        return Response.buildSuccess(memberService.getUserPointsHistory(userId));
    }

    /**
     * 手动调整用户积分
     */
    @PostMapping("/points/adjust")
    public Response adjustUserPoints(@RequestBody PointsAdjustmentVO adjustment) {
        return Response.buildSuccess(memberService.addUserPoints(
            adjustment.getUserId(),
            adjustment.getPointsChange(),
            "ADMIN_ADJUSTMENT",
            null,
            adjustment.getReason()
        ));
    }
}
