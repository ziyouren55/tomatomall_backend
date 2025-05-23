package com.example.tomatomall.vo.products;

import com.example.tomatomall.po.BookComment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.print.Book;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookCommentVO {
    private Integer id;
    private String commentText;
    private Integer productId;
    private String name;


    public BookComment toPO(){
        return new BookComment(id,commentText,productId,name);
    }
}
