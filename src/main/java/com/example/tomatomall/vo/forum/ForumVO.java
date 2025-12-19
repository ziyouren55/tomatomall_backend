package com.example.tomatomall.vo.forum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ForumVO {
    private Integer id;
    private String name;
    private Integer productId;
    private String description;
    private Integer postCount;
    private String status;
    private Date createTime;
    private Date updateTime;

    // 额外需要的字段
    private String bookTitle; // 书籍标题
    private String bookCover; // 书籍封面
}
