package com.example.tomatomall.po;

import com.example.tomatomall.vo.post.PostVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 删除sectionId字段，改为直接关联forumId
    @Column(name = "forum_id", nullable = false)
    private Integer forumId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // 新增：图片URLs，使用JSON字符串存储多张图片
    @Column(name = "image_urls", columnDefinition = "TEXT")
    private String imageUrls;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Column(name = "reply_count", nullable = false)
    private Integer replyCount = 0;

    @Column(name = "is_sticky", nullable = false)
    private Boolean isSticky = false;

    @Column(name = "is_essence", nullable = false)
    private Boolean isEssence = false;

    @Column(name = "status", nullable = false)
    private String status = "NORMAL"; // NORMAL, HIDDEN, DELETED

    @Column(name = "last_reply_time")
    private Date lastReplyTime;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @Transient // 非持久化字段，仅用于业务逻辑
    private List<String> imageUrlList;

    // 获取图片URL列表
    public List<String> getImageUrlList() {
        if (imageUrlList == null) {
            imageUrlList = new ArrayList<>();
            if (imageUrls != null && !imageUrls.isEmpty()) {
                // 简单分割字符串实现，实际可使用JSON库处理
                String[] urls = imageUrls.split(",");
                for (String url : urls) {
                    if (!url.trim().isEmpty()) {
                        imageUrlList.add(url.trim());
                    }
                }
            }
        }
        return imageUrlList;
    }

    // 设置图片URL列表
    public void setImageUrls(List<String> imageUrlList) {
        this.imageUrlList = imageUrlList;
        if (imageUrlList != null && !imageUrlList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < imageUrlList.size(); i++) {
                sb.append(imageUrlList.get(i));
                if (i < imageUrlList.size() - 1) {
                    sb.append(",");
                }
            }
            this.imageUrls = sb.toString();
        }
    }

    public PostVO toVO() {
        PostVO vo = new PostVO();
        vo.setId(id);
        vo.setUserId(userId);
        vo.setTitle(title);
        vo.setContent(content);
        vo.setImageUrls(getImageUrlList()); // 添加图片列表
        vo.setViewCount(viewCount);
        vo.setLikeCount(likeCount);
        vo.setReplyCount(replyCount);
        vo.setIsSticky(isSticky);
        vo.setIsEssence(isEssence);
        vo.setStatus(status);
        vo.setLastReplyTime(lastReplyTime);
        vo.setCreateTime(createTime);
        vo.setUpdateTime(updateTime);
        return vo;
    }
}
