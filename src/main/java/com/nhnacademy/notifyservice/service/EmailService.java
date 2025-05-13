package com.nhnacademy.notifyservice.service;


import com.nhnacademy.notifyservice.dto.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * 이메일 발송 기능을 제공하는 서비스 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender sender;

    /**
     * 일반 텍스트 형식의 이메일을 발송합니다.
     *
     * @param request 이메일 수신자, 제목, 본문 내용을 포함한 요청 객체
     */
    public void sendTextEmail(EmailRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getTo());
        message.setSubject(request.getSubject());
        message.setText(request.getContent());

        sender.send(message);
    }

    /**
     * HTML 형식의 이메일을 발송합니다.
     *
     * @param request 이메일 수신자, 제목, 본문(HTML)을 포함한 요청 객체
     * @throws MessagingException 이메일 생성 또는 발송 중 오류가 발생한 경우
     */
    public void sendHtmlEmail(EmailRequest request) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(request.getTo());
        helper.setSubject(request.getSubject());
        helper.setText(request.getContent(), true); // true는 HTML 형식을 의미

        sender.send(message);
    }
}
