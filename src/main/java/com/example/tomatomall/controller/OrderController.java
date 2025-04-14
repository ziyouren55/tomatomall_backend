package com.example.tomatomall.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.example.tomatomall.po.Stockpile;
import com.example.tomatomall.repository.StockpileRepository;
import com.example.tomatomall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.tomatomall.vo.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    StockpileRepository inventoryService;

    private static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5/9JeqIOaR5IFinRHpi30RY2AGWcMpMtdUcmYzoNIwc9OEl3yk1hj+PjrO9PP1f8IWbdLBbwq7EfcTJGa1S+uOCqUO+5E74j67eSamG0K41HGI15wNAkUNYOyD/kbjuNBDEsdM3BjxvirryQ5PjbuJSqlekY2FXnfMo/Y4vqicj1w1KvMXimuvGqgSNfL4FvQKiOTc1Of9zqZMunrD7yibc9U3uUmuyvk+53c/hqwPxd5ngi9UyZ+Qk/iQRQ2duhIPzhFXEUMBR1OQbv9YI2k7/+/mJ/AHRnQ6T+DOWieDicQ3eioV4kyWH6ak8FOAcfIpP3xwPcxLCJ/4C/wJrExwIDAQAB";
    @Autowired
    OrderService orderService;

    @PostMapping("/{orderId}/pay")
    public Response payInit(@PathVariable Integer orderId){
        return Response.buildSuccess(orderService.payInit(orderId));
    }

    @PostMapping("/api/orders/notify")
    public void handleAlipayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException, AlipayApiException {
        // 1. 解析支付宝回调参数（通常是 application/x-www-form-urlencoded）
        Map<String, String> params = request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));

        // 2. 验证支付宝签名（防止伪造请求）
        boolean signVerified = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, "UTF-8", "RSA2");
        if (!signVerified) {
            response.getWriter().print("fail"); // 签名验证失败，返回 fail
            return;
        }

        // 3. 处理业务逻辑（更新订单、减库存等）
        String tradeStatus = params.get("trade_status");
        if ("TRADE_SUCCESS".equals(tradeStatus)) {
            String orderId = params.get("out_trade_no"); // 您的订单号
            String alipayTradeNo = params.get("trade_no"); // 支付宝交易号
            String amount = params.get("total_amount"); // 支付金额

            // 更新订单状态（注意幂等性，防止重复处理）
            orderService.updateOrderStatus(orderId, alipayTradeNo, amount);

            // 扣减库存（建议加锁或乐观锁）
            inventoryService.reduceStock(orderId);
        }

        // 4. 必须返回纯文本的 "success"（支付宝要求）
        response.getWriter().print("success");
    }
}
