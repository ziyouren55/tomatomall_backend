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

    @Override
    @Transactional
    public ForumVO createBookForum(Integer bookId) {
        // 检查论坛是否已存在
        Optional<Forum> existingForum = forumRepository.findByBookId(bookId);
        if (existingForum.isPresent()) {
            return existingForum.get().toVO();
        }

        // 获取书籍信息
        Product product = productRepository.findById(bookId)
                .orElseThrow(() -> new TomatoMallException("书籍不存在"));

        // 创建论坛
        Forum forum = new Forum();
        forum.setBookId(bookId);
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
    public ForumVO getForumByBookId(Integer bookId) {
        Forum forum = forumRepository.findByBookId(bookId)
                .orElseThrow(() -> new TomatoMallException("该书籍尚未创建论坛"));

        Product product = productRepository.findById(bookId)
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

        Product product = productRepository.findById(forum.getBookId())
                .orElseThrow(() -> new TomatoMallException("书籍不存在"));

        ForumVO forumVO = forum.toVO();
        forumVO.setBookTitle(product.getTitle());
        forumVO.setBookCover(product.getCover());

        return forumVO;
    }


    @Override
    public List<ForumVO> getActiveForums(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "postCount"));
        List<Forum> forums = forumRepository.findByStatus("ACTIVE");

        List<ForumVO> result = new ArrayList<>();
        for (Forum forum : forums) {
            try {
                Product product = productRepository.findById(forum.getBookId()).orElse(null);
                if (product != null) {
                    ForumVO forumVO = forum.toVO();
                    forumVO.setBookTitle(product.getTitle());
                    forumVO.setBookCover(product.getCover());
                    result.add(forumVO);
                }
            } catch (Exception e) {
                // 如果获取书籍信息失败，继续处理下一个论坛
                continue;
            }
        }

        return result;
    }

    @Override
    public List<ForumVO> getAllForums()
    {
        List<Forum> forums = forumRepository.findAll();
        List<ForumVO> result = new ArrayList<>();
        for (Forum forum : forums) {
            try {
                Product product = productRepository.findById(forum.getBookId()).orElse(null);
                if (product != null) {
                    ForumVO forumVO = forum.toVO();
                    forumVO.setBookTitle(product.getTitle());
                    forumVO.setBookCover(product.getCover());
                    result.add(forumVO);
                }
            } catch (Exception e) {
                // 如果获取书籍信息失败，继续处理下一个论坛
                continue;
            }
        }

        return result;
    }

    /**
     * 每天凌晨2点执行一次检测
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Override
    public void detectAndCreateForums() {
        // 查找销量超过阈值但尚未创建论坛的书籍
        List<Product> hotBooks = productRepository.findBySalesCountGreaterThanAndIdNotIn(
                salesThreshold,
                forumRepository.findAllBookIds());

        // 为每本热门书籍创建论坛
        for (Product book : hotBooks) {
            createBookForum(book.getId());
        }
    }

    @Override
    public boolean incrementSalesAndCheckForum(Integer bookId, Integer incrementCount)
    {
        return false;
    }
}
