package com.example.tomatomall.controller;

import com.example.tomatomall.service.ForumService;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.forum.ForumVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forums")
public class ForumController {

    @Autowired
    private ForumService forumService;

    /**
     * 获取书籍的论坛
     */
    @GetMapping("/book/{bookId}")
    public Response getForumByBook(@PathVariable Integer bookId) {
        ForumVO forum = forumService.getForumByBookId(bookId);
        return Response.buildSuccess(forum);
    }

    /**
     * 获取论坛详情
     */
    @GetMapping("/{forumId}")
    public Response getForumDetail(@PathVariable Integer forumId) {
        ForumVO forum = forumService.getForumById(forumId);
        return Response.buildSuccess(forum);
    }


    /**
     * 获取活跃论坛列表
     */
    @GetMapping("/active")
    public Response getActiveForums(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        List<ForumVO> forums = forumService.getActiveForums(page, size);
        return Response.buildSuccess(forums);
    }

    /**
     * 获取论坛列表
     */
    @GetMapping()
    public Response getAllForums() {
        List<ForumVO> forums = forumService.getAllForums();
        return Response.buildSuccess(forums);
    }
}
