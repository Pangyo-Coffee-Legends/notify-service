package com.nhnacademy.notifyservice.repository;

import com.nhnacademy.notifyservice.domain.Member;
import com.nhnacademy.notifyservice.domain.NotificationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 알림 메시지(NotificationMessage) 엔티티에 대한 데이터 접근을 담당하는 JPA 리포지토리 인터페이스입니다.
 *
 * 이 리포지토리는 실시간 알림 시스템에서 알림 메시지의 저장, 조회, 관리 기능을 제공합니다[1].
 * Spring Data JPA의 기본 CRUD 기능과 함께 사용자별 알림 조회, 읽음 상태 기반 필터링,
 * 읽지 않은 알림 개수 계산 등의 특화된 기능을 지원합니다.
 *
 * <p>주요 사용 사례:</p>
 * <ul>
 * <li>사용자별 알림 히스토리 관리</li>
 * <li>읽지 않은 알림 개수 실시간 카운팅</li>
 * <li>알림 읽음 상태 일괄 처리</li>
 * <li>사용자 맞춤형 알림 목록 제공</li>
 * </ul>
 *
 * @author NHN Academy
 * @version 1.0
 * @since 1.0
 * @see NotificationMessage
 * @see Member
 * @see JpaRepository
 */
public interface NotificationMessageRepository extends JpaRepository<NotificationMessage, Long> {
    /**
     * 특정 회원의 읽지 않은 알림 메시지 개수를 조회합니다.
     *
     * 실시간 알림 시스템에서 사용자 인터페이스의 알림 배지나 카운터를 표시하기 위해 사용됩니다[1].
     * 이 메서드는 사용자가 아직 확인하지 않은 알림의 정확한 개수를 제공하여
     * 실시간으로 업데이트되는 알림 카운터 기능을 지원합니다.
     *
     * <p>사용 시나리오:</p>
     * <ul>
     * <li><strong>실시간 카운터 업데이트:</strong> 새 알림 도착 시 읽지 않은 알림 개수 갱신</li>
     * <li><strong>UI 배지 표시:</strong> 헤더나 사이드바의 알림 아이콘에 숫자 배지 표시</li>
     * <li><strong>알림 상태 확인:</strong> 사용자의 미확인 알림 존재 여부 판단</li>
     * </ul>
     *
     * <p>사용 예시:</p>
     * <pre>{@code
     * // NotificationServiceImpl에서 읽지 않은 알림 개수 조회
     * Long unreadCount = notificationMessageRepository.countByMemberAndIsReadFalse(member);
     *
     * // WebSocket을 통한 실시간 카운터 업데이트
     * messageTemplate.convertAndSend("/notification/unread-notification-count-updates/" + userEmail, unreadCount);
     * }</pre>
     *
     * @param member 읽지 않은 알림 개수를 조회할 회원 객체
     * @return Long 해당 회원의 읽지 않은 알림 메시지 개수 (0 이상의 값)
     * @throws IllegalArgumentException member가 null인 경우
     */
    Long countByMemberAndIsReadFalse(Member member);

    /**
     * 특정 회원의 모든 알림 메시지를 조회합니다.
     *
     * 사용자의 전체 알림 히스토리를 제공하기 위해 사용되며, 읽음/미읽음 상태에 관계없이
     * 해당 사용자에게 전송된 모든 알림을 시간순으로 조회합니다[1].
     * 알림 관리 페이지나 히스토리 조회 기능에서 활용됩니다.
     *
     * <p>주요 활용 영역:</p>
     * <ul>
     * <li><strong>알림 히스토리:</strong> 사용자의 과거 알림 내역 전체 조회</li>
     * <li><strong>알림 관리:</strong> 읽은 알림과 읽지 않은 알림을 포함한 전체 목록 제공</li>
     * <li><strong>통계 분석:</strong> 사용자별 알림 수신 패턴 분석</li>
     * <li><strong>데이터 마이그레이션:</strong> 사용자 데이터 백업 및 이전</li>
     * </ul>
     *
     * <p>사용 예시:</p>
     * <pre>{@code
     * // 사용자의 전체 알림 히스토리 조회
     * List<NotificationMessage> allNotifications = notificationMessageRepository.findByMember(member);
     *
     * // DTO로 변환하여 클라이언트에 전달
     * List<NotificationMessageDto> historyDtos = allNotifications.stream()
     *     .map(msg -> NotificationMessageDto.builder()
     *         .content(msg.getContent())
     *         .createdAt(msg.getCreatedAt())
     *         .build())
     *     .collect(Collectors.toList());
     * }</pre>
     *
     * @param member 알림 메시지를 조회할 회원 객체
     * @return List&lt;NotificationMessage&gt; 해당 회원의 모든 알림 메시지 목록, 없으면 빈 리스트
     * @throws IllegalArgumentException member가 null인 경우
     */
    List<NotificationMessage> findByMember(Member member);

    /**
     * 특정 회원의 읽지 않은 알림 메시지 목록을 조회합니다.
     *
     * 사용자가 아직 확인하지 않은 알림들만을 필터링하여 조회하는 기능으로,
     * 알림 읽음 처리나 미확인 알림 표시 등에 활용됩니다[1].
     * 이 메서드는 읽음 상태 일괄 업데이트 작업에서 특히 중요한 역할을 합니다.
     *
     * <p>핵심 사용 사례:</p>
     * <ul>
     * <li><strong>읽음 처리:</strong> 사용자가 알림 페이지 접속 시 미읽음 알림들을 일괄 읽음 처리</li>
     * <li><strong>우선순위 알림:</strong> 읽지 않은 중요 알림만 별도로 표시</li>
     * <li><strong>알림 필터링:</strong> 새로운 알림만 하이라이트하여 사용자 경험 향상</li>
     * <li><strong>배치 처리:</strong> 특정 조건의 미읽음 알림들을 일괄 처리</li>
     * </ul>
     *
     * <p>사용 예시:</p>
     * <pre>{@code
     * // 읽지 않은 알림들을 모두 읽음 처리
     * List<NotificationMessage> unreadMessages = notificationMessageRepository.findByMemberAndIsReadFalse(member);
     *
     * for(NotificationMessage message : unreadMessages) {
     *     message.updateIsRead(true);  // 읽음 상태로 변경
     * }
     *
     * // 변경사항은 @Transactional에 의해 자동 저장됨
     * }</pre>
     *
     * @param member 읽지 않은 알림 메시지를 조회할 회원 객체
     * @return List&lt;NotificationMessage&gt; 해당 회원의 읽지 않은 알림 메시지 목록, 없으면 빈 리스트
     * @throws IllegalArgumentException member가 null인 경우
     */
    List<NotificationMessage> findByMemberAndIsReadFalse(Member member);
}
