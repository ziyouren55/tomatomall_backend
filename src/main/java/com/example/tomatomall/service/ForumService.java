package com.example.tomatomall.service;

import com.example.tomatomall.vo.forum.ForumVO;

import java.util.List;

public interface ForumService {
    // 创建书籍论坛
    ForumVO createBookForum(Integer bookId);

    // 根据书籍ID获取论坛
    ForumVO getForumByBookId(Integer bookId);

    // 获取论坛详情
    ForumVO getForumById(Integer forumId);

    // 获取活跃论坛列表
    List<ForumVO> getActiveForums(int page, int size);

    List<ForumVO> getAllForums();

    // 分页获取活跃论坛
    org.springframework.data.domain.Page<ForumVO> getActiveForumsPage(int page, int size);

    // 分页获取全部论坛
    org.springframework.data.domain.Page<ForumVO> getForumsPage(int page, int size);

    // 按名称搜索（可选状态过滤）分页
    org.springframework.data.domain.Page<ForumVO> searchForums(String keyword, String status, int page, int size);


    /**
     * 检测热门书籍并为其创建论坛
     */
    void detectAndCreateForums();

    /**
     * 更新书籍销量并检查是否需要创建论坛
     * @param bookId 书籍ID
     * @param incrementCount 销量增加数量
     * @return 是否已创建论坛
     */
    boolean incrementSalesAndCheckForum(Integer bookId, Integer incrementCount);
}
