package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Forum;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.repository.ForumRepository;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.service.ForumService;
import com.example.tomatomall.vo.forum.ForumVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ForumServiceImpl implements ForumService {

    @Autowired
    private ForumRepository forumRepository;

    @Autowired
    private ProductRepository productRepository;

    private Integer salesThreshold = 100;

    private ForumVO buildForumVO(Forum forum) {
        Product product = productRepository.findById(forum.getProduct().getId()).orElse(null);
        if (product == null) {
            return null;
        }
        ForumVO forumVO = forum.toVO();
        forumVO.setBookTitle(product.getTitle());
        forumVO.setBookCover(product.getCover());
        return forumVO;
    }

    @Override
    @Transactional
    public ForumVO createProductForum(Integer productId) {
        // 委托给 ensureProductForum，避免重复逻辑
        return ensureProductForum(productId);
    }

    @Override
    @Transactional
    public ForumVO ensureProductForum(Integer productId) {
        // 检查论坛是否已存在
        Optional<Forum> existingForum = forumRepository.findByProductId(productId);
        if (existingForum.isPresent()) {
            Forum forum = existingForum.get();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new TomatoMallException("书籍不存在"));

            ForumVO forumVO = forum.toVO();
            forumVO.setBookTitle(product.getTitle());
            forumVO.setBookCover(product.getCover());
            return forumVO;
        }

        // 获取书籍信息
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new TomatoMallException("书籍不存在"));

        // 创建论坛
        Forum forum = new Forum();
        forum.setProduct(product);
        forum.setName(product.getTitle() + "读者交流区");
        forum.setDescription("关于《" + product.getTitle() + "》的讨论区，欢迎分享阅读心得");
        forum.setPostCount(0);
        forum.setStatus("ACTIVE");
        forum.setCreateTime(new Date());
        forum.setUpdateTime(new Date());

        Forum savedForum = forumRepository.save(forum);

        ForumVO forumVO = savedForum.toVO();
        forumVO.setBookTitle(product.getTitle());
        forumVO.setBookCover(product.getCover());

        return forumVO;
    }


    @Override
    public ForumVO getForumByProductId(Integer productId) {
        Forum forum = forumRepository.findByProductId(productId)
                .orElseThrow(() -> new TomatoMallException("该书籍尚未创建论坛"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new TomatoMallException("书籍不存在"));

        ForumVO forumVO = forum.toVO();
        forumVO.setBookTitle(product.getTitle());
        forumVO.setBookCover(product.getCover());

        return forumVO;
    }

    @Override
    public ForumVO getForumById(Integer forumId) {
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new TomatoMallException("论坛不存在"));

        Product product = productRepository.findById(forum.getProduct().getId())
                .orElseThrow(() -> new TomatoMallException("书籍不存在"));

        ForumVO forumVO = forum.toVO();
        forumVO.setBookTitle(product.getTitle());
        forumVO.setBookCover(product.getCover());

        return forumVO;
    }


    @Override
    public List<ForumVO> getActiveForums(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "postCount", "updateTime"));
        List<ForumVO> result = new ArrayList<>();
        forumRepository.findByStatusAndPostCountGreaterThan("ACTIVE", 0, pageRequest).forEach(forum -> {
            ForumVO forumVO = buildForumVO(forum);
            if (forumVO != null) {
                result.add(forumVO);
            }
        });
        // 如果热门为空，退回到所有 ACTIVE
        if (result.isEmpty()) {
            forumRepository.findByStatus("ACTIVE", pageRequest).forEach(forum -> {
                ForumVO forumVO = buildForumVO(forum);
                if (forumVO != null) {
                    result.add(forumVO);
                }
            });
        }
        return result;
    }

    @Override
    public List<ForumVO> getAllForums()
    {
        List<Forum> forums = forumRepository.findAll();
        List<ForumVO> result = new ArrayList<>();
        for (Forum forum : forums) {
            ForumVO forumVO = buildForumVO(forum);
            if (forumVO != null) {
                result.add(forumVO);
            }
        }

        return result;
    }

    @Override
    public org.springframework.data.domain.Page<ForumVO> getActiveForumsPage(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "postCount", "updateTime"));
        org.springframework.data.domain.Page<ForumVO> pageData =
                forumRepository.findByStatusAndPostCountGreaterThan("ACTIVE", 0, pageRequest)
                        .map(this::buildForumVO)
                        .map(vo -> vo);
        if (pageData.getTotalElements() == 0) {
            // 回退：如果暂无发帖的论坛，使用全部 ACTIVE
            return forumRepository.findByStatus("ACTIVE", pageRequest)
                    .map(this::buildForumVO)
                    .map(vo -> vo);
        }
        return pageData;
    }

    @Override
    public org.springframework.data.domain.Page<ForumVO> getForumsPage(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updateTime"));
        return forumRepository.findAll(pageRequest)
                .map(this::buildForumVO)
                .map(vo -> vo);
    }

    @Override
    public org.springframework.data.domain.Page<ForumVO> searchForums(String keyword, String status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updateTime"));
        org.springframework.data.domain.Page<Forum> forums;
        if (status != null && !status.isEmpty()) {
            forums = forumRepository.findByNameContainingIgnoreCaseAndStatus(keyword, status, pageRequest);
        } else {
            forums = forumRepository.findByNameContainingIgnoreCase(keyword, pageRequest);
        }
        return forums.map(this::buildForumVO).map(vo -> vo);
    }

    /**
     * 每天凌晨2点执行一次检测
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Override
    public void detectAndCreateForums() {
        // 查找销量超过阈值但尚未创建论坛的书籍
        List<Product> hotProducts = productRepository.findBySalesCountGreaterThanAndIdNotIn(
                salesThreshold,
                forumRepository.findAllProductIds());

        // 为每本热门书籍创建论坛
        for (Product product : hotProducts) {
            createProductForum(product.getId());
        }
    }

    @Override
    public boolean incrementSalesAndCheckForum(Integer productId, Integer incrementCount)
    {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new TomatoMallException("书籍不存在"));

        // 更新销量
        int originSales = product.getSalesCount() == null ? 0 : product.getSalesCount();
        product.setSalesCount(originSales + (incrementCount == null ? 0 : incrementCount));
        productRepository.save(product);

        // 如果已存在论坛则直接返回
        Optional<Forum> existingForum = forumRepository.findByProductId(productId);
        if (existingForum.isPresent()) {
            return false;
        }

        // 达到阈值自动创建论坛
        if (product.getSalesCount() != null && product.getSalesCount() >= salesThreshold) {
            createProductForum(productId);
            return true;
        }
        return false;
    }

    @Override
    public boolean existsProductForum(Integer productId) {
        return forumRepository.findByProductId(productId).isPresent();
    }
}
