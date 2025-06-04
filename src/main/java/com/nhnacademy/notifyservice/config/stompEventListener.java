package com.nhnacademy.notifyservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * STOMP WebSocket 세션의 연결 및 해제 이벤트를 처리하는 이벤트 리스너 클래스입니다.
 *
 * Spring과 STOMP는 기본적으로 세션 관리를 자동으로 처리하지만,
 * 이 클래스는 연결/해제 이벤트를 기록하고 연결된 세션 수를 실시간으로 확인하는 목적으로 사용됩니다.
 * 주로 로깅과 디버깅 목적으로 활용되며, 실시간 알림 서비스에서 활성 사용자 세션을 추적합니다.
 *
 * <p>이 클래스는 다음과 같은 이벤트를 처리합니다:</p>
 * <ul>
 * <li>{@link SessionConnectEvent} - 새로운 STOMP 연결이 설정될 때</li>
 * <li>{@link SessionDisconnectEvent} - STOMP 연결이 해제될 때</li>
 * </ul>
 *
 * @see NotificationSessionTracker
 * @see SessionConnectEvent
 * @see SessionDisconnectEvent
 */
@Slf4j
@Component
public class stompEventListener {
    /**
     * 알림 세션을 추적하고 관리하는 서비스 객체입니다.
     * 세션 ID와 사용자 이메일 간의 매핑을 관리하여 타겟팅된 알림 전송을 가능하게 합니다.
     */
    private final NotificationSessionTracker notificationSessionTracker;
    /**
     * StompEventListener의 생성자입니다.
     *
     * @param notificationSessionTracker 알림 세션 추적을 위한 서비스 객체
     */
    public stompEventListener(NotificationSessionTracker notificationSessionTracker) {
        this.notificationSessionTracker = notificationSessionTracker;
    }
    /**
     * STOMP WebSocket 연결 이벤트를 처리합니다.
     *
     * 클라이언트가 WebSocket을 통해 STOMP 연결을 설정할 때 호출됩니다.
     * 요청 헤더에서 'X-USER' 헤더를 통해 사용자 이메일을 추출하고,
     * 세션 ID와 함께 NotificationSessionTracker에 등록합니다.
     *
     * <p>처리 과정:</p>
     * <ol>
     * <li>STOMP 헤더에서 사용자 이메일 추출 (X-USER 헤더)</li>
     * <li>세션 ID 추출</li>
     * <li>세션 추적기에 매핑 정보 등록</li>
     * <li>연결 로그 기록</li>
     * </ol>
     *
     * @param event STOMP 연결 이벤트 객체
     * @see SessionConnectEvent
     * @see StompHeaderAccessor
     */
    @EventListener
    public void connectHandle(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String userEmail = headerAccessor.getFirstNativeHeader("X-USER");

        if(userEmail != null) {
            String sessionId = headerAccessor.getSessionId();
            notificationSessionTracker.notificationSessionIdToUserEmailMapRegisterSession(sessionId, userEmail);
            log.info("LIST CONNECT: {} joined", userEmail);
            log.info("LIST CONNECT: {}", notificationSessionTracker.getNotificationSessionIdToUserEmailMap().values());
        }
    }
    /**
     * STOMP WebSocket 연결 해제 이벤트를 처리합니다.
     *
     * 클라이언트가 WebSocket 연결을 종료하거나 연결이 끊어질 때 호출됩니다.
     * 해당 세션을 NotificationSessionTracker에서 제거하여
     * 더 이상 해당 세션으로 알림이 전송되지 않도록 합니다.
     *
     * <p>처리 과정:</p>
     * <ol>
     * <li>STOMP 헤더에서 사용자 이메일 추출 (디버깅용)</li>
     * <li>세션 ID 추출</li>
     * <li>세션 추적기에서 해당 세션 제거</li>
     * <li>연결 해제 로그 기록</li>
     * </ol>
     *
     * @param event STOMP 연결 해제 이벤트 객체
     * @see SessionDisconnectEvent
     * @see StompHeaderAccessor
     */
    @EventListener
    public void disconnectHandle(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String userEmail = headerAccessor.getFirstNativeHeader("X-USER");

        String sessionId = headerAccessor.getSessionId();
        notificationSessionTracker.notificationSessionIdToUserEmailMapUnregisterSession(sessionId);
        log.info("LIST DISCONNECT: session {} disconnected {}", sessionId, userEmail);
    }
}
