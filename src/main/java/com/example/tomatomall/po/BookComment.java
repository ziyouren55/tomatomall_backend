package com.example.tomatomall.po;

import com.example.tomatomall.vo.bookComment.BookCommentVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "comment")

public class BookComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(name = "comment_text")
    private String comment_text;

    @JoinColumn(name = "product_id", nullable = false)
    @NotNull
    private Integer productId;

    @Column(name = "Name")
    @NotBlank
    private String name;

    public BookCommentVO toVO(){
        return new BookCommentVO(id,comment_text,productId,name);
    }
}
