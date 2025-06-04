package com.nhnacademy.notifyservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 실시간 알림 메시지 정보를 전송하기 위한 데이터 전송 객체(DTO) 클래스입니다.
 *
 * 이 DTO는 WebSocket을 통한 실시간 알림 서비스에서 클라이언트와 서버 간에
 * 알림 메시지 데이터를 주고받을 때 사용됩니다.
 * 알림의 내용과 생성 시간 정보를 포함하여 사용자에게 전달되는 알림의 기본 구조를 정의합니다.
 *
 * <p>주요 사용 사례:</p>
 * <ul>
 * <li>WebSocket을 통한 실시간 알림 전송</li>
 * <li>알림 히스토리 조회 시 응답 데이터</li>
 * <li>클라이언트 측 알림 표시용 데이터 구조</li>
 * </ul>
 *
 * @see java.time.LocalDateTime
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationMessageDto {

    /**
     * 알림 메시지의 실제 내용입니다.
     *
     * 사용자에게 전달될 알림의 텍스트 내용을 포함합니다.
     * 예: "새로운 메시지가 도착했습니다", "주문이 완료되었습니다" 등
     *
     * @since 1.0
     */
    private String content;

    /**
     * 알림 메시지가 생성된 날짜와 시간입니다.
     *
     * 알림이 시스템에서 생성된 정확한 시점을 나타내며,
     * 클라이언트에서 알림 목록을 시간순으로 정렬하거나
     * 상대적 시간 표시("5분 전", "1시간 전" 등)를 위해 사용됩니다.
     *
     * @since 1.0
     */
    private LocalDateTime createdAt;
}
