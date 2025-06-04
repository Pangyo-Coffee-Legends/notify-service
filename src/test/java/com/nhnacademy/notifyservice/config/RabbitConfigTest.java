package com.nhnacademy.notifyservice.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = "email.queue=email-queue")
@SpringBootTest
class RabbitConfigTest {

    @Autowired
    private Queue emailQueue;

    @Autowired
    private DirectExchange emailExchange;

    @Autowired
    private Binding binding;

    @Autowired
    private Queue emailDlq;

    @Autowired
    private DirectExchange emailDlx;

    @Autowired
    private Binding dlqBinding;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Test
    @DisplayName("이메일 발송용 원본 큐가 올바르게 생성되고 DLX/DLQ 설정이 적용된다")
    void testEmailQueue() {
        assertNotNull(emailQueue);
        assertEquals("email-queue", emailQueue.getName());
        assertTrue(emailQueue.isDurable());

        // DLX/DLQ arguments 확인
        Map<String, Object> args = emailQueue.getArguments();
        assertNotNull(args);
        assertEquals("email-exchange.dlx", args.get("x-dead-letter-exchange"));
        assertEquals("email-queue.dlq", args.get("x-dead-letter-routing-key"));
    }

    @Test
    @DisplayName("이메일 발송용 원본 익스체인지가 올바르게 생성된다")
    void testEmailExchange() {
        assertNotNull(emailExchange);
        assertEquals("email-exchange", emailExchange.getName());
    }

    @Test
    @DisplayName("원본 큐와 익스체인지 바인딩이 올바르게 생성된다")
    void testBinding() {
        assertNotNull(binding);
        assertEquals(emailExchange.getName(), binding.getExchange());
        assertEquals(emailQueue.getName(), binding.getDestination());
        assertEquals("email-routing-key", binding.getRoutingKey());
    }

    @Test
    @DisplayName("emailQueue 빈이 정상적으로 생성된다")
    void testQueue() {
        assertNotNull(emailQueue);
        assertEquals("email-queue", emailQueue.getName());
        assertTrue(emailQueue.isDurable());
    }

    @Test
    @DisplayName("DLQ와 DLX가 올바르게 생성된다")
    void testDlqAndDlx() {
        assertNotNull(emailDlq);
        assertEquals("email-queue.dlq", emailDlq.getName());
        assertTrue(emailDlq.isDurable());

        assertNotNull(emailDlx);
        assertEquals("email-exchange.dlx", emailDlx.getName());
    }

    @Test
    @DisplayName("DLQ와 DLX 바인딩이 올바르게 생성된다")
    void testDlqBinding() {
        assertNotNull(dlqBinding);
        assertEquals(emailDlx.getName(), dlqBinding.getExchange());
        assertEquals(emailDlq.getName(), dlqBinding.getDestination());
        assertEquals("email-queue.dlq", dlqBinding.getRoutingKey());
    }
}