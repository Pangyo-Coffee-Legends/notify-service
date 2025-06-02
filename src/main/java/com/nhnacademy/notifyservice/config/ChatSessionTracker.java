package com.nhnacademy.notifyservice.config;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatSessionTracker {
    private final Map<String, String> notificationSessionIdToUserEmailMap = new ConcurrentHashMap<>();

    public void notificationSessionIdToUserEmailMapRegisterSession(String sessionId, String userEmail) {
        notificationSessionIdToUserEmailMap.put(sessionId, userEmail);
    }

    public void notificationSessionIdToUserEmailMapUnregisterSession(String sessionId) {
        notificationSessionIdToUserEmailMap.remove(sessionId);
    }

    public Map<String, String> getNotificationSessionIdToUserEmailMap() {
        return notificationSessionIdToUserEmailMap;
    }
}

