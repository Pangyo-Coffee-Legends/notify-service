package com.nhnacademy.notifyservice.config;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 실시간 알림 세션을 추적하고 관리하는 컴포넌트 클래스입니다.
 * WebSocket 기반의 실시간 알림 서비스에서 알림 세션 ID와 사용자 이메일 간의 매핑을 관리합니다.
 * 사용자가 알림을 받기 위해 연결된 세션들을 추적하여 특정 사용자에게 타겟팅된 알림을 전송할 수 있도록 합니다.
 *
 */
@Component
public class NotificationSessionTracker {

    /**
     * 알림 세션 ID와 사용자 이메일을 매핑하는 동시성 안전한 맵입니다.
     * 실시간 알림 전송을 위해 활성화된 WebSocket 세션과 해당 사용자를 연결합니다.
     * ConcurrentHashMap을 사용하여 멀티스레드 환경에서 안전하게 접근할 수 있습니다.
     */
    private final Map<String, String> notificationSessionIdToUserEmailMap = new ConcurrentHashMap<>();

    /**
     * 새로운 알림 세션을 등록합니다.
     * 사용자가 알림을 받기 위해 WebSocket 연결을 설정할 때 호출됩니다.
     * 세션 ID와 사용자 이메일을 매핑하여 저장하므로, 나중에 특정 사용자에게 알림을 전송할 수 있습니다.
     *
     * @param sessionId 등록할 알림 세션의 고유 식별자 (WebSocket 세션 ID)
     * @param userEmail 알림을 받을 사용자의 이메일 주소
     * @throws IllegalArgumentException sessionId 또는 userEmail이 null인 경우
     */
    public void notificationSessionIdToUserEmailMapRegisterSession(String sessionId, String userEmail) {
        notificationSessionIdToUserEmailMap.put(sessionId, userEmail);
    }

    /**
     * 기존 알림 세션을 제거합니다.
     * 사용자가 알림 연결을 종료하거나 WebSocket 연결이 끊어질 때 호출됩니다.
     * 지정된 세션 ID에 해당하는 매핑을 제거하여 더 이상 해당 세션으로 알림이 전송되지 않도록 합니다.
     *
     * @param sessionId 제거할 알림 세션의 고유 식별자
     * @return 제거된 사용자 이메일, 세션이 존재하지 않았다면 null
     */
    public void notificationSessionIdToUserEmailMapUnregisterSession(String sessionId) {
        notificationSessionIdToUserEmailMap.remove(sessionId);
    }

    /**
     * 현재 등록된 모든 알림 세션 매핑을 반환합니다.
     * 알림 서비스에서 활성화된 모든 사용자 세션을 확인하거나
     * 특정 사용자의 세션을 찾기 위해 사용됩니다.
     * 반환되는 맵은 원본 맵의 참조이므로 직접 수정 시 주의가 필요합니다.
     *
     * @return 알림 세션 ID를 키로, 사용자 이메일을 값으로 하는 맵
     */
    public Map<String, String> getNotificationSessionIdToUserEmailMap() {
        return notificationSessionIdToUserEmailMap;
    }
}