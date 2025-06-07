package com.nhnacademy.notifyservice.service;

import com.nhnacademy.notifyservice.domain.Member;
import com.nhnacademy.notifyservice.domain.Role;
import com.nhnacademy.notifyservice.dto.EmailRequest;
import com.nhnacademy.notifyservice.dto.NotificationMessageDto;
import com.nhnacademy.notifyservice.dto.RoleDto;

import java.util.List;

public interface NotificationService {
    RoleDto findRoleByMember(String userEmail);

    List<Member> findByRole_RoleName(String roleName);

    Role findByRoleName(String roleName);

    void saveNotificationMessage(Member member, Role role , EmailRequest request);

    Long getNotificationUnreadCount(String email);

    void readNotification(String email);

    List<NotificationMessageDto> getHistoryNotification(String email);

    Member findMemberByEmail(String extractedEmail);

    void sendNotification(Member member, String content);
}
