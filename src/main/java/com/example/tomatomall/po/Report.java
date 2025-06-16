package com.example.tomatomall.po;

import com.example.tomatomall.vo.post.ReportVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "target_id", nullable = false)
    private Integer targetId;

    @Column(name = "target_type", nullable = false)
    private String targetType; // POST或REPLY

    @Column(name = "reason_type", nullable = false)
    private String reasonType; // SPAM, PORN, VIOLENCE, AD, OTHER

    @Column(name = "description")
    private String description;

    @Column(name = "status", nullable = false)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    @Column(name = "processed_by")
    private Integer processedBy;

    @Column(name = "processed_time")
    private Date processedTime;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    public ReportVO toVO() {
        ReportVO vo = new ReportVO();
        vo.setId(id);
        vo.setUserId(userId);
        vo.setTargetId(targetId);
        vo.setTargetType(targetType);
        vo.setReasonType(reasonType);
        vo.setDescription(description);
        vo.setStatus(status);
        vo.setProcessedBy(processedBy);
        vo.setProcessedTime(processedTime);
        vo.setCreateTime(createTime);
        return vo;
    }
}
