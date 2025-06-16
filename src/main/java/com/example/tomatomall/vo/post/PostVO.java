package com.example.tomatomall.vo.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostVO {
    private Integer id;
    private Integer forumId;
    private Integer userId;
    private String title;
    private String content;
    private List<String> imageUrls;
    private Integer viewCount;
    private Integer likeCount;
    private Integer replyCount;
    private Boolean isSticky;
    private Boolean isEssence;
    private String status;
    private Date lastReplyTime;
    private Date createTime;
    private Date updateTime;

    // 额外需要的字段
    private String username; // 发帖用户名
    private String forumName;
    private String userAvatar; // 用户头像
    private String sectionName; // 版块名称
    private Boolean isLiked; // 当前用户是否点赞
}
