package com.nhnacademy.notifyservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

// 스프링과 STOMP는 기본적으로 세션관리를 자동(내부적)으로 처리한다.
// 연결/해제 이벤트를 기록, 연결된 세션수를 실시간으로 확인하는 목적으로 이벤트 리스너를 생성 -> 로그, 디버깅 목적
@Slf4j
@Component
public class stompEventListener {
    private final ChatSessionTracker notificationSessionTracker;

    public stompEventListener(ChatSessionTracker notificationSessionTracker) {
        this.notificationSessionTracker = notificationSessionTracker;
    }

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

    @EventListener
    public void disconnectHandle(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String userEmail = headerAccessor.getFirstNativeHeader("X-USER");

        String sessionId = headerAccessor.getSessionId();
        notificationSessionTracker.notificationSessionIdToUserEmailMapUnregisterSession(sessionId);
        log.info("LIST DISCONNECT: session {} disconnected {}", sessionId, userEmail);
    }
}
