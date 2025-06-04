package com.nhnacademy.notifyservice.controller;

import com.nhnacademy.notifyservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 실시간 알림 서비스의 REST API 엔드포인트를 제공하는 컨트롤러 클래스입니다.
 *
 * 이 컨트롤러는 사용자의 알림 관련 기능을 처리하며, 다음과 같은 주요 기능을 제공합니다:
 * <ul>
 * <li>사용자 역할(Role) 조회</li>
 * <li>읽지 않은 알림 개수 조회</li>
 * <li>알림 읽음 처리</li>
 * <li>알림 히스토리 조회</li>
 * </ul>
 *
 * <p>모든 API는 HTTP 헤더의 'X-USER'를 통해 사용자 이메일을 전달받아 인증 및 권한 처리를 수행합니다.</p>
 *
 * @see NotificationService
 */
@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {
    /**
     * 알림 서비스 비즈니스 로직을 처리하는 서비스 구현체입니다.
     * 알림 관련 모든 비즈니스 로직은 이 서비스를 통해 처리됩니다.
     */
    private final NotificationService notificationService;

    /**
     * 사용자의 이메일을 기반으로 해당 사용자의 역할(Role)을 조회합니다.
     *
     * 이 API는 사용자의 권한을 확인하거나 역할 기반의 알림 필터링을 위해 사용됩니다.
     * 예를 들어, 관리자와 일반 사용자에게 서로 다른 유형의 알림을 전송할 때 활용됩니다.
     *
     * @param userEmail HTTP 헤더 'X-USER'에서 전달받은 사용자 이메일 주소
     * @return ResponseEntity 사용자의 역할 정보를 포함한 응답 객체
     * @throws IllegalArgumentException userEmail이 null이거나 빈 문자열인 경우
     */
    @GetMapping("/find/role")
    public ResponseEntity<?> findByRoleName(@RequestHeader("X-USER") String userEmail) {
        return ResponseEntity.ok(notificationService.findRoleByMember(userEmail));
    }

    /**
     * 특정 사용자가 읽지 않은 알림 메시지의 총 개수를 조회합니다.
     *
     * 이 API는 사용자 인터페이스에서 알림 배지나 카운터를 표시하기 위해 사용됩니다.
     * 실시간으로 업데이트되는 읽지 않은 알림 개수를 제공하여 사용자 경험을 향상시킵니다.
     *
     * @param userEmail HTTP 헤더 'X-USER'에서 전달받은 사용자 이메일 주소
     * @return ResponseEntity 읽지 않은 알림의 개수를 포함한 응답 객체
     * @throws IllegalArgumentException userEmail이 null이거나 빈 문자열인 경우
     */
    @GetMapping("/unread/count")
    public ResponseEntity<?> getNotificationUnreadCount(@RequestHeader("X-USER") String userEmail) {
        return ResponseEntity.ok(notificationService.getNotificationUnreadCount(userEmail));
    }

    /**
     * 특정 사용자의 모든 읽지 않은 알림을 읽음 상태로 처리합니다.
     *
     * 사용자가 알림 목록을 확인하거나 알림 페이지에 접근할 때 호출되어
     * 해당 사용자의 모든 미읽음 알림을 일괄적으로 읽음 처리합니다.
     * 이를 통해 알림 카운터가 0으로 리셋됩니다.
     *
     * @param userEmail HTTP 헤더 'X-USER'에서 전달받은 사용자 이메일 주소
     * @return ResponseEntity 성공 응답 (HTTP 200 OK)
     * @throws IllegalArgumentException userEmail이 null이거나 빈 문자열인 경우
     */
    @GetMapping("/read")
    public ResponseEntity<?> readNotification(@RequestHeader("X-USER") String userEmail) {
        notificationService.readNotification(userEmail);
        return ResponseEntity.ok().build();
    }

    /**
     * 특정 사용자의 알림 메시지 히스토리를 조회합니다.
     *
     * 사용자가 받은 모든 알림(읽음/미읽음 포함)의 목록을 시간순으로 정렬하여 반환합니다.
     * 알림 내용, 발송 시간, 읽음 상태 등의 상세 정보를 포함하여
     * 사용자가 과거 알림을 확인할 수 있도록 합니다.
     *
     * @param userEmail HTTP 헤더 'X-USER'에서 전달받은 사용자 이메일 주소
     * @return ResponseEntity 사용자의 알림 히스토리 목록을 포함한 응답 객체
     * @throws IllegalArgumentException userEmail이 null이거나 빈 문자열인 경우
     */
    @GetMapping("/history")
    public ResponseEntity<?> getHistoryNotification(@RequestHeader("X-USER") String userEmail) {
        return ResponseEntity.ok(notificationService.getHistoryNotification(userEmail));
    }
}
