package com.example.tomatomall.vo.bookComment;

import com.example.tomatomall.po.BookComment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookCommentVO {
    private Integer id;
    private String commentText;
    private Integer productId;
    private String name;
    private Integer userId;
    private Date createTime;


    public BookComment toPO(){
        BookComment comment = new BookComment();
        comment.setId(id);
        comment.setComment_text(commentText);
        comment.setProductId(productId);
        comment.setName(name);
        comment.setUserId(userId);
        comment.setCreateTime(createTime);
        return comment;
    }
}
