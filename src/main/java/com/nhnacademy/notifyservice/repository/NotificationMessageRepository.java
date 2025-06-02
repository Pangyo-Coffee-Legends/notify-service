package com.nhnacademy.notifyservice.repository;

import com.nhnacademy.notifyservice.domain.Member;
import com.nhnacademy.notifyservice.domain.NotificationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationMessageRepository extends JpaRepository<NotificationMessage, Long> {
    Long countByMemberAndIsReadFalse(Member member);

    List<NotificationMessage> findByMember(Member member);

    List<NotificationMessage> findByMemberAndIsReadFalse(Member member);
}
