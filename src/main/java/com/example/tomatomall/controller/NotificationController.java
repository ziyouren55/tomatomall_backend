package com.example.tomatomall.controller;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Notification;
import com.example.tomatomall.service.NotificationService;
import com.example.tomatomall.util.UserContext;
import com.example.tomatomall.enums.UserRole;
import com.example.tomatomall.vo.PageResultVO;
import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    @GetMapping("/unread-count")
    public Response<Long> unreadCount() {
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) throw TomatoMallException.notLogin();
        long cnt = notificationService.countUnreadByUserId(currentUserId);
        return Response.buildSuccess(cnt);
    }

    @GetMapping
    public Response<PageResultVO<Notification>> list(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) throw TomatoMallException.notLogin();
        PageResultVO<Notification> result = notificationService.getNotificationsByUserId(currentUserId, page, pageSize);
        return Response.buildSuccess(result);
    }

    @GetMapping("/{id}")
    public Response<Notification> detail(@PathVariable Long id) {
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) throw TomatoMallException.notLogin();
        Optional<Notification> opt = notificationService.getNotificationDetailForUser(id, currentUserId);
        return opt.map(Response::buildSuccess).orElseThrow(() -> TomatoMallException.notificationNotFind());
    }

    @PostMapping("/{id}/mark-read")
    public Response<Integer> markReadSingle(@PathVariable Long id) {
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) throw TomatoMallException.notLogin();
        int updated = notificationService.markReadByUserId(currentUserId, Collections.singletonList(id));
        return Response.buildSuccess(updated);
    }

    @PostMapping("/mark-read")
    public Response<Integer> markReadBulk(@RequestBody Map<String, List<Long>> body) {
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) throw TomatoMallException.notLogin();
        List<Long> ids = body.getOrDefault("ids", Collections.emptyList());
        int updated = notificationService.markReadByUserId(currentUserId, ids);
        return Response.buildSuccess(updated);
    }

    @PostMapping("/mark-all-read")
    public Response<Integer> markAllRead() {
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) throw TomatoMallException.notLogin();
        int updated = notificationService.markAllReadByUserId(currentUserId);
        return Response.buildSuccess(updated);
    }

    @DeleteMapping("/{id}")
    public Response<String> deleteSingle(@PathVariable Long id) {
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) throw TomatoMallException.notLogin();
        notificationService.deleteByUserId(currentUserId, Collections.singletonList(id));
        return Response.buildSuccess("删除成功");
    }

    @PostMapping("/delete")
    public Response<Integer> deleteBulk(@RequestBody Map<String, List<Long>> body) {
        Integer currentUserId = UserContext.getCurrentUserId();
        if (currentUserId == null) throw TomatoMallException.notLogin();
        List<Long> ids = body.getOrDefault("ids", Collections.emptyList());
        int deleted = notificationService.deleteByUserId(currentUserId, ids);
        return Response.buildSuccess(deleted);
    }

    // 管理员/系统使用的创建通知接口
    @PostMapping
    public Response<Notification> create(@RequestBody Notification notification) {
        UserRole role = UserContext.getCurrentUserRole();
        if (role != UserRole.ADMIN) throw TomatoMallException.permissionDenied();
        Notification saved = notificationService.createNotification(notification);
        return Response.buildSuccess(saved);
    }
}


