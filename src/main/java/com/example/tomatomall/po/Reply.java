package com.example.tomatomall.po;

import com.example.tomatomall.vo.post.ReplyVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "replies")
@Getter
@Setter
@NoArgsConstructor
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "post_id", nullable = false)
    private Integer postId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "parent_id")
    private Integer parentId; // 回复的是哪个回复，如果为null则是回复帖子

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Column(name = "status", nullable = false)
    private String status = "NORMAL"; // NORMAL, HIDDEN, DELETED

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    public ReplyVO toVO() {
        ReplyVO vo = new ReplyVO();
        vo.setId(id);
        vo.setPostId(postId);
        vo.setUserId(userId);
        vo.setContent(content);
        vo.setParentId(parentId);
        vo.setLikeCount(likeCount);
        vo.setStatus(status);
        vo.setCreateTime(createTime);
        vo.setUpdateTime(updateTime);
        return vo;
    }
}
