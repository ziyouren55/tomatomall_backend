package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Stockpile;
import com.example.tomatomall.po.Store;
import com.example.tomatomall.repository.ProductRepository;
import com.example.tomatomall.repository.StockpileRepository;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.util.MyBeanUtil;
import com.example.tomatomall.util.UserContext;
import com.example.tomatomall.enums.UserRole;
import com.example.tomatomall.vo.PageResultVO;
import com.example.tomatomall.vo.products.ProductVO;
import com.example.tomatomall.vo.products.SearchResultVO;
import com.example.tomatomall.vo.products.RecommendProductVO;
import com.example.tomatomall.vo.products.RecommendSearchResultVO;
import com.example.tomatomall.vo.products.StockpileVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.example.tomatomall.repository.SchoolVerificationRepository;
import com.example.tomatomall.repository.SchoolRepository;
import com.example.tomatomall.po.SchoolVerification;
import com.example.tomatomall.po.School;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    StockpileRepository stockpileRepository;
    @Autowired
    com.example.tomatomall.repository.StoreRepository storeRepository;
    @Autowired
    private SchoolVerificationRepository schoolVerificationRepository;
    @Autowired
    private SchoolRepository schoolRepository;

    @Override
    public SearchResultVO getProductList(Integer page, Integer pageSize, String sortBy, String sortOrder) {
        // 参数验证和默认值
        if (page == null || page < 0) {
            page = 0;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 20;
        }
        if (pageSize > 100) {
            pageSize = 100; // 限制最大页面大小
        }

        // 构建排序
        Pageable pageable;
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

            Sort sort;
            switch (sortBy.toLowerCase()) {
                case "price":
                    sort = Sort.by(direction, "price");
                    break;
                case "salescount":
                case "sales_count":
                    sort = Sort.by(direction, "salesCount");
                    break;
                case "rate":
                    sort = Sort.by(direction, "rate");
                    break;
                default:
                    sort = Sort.by(Sort.Direction.DESC, "id"); // 默认按ID降序
            }
            pageable = PageRequest.of(page, pageSize, sort);
        } else {
            // 默认按ID降序
            pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        }

        // 使用分页查询（主站显示不基于当前登录用户过滤）
        Page<Product> productPage = productRepository.findAll(pageable);
        List<ProductVO> productVOs = productPage.getContent().stream()
            .map(Product::toVO)
            .collect(Collectors.toList());

        // 构建返回结果
        return new SearchResultVO(
            productVOs,
            productPage.getTotalElements(),
            page,
            pageSize
        );
    }

    @Override
    public SearchResultVO getManageProductList(Integer page, Integer pageSize, String sortBy, String sortOrder) {
        // 管理端列表：如果当前用户为商家则只返回其商品；管理员返回所有
        if (page == null || page < 0) page = 0;
        if (pageSize == null || pageSize <= 0) pageSize = 20;
        if (pageSize > 100) pageSize = 100;

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Integer currentUserId = UserContext.getCurrentUserId();
        UserRole currentRole = UserContext.getCurrentUserRole();
        Page<Product> productPage;
        if (currentRole == UserRole.MERCHANT && currentUserId != null) {
            productPage = productRepository.findByStore_MerchantId(currentUserId, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        List<ProductVO> productVOs = productPage.getContent().stream().map(Product::toVO).collect(Collectors.toList());
        return new SearchResultVO(productVOs, productPage.getTotalElements(), page, pageSize);
    }

    @Override
    public ProductVO getProduct(Integer id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent())
            return product.get().toVO();
        else
            throw TomatoMallException.productNotFind();
    }

    @Override
    public SearchResultVO getProductsByStore(Integer storeId, Integer page, Integer pageSize) {
        // 参数验证和默认值
        if (page == null || page < 0) page = 0;
        if (pageSize == null || pageSize <= 0) pageSize = 20;
        if (pageSize > 100) pageSize = 100;

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<Product> productPage = productRepository.findByStoreId(storeId, pageable);
        List<ProductVO> productVOs = productPage.getContent().stream()
            .map(Product::toVO)
            .collect(Collectors.toList());

        return new SearchResultVO(productVOs, productPage.getTotalElements(), page, pageSize);
    }

    @Override
    public String updateProduct(ProductVO productVO) {
        Optional<Product> productOpt = productRepository.findById(productVO.getId());
        if (productOpt.isEmpty()) {
            throw TomatoMallException.productNotFind();
        }
        Product product = productOpt.get();
        Integer currentUserId = UserContext.getCurrentUserId();
        UserRole currentRole = UserContext.getCurrentUserRole();
        if (currentRole == UserRole.MERCHANT) {
            Integer storeId = product.getStoreId();
            if (storeId == null) throw TomatoMallException.permissionDenied();
            Store store = storeRepository.findById(storeId).orElseThrow(() -> TomatoMallException.storeNotFind());
            if (!store.getMerchantId().equals(currentUserId)) throw TomatoMallException.permissionDenied();
            // 商家不能更改商品归属
            productVO.setStoreId(null);
        }
        BeanUtils.copyProperties(productVO, product, MyBeanUtil.getNullPropertyNames(productVO));
        productRepository.save(product);
        return "更新成功";
    }

    @Override
    @Transactional
    public ProductVO createProduct(ProductVO productVO) {
        Optional<Product> product = productRepository.findByTitle(productVO.getTitle());
        if (product.isPresent()) {
            throw TomatoMallException.productAlreadyExists();
        }
        // 如果当前用户为商家，自动设置 merchantId
        Integer currentUserId = UserContext.getCurrentUserId();
        UserRole currentRole = UserContext.getCurrentUserRole();
        if (currentRole == UserRole.MERCHANT && currentUserId != null) {
            // 如果商家创建商品且未指定 storeId，尝试找到其唯一店铺或抛出异常
            if (productVO.getStoreId() == null) {
                java.util.List<Store> stores = storeRepository.findByMerchantId(currentUserId);
                if (stores.size() == 1) {
                    productVO.setStoreId(stores.get(0).getId());
                } else {
                    throw new RuntimeException("商家有多个店铺，请在请求中指定 storeId");
                }
            } else {
                // 校验指定 storeId 是否属于当前商家
                Store s = storeRepository.findById(productVO.getStoreId()).orElseThrow(() -> TomatoMallException.storeNotFind());
                if (!s.getMerchantId().equals(currentUserId)) throw TomatoMallException.permissionDenied();
            }
        }
        Product newProduct = productVO.toPO();
        productRepository.save(newProduct);

        // 修复：使用保存后生成的newProduct.getId()，而不是productVO.getId()
        Optional<Stockpile> stockpile = stockpileRepository.findByProduct_Id(newProduct.getId());
        if (stockpile.isPresent()) {
            // 如果库存已存在，增加库存
            stockpile.get().setAmount(stockpile.get().getAmount() + 1);
            stockpileRepository.save(stockpile.get());
        } else {
            // 创建新的库存记录，使用JPA关联关系
            Stockpile newStockpile = new Stockpile();
            newStockpile.setProduct(newProduct);
            newStockpile.setAmount(1); // 默认库存为1，可以根据需要调整
            newStockpile.setFrozen(0);
            stockpileRepository.save(newStockpile);
        }

        return newProduct.toVO();
    }

    @Override
    @Transactional
    public String deleteProduct(Integer id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) throw TomatoMallException.productNotFind();
        Product product = productOpt.get();
        Integer currentUserId = UserContext.getCurrentUserId();
        UserRole currentRole = UserContext.getCurrentUserRole();
        if (currentRole == UserRole.MERCHANT) {
            Integer storeId = product.getStoreId();
            if (storeId == null) throw TomatoMallException.permissionDenied();
            Store store = storeRepository.findById(storeId).orElseThrow(() -> TomatoMallException.storeNotFind());
            if (!store.getMerchantId().equals(currentUserId)) throw TomatoMallException.permissionDenied();
        }
        Optional<Stockpile> stockpile = stockpileRepository.findByProduct_Id(id);
        if (stockpile.isPresent()) {
            stockpileRepository.delete(stockpile.get());
        }
        productRepository.deleteById(id);
        return "删除成功";
    }

    @Override
    @Transactional
    public String updateProductStockpile(Integer productId, Integer amount) {
        // 检查商品是否存在并做权限校验（商家只能修改自己的商品库存）
        Optional<Product> productOpt = productRepository.findById(productId);
        if (!productOpt.isPresent()) {
            throw TomatoMallException.productNotFind();
        }
        Product product = productOpt.get();
        Integer currentUserId = UserContext.getCurrentUserId();
        UserRole currentRole = UserContext.getCurrentUserRole();
        if (currentRole == UserRole.MERCHANT) {
            Integer storeId = product.getStoreId();
            if (storeId == null) throw TomatoMallException.permissionDenied();
            Store store = storeRepository.findById(storeId).orElseThrow(() -> TomatoMallException.storeNotFind());
            if (!store.getMerchantId().equals(currentUserId)) throw TomatoMallException.permissionDenied();
        }
        Optional<Stockpile> stockpile = stockpileRepository.findByProduct_Id(productId);
        if (!stockpile.isPresent()) {
            Stockpile newStockpile = new Stockpile();
            newStockpile.setProduct(product);
            newStockpile.setAmount(amount);
            newStockpile.setFrozen(0);
            stockpileRepository.save(newStockpile);
        } else {
            stockpile.get().setAmount(amount);
            stockpileRepository.save(stockpile.get());
        }
        return "调整库存成功";
    }

    @Override
    public StockpileVO getProductStockpile(Integer productId) {
        Optional<Stockpile> stockpile = stockpileRepository.findByProduct_Id(productId);
        if (stockpile.isPresent()) {
            return stockpile.get().toVO();
        } else {
            // 如果库存不存在，返回默认库存而不是抛出异常
            StockpileVO defaultStockpile = new StockpileVO();
            defaultStockpile.setProductId(productId);
            defaultStockpile.setAmount(0);
            defaultStockpile.setFrozen(0);
            // 尝试获取商品名称
            Optional<Product> product = productRepository.findById(productId);
            if (product.isPresent()) {
                defaultStockpile.setProductName(product.get().getTitle());
            }
            return defaultStockpile;
        }
    }

    @Override
    public PageResultVO<StockpileVO> getAllStockpile(Integer page, Integer pageSize) {
        // 参数验证和默认值
        if (page == null || page < 0) {
            page = 0;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 20;
        }
        if (pageSize > 100) {
            pageSize = 100; // 限制最大页面大小
        }

        // 使用分页查询库存
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Integer currentUserId = UserContext.getCurrentUserId();
        UserRole currentRole = UserContext.getCurrentUserRole();
        Page<Stockpile> stockpilePage;
        if (currentRole != null && currentRole == UserRole.MERCHANT && currentUserId != null) {
            stockpilePage = stockpileRepository.findByProduct_Store_MerchantId(currentUserId, pageable);
        } else {
            stockpilePage = stockpileRepository.findAll(pageable);
        }

        List<StockpileVO> stockpileVOs = stockpilePage.getContent().stream()
            .map(Stockpile::toVO)
            .collect(Collectors.toList());

        // 构建返回结果
        return new PageResultVO<>(
            stockpileVOs,
            stockpilePage.getTotalElements(),
            page,
            pageSize
        );
    }

    @Override
    public RecommendSearchResultVO getNearbyRecommendations(Integer page, Integer pageSize) {
        // 参数验证和默认值（与其它方法风格一致）
        if (page == null || page < 0) page = 0;
        if (pageSize == null || pageSize <= 0) pageSize = 20;
        if (pageSize > 100) pageSize = 100;

        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) {
            throw com.example.tomatomall.exception.TomatoMallException.notLogin();
        }

        // 检查用户的学校认证
        Optional<SchoolVerification> mySvOpt = schoolVerificationRepository.findByUserId(currentUserId);
        if (mySvOpt.isEmpty() || mySvOpt.get().getStatus() == null || !"VERIFIED".equalsIgnoreCase(mySvOpt.get().getStatus())) {
            throw TomatoMallException.schoolNotVerified();
        }
        String mySchoolName = mySvOpt.get().getSchoolName();

        // 试图从学校表推导城市信息（取第一个匹配项）
        String myCityCode = null;
        try {
            org.springframework.data.domain.Page<School> schPage = schoolRepository.findByNameContainingIgnoreCase(mySchoolName, PageRequest.of(0, 1));
            if (schPage != null && schPage.hasContent()) {
                School s = schPage.getContent().get(0);
                myCityCode = s.getCityCode();
            }
        } catch (Exception ignored) {
        }

        // 收集同校和同城的商家ID
        List<SchoolVerification> allSv = schoolVerificationRepository.findAll();
        List<Integer> sameSchoolMerchantIds = allSv.stream()
                .filter(sv -> sv.getStatus() != null && "VERIFIED".equalsIgnoreCase(sv.getStatus())
                        && sv.getSchoolName() != null
                        && sv.getSchoolName().toLowerCase().contains(mySchoolName.toLowerCase()))
                .map(SchoolVerification::getUserId)
                .distinct()
                .collect(Collectors.toList());
        final List<Integer> sameSchoolMerchantIdsFinal = (sameSchoolMerchantIds == null) ? java.util.Collections.emptyList() : sameSchoolMerchantIds;

        List<Integer> sameCityMerchantIds = new java.util.ArrayList<>();
        if (myCityCode != null) {
            for (SchoolVerification sv : allSv) {
                if (sv.getStatus() == null || !"VERIFIED".equalsIgnoreCase(sv.getStatus()) || sv.getSchoolName() == null) continue;
                try {
                    org.springframework.data.domain.Page<School> sp = schoolRepository.findByNameContainingIgnoreCase(sv.getSchoolName(), PageRequest.of(0, 1));
                    if (sp != null && sp.hasContent()) {
                        School sc = sp.getContent().get(0);
                        if (myCityCode.equalsIgnoreCase(sc.getCityCode())) {
                            sameCityMerchantIds.add(sv.getUserId());
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }

        // 确保同校商家优先，若不足再补同城，再补其他
        java.util.List<RecommendProductVO> resultProducts = new java.util.ArrayList<>();
        int remaining = pageSize;

        // Pageable not needed for custom prioritized queries

        // 同校（优先）
        if (!sameSchoolMerchantIdsFinal.isEmpty() && remaining > 0) {
            Page<Product> p = productRepository.findByStore_MerchantIdIn(sameSchoolMerchantIdsFinal, PageRequest.of(0, remaining, Sort.by(Sort.Direction.DESC, "id")));
            List<ProductVO> vos = p.getContent().stream().map(Product::toVO).collect(Collectors.toList());
            for (ProductVO vo : vos) {
                if (remaining <= 0) break;
                RecommendProductVO r = new RecommendProductVO();
                r.setProduct(vo);
                r.setPriority(com.example.tomatomall.enums.RecommendationPriority.SAME_SCHOOL);
                resultProducts.add(r);
                remaining--;
            }
        }

        // 同城（排除已包含的商家）
        if (remaining > 0 && sameCityMerchantIds != null && !sameCityMerchantIds.isEmpty()) {
            List<Integer> cityOnly = new java.util.ArrayList<>();
            for (Integer id : sameCityMerchantIds) {
                if (!sameSchoolMerchantIdsFinal.contains(id) && !cityOnly.contains(id)) {
                    cityOnly.add(id);
                }
            }
            if (!cityOnly.isEmpty()) {
                Page<Product> p2 = productRepository.findByStore_MerchantIdIn(cityOnly, PageRequest.of(0, remaining, Sort.by(Sort.Direction.DESC, "id")));
                List<ProductVO> vos2 = p2.getContent().stream().map(Product::toVO).collect(Collectors.toList());
                for (ProductVO vo : vos2) {
                    if (remaining <= 0) break;
                    // 防止重复
                    boolean exists = resultProducts.stream().anyMatch(rp -> rp.getProduct().getId().equals(vo.getId()));
                    if (!exists) {
                        RecommendProductVO r = new RecommendProductVO();
                        r.setProduct(vo);
                        r.setPriority(com.example.tomatomall.enums.RecommendationPriority.SAME_CITY);
                        resultProducts.add(r);
                        remaining--;
                    }
                }
            }
        }

        // 其他商品补齐（不限制商家）
        if (remaining > 0) {
            Page<Product> others = productRepository.findAll(PageRequest.of(0, remaining, Sort.by(Sort.Direction.DESC, "id")));
            List<ProductVO> vos = others.getContent().stream().map(Product::toVO).collect(Collectors.toList());
            for (ProductVO vo : vos) {
                if (remaining <= 0) break;
                // 简单去重：避免加入重复 productId
                boolean exists = resultProducts.stream().anyMatch(rp -> rp.getProduct().getId().equals(vo.getId()));
                if (!exists) {
                    RecommendProductVO r = new RecommendProductVO();
                    r.setProduct(vo);
                    r.setPriority(com.example.tomatomall.enums.RecommendationPriority.OTHER);
                    resultProducts.add(r);
                    remaining--;
                }
            }
        }

        // 返回构建：注意 total 值这里仅为 resultProducts.size()
        return new RecommendSearchResultVO(resultProducts, (long) resultProducts.size(), page, pageSize);
    }

    @Override
    public SearchResultVO searchProducts(String keyword, Integer page, Integer pageSize, String sortBy, String sortOrder) {
        // 参数验证和默认值
        if (keyword == null || keyword.trim().isEmpty()) {
            // 如果关键词为空，返回空结果
            return new SearchResultVO(java.util.Collections.emptyList(), 0L, page, pageSize);
        }

        // 设置默认值
        if (page == null || page < 0) {
            page = 0;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 20;
        }

        // 关键词预处理：去除多余空格，分割关键词
        String processedKeyword = keyword.trim().replaceAll("\\s+", " ");
        String[] keywords = processedKeyword.split("\\s+");

        // 执行搜索：支持多关键词（AND逻辑，所有关键词都要匹配）
        List<Product> allMatchedProducts = null;

        if (keywords.length == 1) {
            // 单关键词搜索
            allMatchedProducts = productRepository.findProductsByKeyword(keywords[0].toLowerCase());
        } else {
            // 多关键词搜索：先搜索第一个关键词，然后过滤出包含所有关键词的商品
            List<Product> firstKeywordResults = productRepository.findProductsByKeyword(keywords[0].toLowerCase());
            allMatchedProducts = firstKeywordResults.stream()
                .filter(product -> {
                    // 检查商品是否包含所有关键词
                    String title = (product.getTitle() != null ? product.getTitle().toLowerCase() : "");
                    String description = (product.getDescription() != null ? product.getDescription().toLowerCase() : "");
                    String detail = (product.getDetail() != null ? product.getDetail().toLowerCase() : "");
                    String allText = title + " " + description + " " + detail;

                    // 所有关键词都要在文本中出现
                    for (int i = 1; i < keywords.length; i++) {
                        if (!allText.contains(keywords[i].toLowerCase())) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
        }

        // 根据角色过滤：商家只能搜索到自己的商品
        Integer currentUserId = UserContext.getCurrentUserId();
        UserRole currentRole = UserContext.getCurrentUserRole();
        if (currentRole == UserRole.MERCHANT && currentUserId != null) {
            // 获取该商家的所有店铺ID，过滤出属于这些店铺的商品
            List<Store> stores = storeRepository.findByMerchantId(currentUserId);
            java.util.Set<Integer> storeIds = stores.stream().map(s -> s.getId()).collect(Collectors.toSet());
            allMatchedProducts = allMatchedProducts.stream()
                .filter(p -> p.getStoreId() != null && storeIds.contains(p.getStoreId()))
                .collect(Collectors.toList());
        }

        // 相关性排序：标题匹配优先于描述匹配
        List<Product> sortedProducts = allMatchedProducts.stream()
            .sorted((p1, p2) -> {
                String title1 = (p1.getTitle() != null ? p1.getTitle().toLowerCase() : "");
                String title2 = (p2.getTitle() != null ? p2.getTitle().toLowerCase() : "");
                String desc1 = (p1.getDescription() != null ? p1.getDescription().toLowerCase() : "");
                String desc2 = (p2.getDescription() != null ? p2.getDescription().toLowerCase() : "");

                // 计算相关性分数
                int score1 = calculateRelevanceScore(title1, desc1, keywords);
                int score2 = calculateRelevanceScore(title2, desc2, keywords);

                // 先按相关性排序（降序），再按ID排序（降序）
                if (score1 != score2) {
                    return Integer.compare(score2, score1);
                }
                return Integer.compare(p2.getId(), p1.getId());
            })
            .collect(Collectors.toList());

        // 应用用户指定的排序
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

            switch (sortBy.toLowerCase()) {
                case "price":
                    sortedProducts.sort((p1, p2) -> {
                        int cmp = Double.compare(p1.getPrice(), p2.getPrice());
                        return direction == Sort.Direction.ASC ? cmp : -cmp;
                    });
                    break;
                case "salescount":
                case "sales_count":
                    sortedProducts.sort((p1, p2) -> {
                        int cmp = Integer.compare(
                            p1.getSalesCount() != null ? p1.getSalesCount() : 0,
                            p2.getSalesCount() != null ? p2.getSalesCount() : 0
                        );
                        return direction == Sort.Direction.ASC ? cmp : -cmp;
                    });
                    break;
                case "rate":
                    sortedProducts.sort((p1, p2) -> {
                        int cmp = Float.compare(
                            p1.getRate() != null ? p1.getRate() : 0f,
                            p2.getRate() != null ? p2.getRate() : 0f
                        );
                        return direction == Sort.Direction.ASC ? cmp : -cmp;
                    });
                    break;
            }
        }

        // 手动分页
        int total = sortedProducts.size();
        int start = page * pageSize;
        int end = Math.min(start + pageSize, total);
        List<Product> pagedProducts = start < total ? sortedProducts.subList(start, end) : java.util.Collections.emptyList();

        // 转换为VO列表
        List<ProductVO> productVOs = pagedProducts.stream()
            .map(Product::toVO)
            .collect(Collectors.toList());

        // 构建搜索结果
        return new SearchResultVO(
            productVOs,
            (long) total,
            page,
            pageSize
        );
    }

    /**
     * 计算商品的相关性分数
     * 标题匹配权重更高，描述匹配权重较低
     */
    private int calculateRelevanceScore(String title, String description, String[] keywords) {
        int score = 0;
        for (String keyword : keywords) {
            String lowerKeyword = keyword.toLowerCase();
            // 标题匹配：权重10
            if (title.contains(lowerKeyword)) {
                score += 10;
                // 标题开头匹配：额外权重5
                if (title.startsWith(lowerKeyword)) {
                    score += 5;
                }
            }
            // 描述匹配：权重3
            if (description.contains(lowerKeyword)) {
                score += 3;
            }
        }
        return score;
    }

    @Override
    public List<ProductVO> getProductsByMerchantId(Integer merchantId) {
        // 参数验证
        if (merchantId == null) {
            throw new IllegalArgumentException("商家ID不能为空");
        }

        // 获取商家所有店铺
        List<Store> stores = storeRepository.findByMerchantId(merchantId);
        if (stores.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        // 获取店铺ID列表
        List<Integer> storeIds = stores.stream()
                .map(Store::getId)
                .collect(Collectors.toList());

        // 获取所有店铺的商品，按ID降序排列（保持与其他查询一致）
        List<Product> products = productRepository.findByStoreIdIn(storeIds);
        products.sort((p1, p2) -> Integer.compare(p2.getId(), p1.getId())); // 降序排列

        // 转换为VO
        return products.stream()
                .map(Product::toVO)
                .collect(Collectors.toList());
    }
}
