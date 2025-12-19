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
    //todo 将book相关的命名重新改为product，需前后端一致
    @GetMapping("/book/{bookId}")
    public Response<ForumVO> getForumByProductId(@PathVariable Integer bookId) {
        ForumVO forum = forumService.getForumByProductId(bookId);
        return Response.buildSuccess(forum);
    }

    /**
     * 获取论坛详情
     */
    @GetMapping("/{forumId}")
    public Response<ForumVO> getForumDetail(@PathVariable Integer forumId) {
        ForumVO forum = forumService.getForumById(forumId);
        return Response.buildSuccess(forum);
    }


    /**
     * 获取活跃论坛列表
     */
    @GetMapping("/active")
    public Response<List<ForumVO>> getActiveForums(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        List<ForumVO> forums = forumService.getActiveForums(page, size);
        return Response.buildSuccess(forums);
    }

    /**
     * 分页获取活跃论坛
     */
    @GetMapping("/active/page")
    public Response<org.springframework.data.domain.Page<ForumVO>> getActiveForumsPage(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return Response.buildSuccess(forumService.getActiveForumsPage(page, size));
    }

    /**
     * 获取论坛列表
     */
    @GetMapping()
    public Response<List<ForumVO>> getAllForums() {
        List<ForumVO> forums = forumService.getAllForums();
        return Response.buildSuccess(forums);
    }

    /**
     * 分页获取全部论坛
     */
    @GetMapping("/page")
    public Response<org.springframework.data.domain.Page<ForumVO>> getForumsPage(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        return Response.buildSuccess(forumService.getForumsPage(page, size));
    }

    /**
     * 按名称搜索论坛（可选状态），分页
     */
    @GetMapping("/search")
    public Response<org.springframework.data.domain.Page<ForumVO>> searchForums(@RequestParam String keyword,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String status) {
        return Response.buildSuccess(forumService.searchForums(keyword, status, page, size));
    }
}
