package com.example.tomatomall.controller;

import com.example.tomatomall.service.BookCommentService;
import com.example.tomatomall.vo.Response;
import com.example.tomatomall.vo.bookComment.BookCommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookComment")
public class BookCommentController {

    @Autowired
    BookCommentService bookCommentService;
    @PostMapping("/{productId}")
    public Response addBookComment(@PathVariable("productId") Integer productId, @RequestBody BookCommentVO bookCommentVO){
        return Response.buildSuccess(bookCommentService.addBookComment(productId, bookCommentVO));
    }

    @GetMapping("/{productId}")
    public Response getBookComment(@PathVariable("productId") Integer productId){
        return Response.buildSuccess(bookCommentService.getBookComment(productId));
    }

    @DeleteMapping("/{id}")
    public Response deleteBookComment(@PathVariable("Id") Integer id){
        return Response.buildSuccess(bookCommentService.deleteBookComment(id));
    }
}
