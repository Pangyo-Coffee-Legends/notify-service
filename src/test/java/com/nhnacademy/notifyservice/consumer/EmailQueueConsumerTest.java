package com.nhnacademy.notifyservice.consumer;

import com.nhnacademy.notifyservice.dto.EmailRequest;
import com.nhnacademy.notifyservice.service.EmailService;
import com.nhnacademy.notifyservice.service.NotificationServiceImpl;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EmailQueueConsumerTest {

    EmailService emailService;
    NotificationServiceImpl notificationService;
    EmailQueueConsumer consumer;


    @BeforeEach
    void setUp() {
        emailService = mock(EmailService.class);
        notificationService = mock(NotificationServiceImpl.class);
        consumer = new EmailQueueConsumer(emailService, notificationService);
    }

    @Test
    @DisplayName("HTML 타입이면 sendHtmlEmail이 호출된다")
    void receiveEmailRequest_htmlType_callsSendHtmlEmail() throws Exception {
        EmailRequest request = new EmailRequest("to@example.com", "제목", "<b>내용</b>");
        request.setType("HTML");

        consumer.receiveEmailRequest(request);

        verify(emailService).sendHtmlEmail(request);
        verify(emailService, never()).sendTextEmail(any());
    }

    @Test
    @DisplayName("TEXT 타입이면 sendTextEmail이 호출된다")
    void receiveEmailRequest_textType_callsSendTextEmail() throws MessagingException {
        EmailRequest request = new EmailRequest("to@example.com", "제목", "내용");
        request.setType("TEXT");

        consumer.receiveEmailRequest(request);

        verify(emailService).sendTextEmail(request);
        verify(emailService, never()).sendHtmlEmail(any());
    }

    @Test
    @DisplayName("type이 null이어도 sendTextEmail이 호출된다")
    void receiveEmailRequest_nullType_callsSendTextEmail() throws MessagingException {
        EmailRequest request = new EmailRequest("to@example.com", "제목", "내용");

        consumer.receiveEmailRequest(request);

        verify(emailService).sendTextEmail(request);
        verify(emailService, never()).sendHtmlEmail(any());
    }

    @Test
    @DisplayName("이메일 발송 중 예외 발생 시 DLQ 이동 예외를 던지고 로그를 남긴다")
    void receiveEmailRequest_sendEmailThrowsException_throwsAmqpRejectAndDontRequeueException() throws Exception {
        EmailRequest request = new EmailRequest("to@example.com", "제목", "내용", "HTML");
        doThrow(new RuntimeException("메일 발송 실패")).when(emailService).sendHtmlEmail(request);

        assertThrows(AmqpRejectAndDontRequeueException.class, () -> consumer.receiveEmailRequest(request));

        verify(emailService).sendHtmlEmail(request);
    }
}