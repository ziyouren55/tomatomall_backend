package com.example.tomatomall.po;

import com.example.tomatomall.vo.forum.ForumVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "forums")
@Getter
@Setter
@NoArgsConstructor
public class Forum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "book_id", nullable = false)
    private Integer bookId;

    @Column(name = "description")
    private String description;

    @Column(name = "post_count", nullable = false)
    private Integer postCount = 0;

    @Column(name = "status", nullable = false)
    private String status = "ACTIVE"; // ACTIVE, DISABLED

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    public ForumVO toVO() {
        ForumVO vo = new ForumVO();
        vo.setId(id);
        vo.setName(name);
        vo.setBookId(bookId);
        vo.setDescription(description);
        vo.setPostCount(postCount);
        vo.setStatus(status);
        vo.setCreateTime(createTime);
        vo.setUpdateTime(updateTime);
        return vo;
    }
}
