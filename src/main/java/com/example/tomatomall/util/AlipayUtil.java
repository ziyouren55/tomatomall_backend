package com.example.tomatomall.util;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AlipayUtil {
    @Value("${alipay.app-id}")
    private String appId;
    @Value("${alipay.private-key}")
    private String privateKey;
    @Value("${alipay.alipay-public-key}")
    private String alipayPublicKey;
    @Value("${alipay.server-url}")
    private String serverUrl;
    @Value("${alipay.charset}")
    private String charset;
    @Value("${alipay.sign-type}")
    private String signType;
    @Value("${alipay.notify-url}")
    private String notifyUrl;
    @Value("${alipay.return-url}")
    private String returnUrl;
    private static final String FORMAT = "JSON";

    /**
     * 生成支付宝支付表单HTML
     */
    public String generatePayForm(String outTradeNo, String totalAmount, String subject) throws AlipayApiException
    {
        AlipayClient alipayClient = new DefaultAlipayClient(
            serverUrl, appId, privateKey, FORMAT, charset, alipayPublicKey, signType
        );
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl);
        request.setReturnUrl(returnUrl);

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", outTradeNo);
        bizContent.put("total_amount", totalAmount);
        bizContent.put("subject", subject);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());

        return alipayClient.pageExecute(request).getBody();
    }

    /**
     * 验证支付宝回调签名
     */
    public boolean verifySignature(Map<String, String> params) throws AlipayApiException {
        // 使用官方推荐的 rsaCheckV1，直接传入完整参数、配置的编码与签名算法
        return AlipaySignature.rsaCheckV1(params, alipayPublicKey, charset, signType);
    }

    /**
     * 解析支付宝回调参数（从HttpServletRequest到Map）
     */
    public Map<String, String> parseAlipayParams(HttpServletRequest request) {
        return request.getParameterMap().entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue()[0]
            ));
    }
}
