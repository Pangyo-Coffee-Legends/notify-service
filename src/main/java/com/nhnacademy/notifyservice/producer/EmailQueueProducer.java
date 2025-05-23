package com.nhnacademy.notifyservice.producer;

import com.nhnacademy.notifyservice.dto.EmailRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 이메일 발송 요청을 RabbitMQ 큐에 비동기적으로 전달하는 프로듀서 서비스입니다.
 * <p>
 * - 텍스트/HTML 이메일 요청을 각각 큐에 전송할 수 있습니다.
 * - 큐 이름은 application.properties의 email.queue 프로퍼티로 주입받습니다.
 * </p>
 */
@Service
public class EmailQueueProducer {

    /**
     * RabbitMQ와의 메시지 송수신을 담당하는 템플릿 객체입니다.
     */
    private final RabbitTemplate rabbitTemplate;

    /**
     * 이메일 발송 요청을 전송할 큐 이름입니다.
     */
    private final String emailQueue;

    /**
     * 생성자.
     *
     * @param rabbitTemplate RabbitMQ 메시지 송수신 템플릿
     * @param emailQueue     큐 이름 (application.properties의 email.queue에서 주입)
     */
    public EmailQueueProducer(RabbitTemplate rabbitTemplate, @Value("${email.queue}") String emailQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.emailQueue = emailQueue;
    }

    /**
     * 텍스트 이메일 발송 요청을 큐에 전송합니다.
     * <p>
     * EmailRequest의 type 필드를 "TEXT"로 설정한 뒤 큐에 전송합니다.
     * </p>
     *
     * @param request 이메일 발송 요청 정보
     */
    public void sendTextEmail(EmailRequest request) {
        request.setType("TEXT");
        rabbitTemplate.convertAndSend(emailQueue, request);
    }

    /**
     * HTML 이메일 발송 요청을 큐에 전송합니다.
     * <p>
     * EmailRequest의 type 필드를 "HTML"로 설정한 뒤 큐에 전송합니다.
     * </p>
     *
     * @param request 이메일 발송 요청 정보
     */
    public void sendHtmlEmail(EmailRequest request) {
        request.setType("HTML");
        rabbitTemplate.convertAndSend(emailQueue, request);
    }
}
