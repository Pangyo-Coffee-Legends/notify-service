package com.nhnacademy.notifyservice.consumer;

import com.nhnacademy.notifyservice.domain.Member;
import com.nhnacademy.notifyservice.domain.Role;
import com.nhnacademy.notifyservice.dto.EmailRequest;
import com.nhnacademy.notifyservice.service.EmailService;
import com.nhnacademy.notifyservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RabbitMQ 큐로부터 이메일 발송 요청을 비동기적으로 수신하고 처리하는 Consumer 서비스입니다.
 * <p>
 * - 큐에서 수신한 EmailRequest의 type에 따라 텍스트 또는 HTML 이메일을 발송합니다.
 * - 발송 성공/실패를 로깅하며, 실패 시 메시지를 Dead Letter Queue(DLQ)로 이동시킵니다.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailQueueConsumer {

    /**
     * 실제 이메일 발송을 담당하는 서비스입니다.
     */
    private final EmailService emailService;
    private final NotificationService notificationService;

    /**
     * RabbitMQ 큐에서 EmailRequest 메시지를 수신하여 이메일을 발송합니다.
     * <p>
     * - type이 "HTML"이면 HTML 이메일을, 그 외에는 텍스트 이메일을 발송합니다.
     * - 발송 성공 시 info 로그를 남기고, 실패 시 error 로그를 남긴 뒤
     *   AmqpRejectAndDontRequeueException을 throw하여 메시지를 DLQ로 이동시킵니다.
     * </p>
     *
     * @param request 큐로부터 수신한 이메일 발송 요청 정보
     * @throws AmqpRejectAndDontRequeueException 이메일 발송 실패 시 DLQ로 메시지 이동
     */
    @RabbitListener(queues = "${email.queue}")
    public void receiveEmailRequest(EmailRequest request) {

        List<Member> admins = notificationService.findByRole_RoleName("ROLE_ADMIN");
        Role role = notificationService.findByRoleName("ROLE_ADMIN");

        try{
            if(request.getRoleType().equals("ROLE_ADMIN")) {
                // 관리자 메시지
                for(Member admin : admins) {
                    // 큐에서 메시지 꺼낸 후 저장하고 프런트로 전송 (관리자는 모든 알림 메시지 저장)
                    notificationService.saveNotificationMessage(admin, role, request);
                }

                if (request.getType().equals("HTML")) {
                    // 이메일로 HTML 메시지 전송
                    emailService.sendHtmlEmail(request);
                } else if (request.getType().equals("TEXT")) {
                    // 이메일로 텍스트 메시지 전송
                    emailService.sendTextEmail(request);
                }

                log.info("이메일 발송 성공 : {}", request);
            } else if(request.getRoleType().equals("ROLE_ALL")) {
                // 팝업 메시지
                String toEmail = request.getTo();

                Member member = notificationService.findMemberByEmail(toEmail);

                notificationService.sendNotification(member, request.getContent());
            }
        }  catch (Exception e) {
            log.error("이메일 발송 실패 : {}", request, e);

            // Slack 등 실시간 알림 연동 기능
            throw new AmqpRejectAndDontRequeueException("DLQ로 이동", e);
        }
    }
}
