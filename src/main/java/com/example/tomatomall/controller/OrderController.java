package com.example.tomatomall.controller;

import com.alipay.api.AlipayApiException;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.vo.shopping.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Value("${frontend.base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    @GetMapping("")
    public Response getAllOrders()
    {
        return Response.buildSuccess(orderService.getAllOrders());
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public Response getOrderById(@PathVariable Integer orderId,
                                  @RequestAttribute("userId") Integer userId) {
        return Response.buildSuccess(orderService.getOrderById(orderId, userId));
    }

    /**
     * 商家视图：获取某订单中属于当前商家的明细（仅商家可访问）
     */
    @GetMapping("/merchant/{orderId}")
    public Response getOrderForMerchant(@PathVariable Integer orderId,
                                        @RequestAttribute("userId") Integer userId) {
        return Response.buildSuccess(orderService.getOrderForMerchant(orderId, userId));
    }

    /**
     * 获取当前用户的订单列表
     */
    @GetMapping("/my")
    public Response getMyOrders(@RequestAttribute("userId") Integer userId) {
        return Response.buildSuccess(orderService.getOrdersByUserId(userId));
    }

    @PostMapping("/{orderId}/pay")
    public Response initiatePayment(@PathVariable Integer orderId){
        return Response.buildSuccess(orderService.initiatePayment(orderId));
    }

    @PostMapping("/notify")
    public void handleAlipayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException, AlipayApiException {
        System.out.println("支付宝回调通知来了");
        try {
            System.out.println("try");
            orderService.handleAlipayNotify(request);
            response.getWriter().print("success");
            System.out.println("suc");
        } catch (Exception e) {
            response.getWriter().print("fail");
            System.out.println("fail");
        }
    }

    @GetMapping("/returnUrl")
    public void returnUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("收到return_url");
        // 同步回跳：后端中转，携带核心参数跳转到前端支付结果页
        String outTradeNo = request.getParameter("out_trade_no");
        String tradeNo = request.getParameter("trade_no");
        String totalAmount = request.getParameter("total_amount");
        String status = request.getParameter("trade_status");

        // 构造前端支付结果页路由，前端基地址从配置中读取
        String base = frontendBaseUrl.endsWith("/") ? frontendBaseUrl.substring(0, frontendBaseUrl.length() - 1) : frontendBaseUrl;
        StringBuilder redirectUrl = new StringBuilder(base + "/pay/result?pay=success");
        if (status != null) redirectUrl.append("&status=").append(status);
        if (outTradeNo != null) redirectUrl.append("&orderId=").append(outTradeNo);
        if (tradeNo != null) redirectUrl.append("&tradeNo=").append(tradeNo);
        if (totalAmount != null) redirectUrl.append("&amount=").append(totalAmount);

        System.out.println("return_url redirect to: " + redirectUrl);
        response.sendRedirect(redirectUrl.toString());
    }
}
