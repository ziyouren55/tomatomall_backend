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
public class ReplyCreateVO {
    @NotNull(message = "帖子ID不能为空")
    private Integer postId;

    @NotBlank(message = "内容不能为空")
    @Size(min = 2, max = 5000, message = "内容长度必须在2-5000字之间")
    private String content;

    private Integer parentId; // 回复的是哪个回复，可以为null

}
