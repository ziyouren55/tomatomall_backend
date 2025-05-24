package com.example.tomatomall.controller;

import com.example.tomatomall.service.MemberService;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.member.MemberLevelVO;
import com.example.tomatomall.vo.member.MemberPointsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    /**
     * 获取用户会员信息
     */
    @GetMapping("/info")
    public Response getMemberInfo(@RequestAttribute("userId") Integer userId) {
        MemberPointsVO pointsInfo = memberService.getUserPoints(userId);
        MemberLevelVO levelInfo = memberService.getMemberLevelByUserId(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("points", pointsInfo);
        result.put("level", levelInfo);

        return Response.buildSuccess(result);
    }

    /**
     * 获取用户当前会员等级
     */
    @GetMapping("/level")
    public Response getMemberLevel(@RequestAttribute("userId") Integer userId) {
        return Response.buildSuccess(memberService.getMemberLevelByUserId(userId));
    }

    /**
     * 获取所有会员等级列表
     */
    @GetMapping("/levels")
    public Response getAllLevels() {
        return Response.buildSuccess(memberService.getAllMemberLevels());
    }

    /**
     * 获取用户当前积分
     */
    @GetMapping("/points")
    public Response getUserPoints(@RequestAttribute("userId") Integer userId) {
        return Response.buildSuccess(memberService.getUserPoints(userId));
    }

    /**
     * 获取用户积分历史记录
     */
    @GetMapping("/points/history")
    public Response getPointsHistory(@RequestAttribute("userId") Integer userId) {
        return Response.buildSuccess(memberService.getUserPointsHistory(userId));
    }
}
