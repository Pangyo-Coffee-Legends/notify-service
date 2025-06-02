package com.nhnacademy.notifyservice.controller;

import com.nhnacademy.notifyservice.service.NotificationServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationServiceImpl notificationService;

    public NotificationController(NotificationServiceImpl notificationService) {
        this.notificationService = notificationService;
    }

    // 사용자의 이메일로 Role 찾기
    @GetMapping("/find/role")
    public ResponseEntity<?> findByRoleName(@RequestHeader("X-USER") String userEmail) {
        return ResponseEntity.ok(notificationService.findRoleByMember(userEmail));
    }

    // 내가 읽지 않은 알림 메시지의 총 개수
    @GetMapping("/unread/count")
    public ResponseEntity<?> getNotificationUnreadCount(@RequestHeader("X-USER") String userEmail) {
        return ResponseEntity.ok(notificationService.getNotificationUnreadCount(userEmail));
    }

    // 알림 메시지 읽음 처리
    @GetMapping("/read")
    public ResponseEntity<?> readNotification(@RequestHeader("X-USER") String userEmail) {
        notificationService.readNotification(userEmail);
        return ResponseEntity.ok().build();
    }

    // 알림 메시지 내역 불러오기
    @GetMapping("/history")
    public ResponseEntity<?> getHistoryNotification(@RequestHeader("X-USER") String userEmail) {
        return ResponseEntity.ok(notificationService.getHistoryNotification(userEmail));
    }
}
