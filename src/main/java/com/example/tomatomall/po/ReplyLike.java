package com.example.tomatomall.po;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reply_like", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"reply_id", "user_id"})
})
public class ReplyLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reply_id", nullable = false)
    private Integer replyId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReplyId() {
        return replyId;
    }

    public void setReplyId(Integer replyId) {
        this.replyId = replyId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}

