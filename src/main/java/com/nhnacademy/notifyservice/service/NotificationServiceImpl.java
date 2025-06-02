package com.nhnacademy.notifyservice.service;

import com.nhnacademy.notifyservice.config.ChatSessionTracker;
import com.nhnacademy.notifyservice.domain.Member;
import com.nhnacademy.notifyservice.domain.NotificationMessage;
import com.nhnacademy.notifyservice.domain.Role;
import com.nhnacademy.notifyservice.dto.NotificationMessageDto;
import com.nhnacademy.notifyservice.dto.RoleDto;
import com.nhnacademy.notifyservice.repository.MemberRepository;
import com.nhnacademy.notifyservice.repository.NotificationMessageRepository;
import com.nhnacademy.notifyservice.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {


    private final NotificationMessageRepository notificationMessageRepository;
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final SimpMessageSendingOperations messageTemplate;
    private final ChatSessionTracker chatSessionTracker;

    public NotificationServiceImpl(NotificationMessageRepository notificationMessageRepository, MemberRepository memberRepository, RoleRepository roleRepository, SimpMessageSendingOperations messageTemplate, ChatSessionTracker chatSessionTracker) {
        this.notificationMessageRepository = notificationMessageRepository;
        this.memberRepository = memberRepository;
        this.roleRepository = roleRepository;
        this.messageTemplate = messageTemplate;
        this.chatSessionTracker = chatSessionTracker;
    }

    @Override
    public RoleDto findRoleByMember(String userEmail) {
        Member member = memberRepository.findByMbEmail(userEmail).orElseThrow(() -> new EntityNotFoundException("member cannot be found."));

        Role role = member.getRole();

        RoleDto roleDto = RoleDto.builder()
                .roleName(role.getRoleName())
                .build();

        System.out.println(roleDto + "gdgd");

        return roleDto;
    }

    @Override
    public List<Member> findByRole_RoleName(String roleName) {
        List<Member> roleMembers = memberRepository.findByRole_RoleName(roleName);

        if(!roleMembers.isEmpty()) {
            return roleMembers;
        }

        return List.of();
    }

    @Override
    public Role findByRoleName(String roleName) {
        Role role = roleRepository.findByRoleName("ROLE_ADMIN").orElseThrow(() -> new EntityNotFoundException("role cannot be found."));

        return role;
    }

    @Override
    public void saveNotificationMessage(Member member, Role role ,String content) {

        // ✅ 현재 사용자의 활성 세션 수 계산
        long sessionCount = chatSessionTracker.getNotificationSessionIdToUserEmailMap().values()
                .stream()
                .filter(email -> email.equals(member.getMbEmail()))
                .count();

        NotificationMessage notificationMessage = NotificationMessage.builder()
                .member(member)
                .role(role)
                .content(content)
                .isRead(sessionCount >= 2) // ✅ 2개 이상 세션 시 즉시 읽음
                .build();

        notificationMessageRepository.save(notificationMessage);

        if(chatSessionTracker.getNotificationSessionIdToUserEmailMap().containsValue(member.getMbEmail())) {
            // 현재 접속중인 사용자에게만 notification count 및 content 메시지 보냄
            if(sessionCount < 2) {
                // 알림 페이지에 접속하지 않았을 때만 안읽은 알림 개수와 팝업 메시지 전송
                Long count = notificationMessageRepository.countByMemberAndIsReadFalse(member);
                messageTemplate.convertAndSend("/notification/unread-notification-count-updates/" + member.getMbEmail(), count);
                messageTemplate.convertAndSend("/notification/notification-message/" + member.getMbEmail(), content);
            }
            messageTemplate.convertAndSend("/notification/" + member.getMbEmail(), content);
        }
    }

    @Override
    public Long getNotificationUnreadCount(String email) {
        Member member = memberRepository.findByMbEmail(email).orElseThrow(() -> new EntityNotFoundException("member cannot be found."));

        Long count = notificationMessageRepository.countByMemberAndIsReadFalse(member);

        return count;
    }

    @Override
    public void readNotification(String email) {
        Member member = memberRepository.findByMbEmail(email).orElseThrow(() -> new EntityNotFoundException("member cannot be found."));

        List<NotificationMessage> notificationMessages = notificationMessageRepository.findByMemberAndIsReadFalse(member);

        for(NotificationMessage notificationMessage : notificationMessages) {
            System.out.println("notificationMessage = " + notificationMessage);
            notificationMessage.updateIsRead(true);
        }
    }

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
