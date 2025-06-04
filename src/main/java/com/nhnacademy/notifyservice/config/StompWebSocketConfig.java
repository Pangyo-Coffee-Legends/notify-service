package com.nhnacademy.notifyservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * 실시간 알림 서비스를 위한 STOMP WebSocket 설정 클래스입니다.
 *
 * 이 설정 클래스는 Spring WebSocket과 STOMP 프로토콜을 사용하여
 * 실시간 양방향 통신을 가능하게 하는 WebSocket 메시지 브로커를 구성합니다.
 * 클라이언트와 서버 간의 실시간 알림 전송을 위한 엔드포인트와 메시지 브로커를 설정합니다.
 *
 * <p>주요 구성 요소:</p>
 * <ul>
 * <li>STOMP 엔드포인트 등록 및 CORS 설정</li>
 * <li>SockJS 폴백 지원</li>
 * <li>메시지 브로커 경로 설정</li>
 * <li>실시간 알림 구독/발행 메커니즘</li>
 * </ul>
 *
 * <p>이 설정은 다음과 같은 실시간 알림 기능을 지원합니다:</p>
 * <ul>
 * <li>사용자별 개인화된 알림 전송</li>
 * <li>읽지 않은 알림 개수 실시간 업데이트</li>
 * <li>알림 메시지 실시간 푸시</li>
 * </ul>
 *
 * @see WebSocketMessageBrokerConfigurer
 * @see EnableWebSocketMessageBroker
 * @see StompEndpointRegistry
 * @see MessageBrokerRegistry
 */
@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /**
     * STOMP WebSocket 엔드포인트를 등록하고 구성합니다.
     *
     * 클라이언트가 WebSocket 연결을 설정하기 위한 초기 엔드포인트를 정의합니다.
     * 이 엔드포인트로 WebSocket 요청이 들어오면, STOMP에서 클라이언트별 및
     * 토픽(구독 채널)별로 구독 정보를 저장하고 관리합니다.
     *
     * <p>설정된 기능:</p>
     * <ul>
     * <li><strong>엔드포인트 URL:</strong> {@code /ws/notification/connect}</li>
     * <li><strong>허용된 Origin:</strong> {@code https://aiot2.live}, {@code http://localhost:10253}</li>
     * <li><strong>SockJS 지원:</strong> WebSocket을 지원하지 않는 브라우저를 위한 폴백 제공</li>
     * </ul>
     *
     * <p>클라이언트 연결 예시:</p>
     * <pre>{@code
     * const socket = new SockJS('/ws/notification/connect');
     * const stompClient = Stomp.over(socket);
     * }</pre>
     *
     * @param registry STOMP 엔드포인트를 등록하기 위한 레지스트리
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // endpoint url로 webSocket 요청이 들어오면, STOMP에서 클라이언트별, topic(=room)별로 구독정보 저장
        // endpoint는 최초 연결을 맺기 위한 url이다.
        registry.addEndpoint("ws/notification/connect")
                .setAllowedOriginPatterns("https://aiot2.live","http://localhost:10253")
        // sockJs 라이브러리를 통한 요청을 허용하는 설정
                .withSockJS();
    }

    /**
     * 메시지 브로커를 구성하여 실시간 알림 전송 경로를 설정합니다.
     *
     * 서버에서 클라이언트로 메시지를 전송할 때 사용하는 브로커 경로를 정의합니다.
     * {@code /notification} 경로를 통해 클라이언트가 특정 토픽을 구독하고
     * 서버가 해당 토픽으로 메시지를 발행할 수 있습니다.
     *
     * <p>브로커 경로 구성:</p>
     * <ul>
     * <li><strong>브로커 Prefix:</strong> {@code /notification}</li>
     * <li><strong>사용 목적:</strong> 서버 → 클라이언트 메시지 전송</li>
     * <li><strong>구독 패턴:</strong> {@code /notification/{userEmail}}, {@code /notification/unread-notification-count-updates/{userEmail}} 등</li>
     * </ul>
     *
     * <p>클라이언트 구독 예시:</p>
     * <pre>{@code
     * // 개인 알림 구독
     * stompClient.subscribe('/notification/user@example.com', function(message) {
     *     // 알림 처리 로직
     * });
     *
     * // 읽지 않은 알림 개수 업데이트 구독
     * stompClient.subscribe('/notification/unread-notification-count-updates/user@example.com', function(count) {
     *     // 카운터 업데이트 로직
     * });
     * }</pre>
     *
     * <p>서버 메시지 전송 예시:</p>
     * <pre>{@code
     * // NotificationServiceImpl에서 사용
     * messageTemplate.convertAndSend("/notification/" + userEmail, notificationContent);
     * messageTemplate.convertAndSend("/notification/unread-notification-count-updates/" + userEmail, count);
     * }</pre>
     *
     * @param registry 메시지 브로커 설정을 위한 레지스트리
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트가 메시지를 보낼 때 사용하는 목적지 prefix
        // 클라이언트에서 /publish 형태로 시작하는 url 패턴으로 메시지가 발행되면 @Controller 객체의 @MessageMapping 메서드로 라우팅
        // 서버가 메시지를 클라이언트에게 발행할 때 사용하는 경로 prefix
        registry.enableSimpleBroker("/notification");
    }
}