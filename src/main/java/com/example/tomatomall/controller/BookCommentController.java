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
    public Response<String> addBookComment(@PathVariable("productId") Integer productId,
                                   @RequestAttribute(value = "userId", required = false) Integer userId,
                                   @RequestBody BookCommentVO bookCommentVO){
        return Response.buildSuccess(bookCommentService.addBookComment(productId, userId, bookCommentVO));
    }

    @GetMapping("/{productId}")
    public Response<?> getBookComment(@PathVariable("productId") Integer productId,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size){
        return Response.buildSuccess(bookCommentService.getBookComment(productId, page, size));
    }

    @DeleteMapping("/{id}")
    public Response<String> deleteBookComment(@PathVariable("id") Integer id,
                                      @RequestAttribute(value = "userId", required = false) Integer userId){
        return Response.buildSuccess(bookCommentService.deleteBookComment(id, userId));
    }
}
