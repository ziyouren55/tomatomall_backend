package com.example.tomatomall.vo.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class ReportCreateVO {
    @NotNull(message = "目标ID不能为空")
    private Integer targetId;

    @NotBlank(message = "目标类型不能为空")
    private String targetType; // POST或REPLY

    @NotBlank(message = "举报原因不能为空")
    private String reasonType;

    @Size(max = 500, message = "描述不能超过500字")
    private String description;
}
