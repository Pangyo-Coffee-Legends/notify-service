//package com.nhnacademy.notifyservice.producer;
//
//import com.nhnacademy.notifyservice.dto.EmailRequest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//
//class EmailQueueProducerTest {
//
//    RabbitTemplate rabbitTemplate;
//    EmailQueueProducer producer;
//
//    @BeforeEach
//    void setUp() {
//        rabbitTemplate = mock();
//        producer = new EmailQueueProducer(rabbitTemplate, "email-queue");
//    }
//
//    @Test
//    @DisplayName("sendTextEmail: type이 TEXT로 설정되고 큐에 전송된다")
//    void sendTextEmail_setsTypeAndSends() {
//        EmailRequest request = new EmailRequest("to@example.com", "제목", "내용");
//        producer.sendTextEmail(request);
//
//        assertEquals("TEXT", request.getType());
//        verify(rabbitTemplate).convertAndSend("email-queue", request);
//    }
//
//    @Test
//    @DisplayName("sendHtmlEmail: type이 HTML로 설정되고 큐에 전송된다")
//    void sendHtmlEmail_setsTypeAndSends() {
//        EmailRequest request = new EmailRequest("to@example.com", "제목", "내용");
//        producer.sendHtmlEmail(request);
//
//        assertEquals("HTML", request.getType());
//        verify(rabbitTemplate).convertAndSend("email-queue", request);
//    }
//}