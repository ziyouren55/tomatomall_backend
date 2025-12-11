package com.example.tomatomall.controller;

import com.alipay.api.AlipayApiException;
import com.example.tomatomall.service.OrderService;
import com.example.tomatomall.vo.shopping.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void returnUrl(HttpServletResponse response) throws IOException {
        // RestController 默认返回字符串到 body，这里使用重定向确保浏览器跳转
        System.out.println("return_url");
        response.sendRedirect("/#/orders?pay=success");
    }
}
