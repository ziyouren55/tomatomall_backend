package com.example.tomatomall.vo.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReplyVO {
    private Integer id;
    private Integer postId;
    private Integer userId;
    private String content;
    private Integer parentId;
    private Integer likeCount;
    private String status;
    private Date createTime;
    private Date updateTime;

    // 额外需要的字段
    private String username; // 回复用户名
    private String userAvatar; // 用户头像
    private List<ReplyVO> childReplies; // 子回复
    private Boolean isLiked; // 当前用户是否点赞
    private String parentUsername; // 被回复人用户名(如果是回复别人的回复)
}
