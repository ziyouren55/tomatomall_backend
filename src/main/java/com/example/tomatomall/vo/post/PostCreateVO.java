package com.example.tomatomall.vo.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostCreateVO {
    @NotNull(message = "版块ID不能为空")
    private Integer forumId;

    @NotBlank(message = "标题不能为空")
    @Size(min = 2, max = 100, message = "标题长度必须在2-100字之间")
    private String title;

    @NotBlank(message = "内容不能为空")
    @Size(min = 5, max = 10000, message = "内容长度必须在5-10000字之间")
    private String content;

    private List<String> imageUrlList; // 新增：Base64编码的图片列表

}
