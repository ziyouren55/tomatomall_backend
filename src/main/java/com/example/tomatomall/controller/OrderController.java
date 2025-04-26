package com.example.tomatomall.controller;

import com.alipay.api.AlipayApiException;
import com.example.tomatomall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.tomatomall.vo.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/{orderId}/pay")
    public Response initiatePayment(@PathVariable Integer orderId){
        return Response.buildSuccess(orderService.initiatePayment(orderId));
    }

    @PostMapping("/notify")
    public void handleAlipayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException, AlipayApiException {
        try {
            orderService.handleAlipayNotify(request);
            response.getWriter().print("success");
        } catch (Exception e) {
            response.getWriter().print("fail");
        }
    }

    @GetMapping("/returnUrl")
    public String returnUrl() {
        return "支付成功了";
    }
}
