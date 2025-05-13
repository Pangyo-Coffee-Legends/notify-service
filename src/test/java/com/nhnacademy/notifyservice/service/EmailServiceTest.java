package com.nhnacademy.notifyservice.service;

import com.nhnacademy.notifyservice.dto.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


class EmailServiceTest {
    @Mock
    private JavaMailSender sender;

    @InjectMocks
    private EmailService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("이메일 발송 - text")
    void sendTextEmail() {
        EmailRequest request = new EmailRequest("test@test.com", "이메일 발송(text)", "이메일 발송 테스트");
        service.sendTextEmail(request);

        Mockito.verify(sender, Mockito.times(1)).send(Mockito.any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("이메일 발송 - html")
    void sendHtmlEmail() throws MessagingException {
        Session session = Session.getDefaultInstance(System.getProperties());
        MimeMessage message = new MimeMessage(session);
        Mockito.when(sender.createMimeMessage()).thenReturn(message);

        EmailRequest request = new EmailRequest("test@test.com", "이메일 발송(text)", "<p><b>이메일</b> 발송 테스트</p>");
        service.sendHtmlEmail(request);

        Mockito.verify(sender, Mockito.times(1)).createMimeMessage();
        Mockito.verify(sender, Mockito.times(1)).send(message);
    }
}