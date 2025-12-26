package com.example.tomatomall.controller;

import com.example.tomatomall.service.MemberService;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.member.MemberLevelVO;
import com.example.tomatomall.vo.member.PointsAdjustmentVO;
import com.example.tomatomall.vo.member.PointsRecordVO;
import java.util.List;
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
    public Response<List<MemberLevelVO>> getAllLevels() {
        return Response.buildSuccess(memberService.getAllMemberLevels());
    }

    /**
     * 根据ID获取会员等级
     */
    @GetMapping("/levels/{levelId}")
    public Response<MemberLevelVO> getLevelById(@PathVariable Integer levelId) {
        return Response.buildSuccess(memberService.getMemberLevelById(levelId));
    }

    /**
     * 创建会员等级
     */
    @PostMapping("/levels")
    public Response<MemberLevelVO> createLevel(@RequestBody MemberLevelVO levelVO) {
        return Response.buildSuccess(memberService.createMemberLevel(levelVO));
    }

    /**
     * 更新会员等级
     */
    @PutMapping("/levels/{levelId}")
    public Response<MemberLevelVO> updateLevel(@PathVariable Integer levelId, @RequestBody MemberLevelVO levelVO) {
        return Response.buildSuccess(memberService.updateMemberLevel(levelId, levelVO));
    }

    /**
     * 手动升级用户会员等级
     */
    @PostMapping("/upgrade/{userId}")
    public Response<MemberLevelVO> upgradeUserLevel(@PathVariable Integer userId, @RequestParam Integer targetLevelId) {
        return Response.buildSuccess(memberService.upgradeMemberLevel(userId, targetLevelId));
    }

    /**
     * 删除会员等级
     */
    @DeleteMapping("/levels/{levelId}")
    public Response<String> deleteLevel(@PathVariable Integer levelId) {
        memberService.deleteMemberLevel(levelId);
        return Response.buildSuccess("删除成功");
    }

    /**
     * 查看用户积分记录
     */
    @GetMapping("/points/{userId}")
    public Response<List<PointsRecordVO>> getUserPointsHistory(@PathVariable Integer userId) {
        return Response.buildSuccess(memberService.getUserPointsHistory(userId));
    }

    /**
     * 手动调整用户积分
     */
    @PostMapping("/points/adjust")
    public Response<PointsRecordVO> adjustUserPoints(@RequestBody PointsAdjustmentVO adjustment) {
        return Response.buildSuccess(memberService.addUserPoints(
            adjustment.getUserId(),
            adjustment.getPointsChange(),
            "ADMIN_ADJUSTMENT",
            null,
            adjustment.getReason()
        ));
    }
}
