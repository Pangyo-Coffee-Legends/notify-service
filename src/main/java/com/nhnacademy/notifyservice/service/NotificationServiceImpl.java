package com.nhnacademy.notifyservice.service;

import com.nhnacademy.notifyservice.config.NotificationSessionTracker;
import com.nhnacademy.notifyservice.converter.HtmlTextConverter;
import com.nhnacademy.notifyservice.domain.Member;
import com.nhnacademy.notifyservice.domain.NotificationMessage;
import com.nhnacademy.notifyservice.domain.Role;
import com.nhnacademy.notifyservice.dto.NotificationMessageDto;
import com.nhnacademy.notifyservice.dto.RoleDto;
import com.nhnacademy.notifyservice.repository.MemberRepository;
import com.nhnacademy.notifyservice.repository.NotificationMessageRepository;
import com.nhnacademy.notifyservice.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 실시간 알림 서비스의 핵심 비즈니스 로직을 구현하는 서비스 클래스입니다.
 *
 * 이 서비스는 WebSocket을 통한 실시간 알림 전송, 사용자 세션 관리, 알림 메시지 저장 및 조회 등의
 * 알림 시스템의 모든 핵심 기능을 담당합니다[1].
 * 특히 사용자의 활성 세션 수를 기반으로 한 스마트 알림 처리와 실시간 메시지 전송을 지원합니다.
 *
 * <p>주요 기능:</p>
 * <ul>
 * <li>사용자 역할(Role) 기반 알림 관리</li>
 * <li>실시간 WebSocket 알림 전송</li>
 * <li>세션 기반 스마트 읽음 처리</li>
 * <li>알림 히스토리 관리</li>
 * <li>읽지 않은 알림 카운팅</li>
 * </ul>
 *
 * @see NotificationService
 * @see NotificationSessionTracker
 * @see SimpMessageSendingOperations
 */
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {


    private final NotificationMessageRepository notificationMessageRepository;
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final SimpMessageSendingOperations messageTemplate;
    private final NotificationSessionTracker notificationSessionTracker;
    private final HtmlTextConverter htmlTextConverter;
    /**
     * 사용자 이메일을 기반으로 해당 사용자의 역할 정보를 조회합니다.
     *
     * 사용자의 권한을 확인하거나 역할 기반 알림 필터링을 위해 사용됩니다.
     * 조회된 역할 정보는 RoleDto 형태로 변환되어 반환됩니다.
     *
     * @param userEmail 조회할 사용자의 이메일 주소
     * @return RoleDto 사용자의 역할 정보를 담은 DTO 객체
     * @throws EntityNotFoundException 해당 이메일의 사용자를 찾을 수 없는 경우
     */
    @Override
    public RoleDto findRoleByMember(String userEmail) {
        Member member = memberRepository.findByMbEmail(userEmail).orElseThrow(() -> new EntityNotFoundException("member cannot be found."));

        Role role = member.getRole();

        RoleDto roleDto = RoleDto.builder()
                .roleName(role.getRoleName())
                .build();

        return roleDto;
    }
    /**
     * 특정 역할을 가진 모든 사용자 목록을 조회합니다.
     *
     * 역할 기반 알림 전송 시 해당 역할을 가진 모든 사용자에게
     * 일괄적으로 알림을 전송하기 위해 사용됩니다.
     *
     * @param roleName 조회할 역할명 (예: "ROLE_ADMIN", "ROLE_USER")
     * @return List&lt;Member&gt; 해당 역할을 가진 사용자 목록, 없으면 빈 리스트
     */
    @Override
    public List<Member> findByRole_RoleName(String roleName) {
        List<Member> roleMembers = memberRepository.findByRole_RoleName(roleName);

        if(!roleMembers.isEmpty()) {
            return roleMembers;
        }

        return List.of();
    }
    /**
     * 역할명으로 역할 정보를 조회합니다.
     *
     * 현재 구현에서는 하드코딩된 "ROLE_ADMIN"을 조회하고 있어
     * 매개변수와 실제 조회 로직이 일치하지 않습니다.
     *
     * @param roleName 조회할 역할명 (현재 사용되지 않음)
     * @return Role ROLE_ADMIN 역할 정보
     * @throws EntityNotFoundException ROLE_ADMIN 역할을 찾을 수 없는 경우
     * @deprecated 매개변수와 실제 로직이 일치하지 않아 수정이 필요합니다
     */
    @Override
    public Role findByRoleName(String roleName) {
        Role role = roleRepository.findByRoleName("ROLE_ADMIN").orElseThrow(() -> new EntityNotFoundException("role cannot be found."));

        return role;
    }
    /**
     * 알림 메시지를 저장하고 실시간으로 사용자에게 전송합니다.
     *
     * 이 메서드는 알림 시스템의 핵심 기능으로, 다음과 같은 스마트 알림 처리를 수행합니다:
     * <ol>
     * <li>사용자의 현재 활성 세션 수를 계산</li>
     * <li>세션 수가 2개 이상인 경우 자동으로 읽음 처리 (알림 페이지 접속 중으로 간주)</li>
     * <li>알림 메시지를 데이터베이스에 저장</li>
     * <li>활성 사용자에게 실시간 WebSocket 알림 전송</li>
     * </ol>
     *
     * <p>전송되는 알림 유형:</p>
     * <ul>
     * <li>읽지 않은 알림 개수 업데이트 (세션 수 &lt; 2인 경우)</li>
     * <li>팝업 알림 메시지 (세션 수 &lt; 2인 경우)</li>
     * <li>일반 알림 메시지 (모든 활성 세션)</li>
     * </ul>
     *
     * @param member 알림을 받을 사용자 정보
     * @param role 사용자의 역할 정보
     * @param content 알림 메시지 내용
     */
    @Override
    public void saveNotificationMessage(Member member, Role role ,String content) {

        // 관리자용 구조화된 텍스트로 변환
        String adminFormattedContent = htmlTextConverter.convertToAdminNotification(content);

        // ✅ 현재 사용자의 활성 세션 수 계산
        long sessionCount = notificationSessionTracker.getNotificationSessionIdToUserEmailMap().values()
                .stream()
                .filter(email -> email.equals(member.getMbEmail()))
                .count();

        NotificationMessage notificationMessage = NotificationMessage.builder()
                .member(member)
                .role(role)
                .content(adminFormattedContent) // 관리자가 보기 편한 형태로 저장
                .isRead(sessionCount >= 2) // ✅ 2개 이상 세션 시 즉시 읽음
                .build();

        notificationMessageRepository.save(notificationMessage);

        if(notificationSessionTracker.getNotificationSessionIdToUserEmailMap().containsValue(member.getMbEmail())) {
            // 현재 접속중인 사용자에게만 notification count 및 content 메시지 보냄
            if(sessionCount < 2) {
                // 관리자용 요약 메시지 (팝업용)
                String adminSummary = htmlTextConverter.createAdminSummary(content, 80);

                // 알림 페이지에 접속하지 않았을 때만 안읽은 알림 개수와 팝업 메시지 전송
                Long count = notificationMessageRepository.countByMemberAndIsReadFalse(member);
                messageTemplate.convertAndSend("/notification/unread-notification-count-updates/" + member.getMbEmail(), count);
                messageTemplate.convertAndSend("/notification/notification-message/" + member.getMbEmail(), adminSummary);
            }
            messageTemplate.convertAndSend("/notification/" + member.getMbEmail(), adminFormattedContent);
        }
    }
    /**
     * 특정 사용자의 읽지 않은 알림 개수를 조회합니다.
     *
     * 사용자 인터페이스에서 알림 배지나 카운터를 표시하기 위해 사용됩니다.
     * 실시간으로 업데이트되는 읽지 않은 알림 개수를 제공합니다.
     *
     * @param email 조회할 사용자의 이메일 주소
     * @return Long 읽지 않은 알림의 개수
     * @throws EntityNotFoundException 해당 이메일의 사용자를 찾을 수 없는 경우
     */
    @Override
    public Long getNotificationUnreadCount(String email) {
        Member member = memberRepository.findByMbEmail(email).orElseThrow(() -> new EntityNotFoundException("member cannot be found."));

        Long count = notificationMessageRepository.countByMemberAndIsReadFalse(member);

        return count;
    }
    /**
     * 특정 사용자의 모든 읽지 않은 알림을 읽음 상태로 일괄 처리합니다.
     *
     * 사용자가 알림 목록을 확인하거나 알림 페이지에 접근할 때 호출되어
     * 해당 사용자의 모든 미읽음 알림을 읽음 처리합니다.
     * 이를 통해 알림 카운터가 0으로 리셋됩니다.
     *
     * @param email 읽음 처리할 사용자의 이메일 주소
     * @throws EntityNotFoundException 해당 이메일의 사용자를 찾을 수 없는 경우
     */
    @Override
    public void readNotification(String email) {
        Member member = memberRepository.findByMbEmail(email).orElseThrow(() -> new EntityNotFoundException("member cannot be found."));

        List<NotificationMessage> notificationMessages = notificationMessageRepository.findByMemberAndIsReadFalse(member);

        for(NotificationMessage notificationMessage : notificationMessages) {
            notificationMessage.updateIsRead(true);
        }
    }
    /**
     * 특정 사용자의 모든 알림 히스토리를 조회하여 DTO 형태로 반환합니다.
     *
     * 사용자가 받은 모든 알림(읽음/미읽음 포함)의 목록을 조회하여
     * 클라이언트에서 표시하기 적합한 DTO 형태로 변환합니다.
     * 알림 내용과 생성 시간 정보를 포함하여 반환합니다.
     *
     * @param email 히스토리를 조회할 사용자의 이메일 주소
     * @return List&lt;NotificationMessageDto&gt; 사용자의 알림 히스토리 목록
     * @throws EntityNotFoundException 해당 이메일의 사용자를 찾을 수 없는 경우
     */
    @Override
    public List<NotificationMessageDto> getHistoryNotification(String email) {
        Member member = memberRepository.findByMbEmail(email).orElseThrow(() -> new EntityNotFoundException("member cannot be found."));

        List<NotificationMessage> notificationMessages = notificationMessageRepository.findByMember(member);

        List<NotificationMessageDto> notificationMessageDtos = new ArrayList<>();

        for(NotificationMessage notificationMessage : notificationMessages) {
            NotificationMessageDto notificationMessageDto = NotificationMessageDto.builder()
                    .content(notificationMessage.getContent())
                    .createdAt(notificationMessage.getCreatedAt())
                    .build();
            notificationMessageDtos.add(notificationMessageDto);
        }

        return notificationMessageDtos;
    }
}
