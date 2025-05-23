package com.nhnacademy.notifyservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ 관련 설정을 담당하는 Configuration 클래스입니다.
 * <p>
 * - 이메일 발송용 큐(email-queue) 및 해당 큐의 Dead Letter Queue(email-queue.dlq) 설정을 포함합니다.
 * - 원본 큐에 메시지 처리 실패 시 Dead Letter Exchange(email-exchange.dlx)로 메시지를 보내고,
 *   DLX는 DLQ로 메시지를 라우팅합니다.
 * </p>
 */
@Configuration
public class RabbitConfig {

    /**
     * 이메일 발송용 큐 이름을 외부 프로퍼티에서 주입받습니다.
     */
    @Value("${email.queue}")
    private String emailQueue;

    /**
     * Dead Letter Queue 이름 상수입니다.
     */
    private static final String EMAIL_DLQ = "email-queue.dlq";

    /**
     * Dead Letter Exchange 이름 상수입니다.
     */
    private static final String EMAIL_DLX = "email-exchange.dlx";

    /**
     * 이메일 발송용 원본 큐를 생성합니다.
     * <p>
     * 이 큐는 durable 하며, 메시지 처리 실패 시 {@link #emailDlx()}로 지정된 DLX로 메시지를 전달합니다.
     * DLX로 전달할 때 사용할 라우팅 키는 {@link #EMAIL_DLQ}입니다.
     * </p>
     *
     * @return durable한 원본 {@link Queue} 빈
     */
    @Bean
    public Queue emailQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", EMAIL_DLX); // DLX 지정
        args.put("x-dead-letter-routing-key", EMAIL_DLQ); // DLQ 라우팅 키 (옵션)
        return new Queue(emailQueue, true, false, false, args); // durable, exclusive, autoDelete, arguments
    }

    /**
     * 이메일 발송용 원본 Direct Exchange를 생성합니다.
     *
     * @return {@link DirectExchange} 빈
     */
    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange("email-exchange");
    }

    /**
     * 원본 큐와 원본 Direct Exchange를 바인딩합니다.
     * <p>
     * 라우팅 키는 "email-routing-key"로 지정됩니다.
     * </p>
     *
     * @param emailQueue    원본 큐 빈
     * @param emailExchange 원본 Direct Exchange 빈
     * @return {@link Binding} 빈
     */
    @Bean
    public Binding binding(Queue emailQueue, DirectExchange emailExchange) {
        return BindingBuilder.bind(emailQueue)
                .to(emailExchange)
                .with("email-routing-key");
    }

    /**
     * Dead Letter Exchange(DLX)를 생성합니다.
     * <p>
     * 메시지 처리 실패 시 원본 큐에서 이 Exchange로 메시지가 전달됩니다.
     * </p>
     *
     * @return {@link DirectExchange} DLX 빈
     */
    @Bean
    public DirectExchange emailDlx() {
        return new DirectExchange(EMAIL_DLX);
    }

    /**
     * Dead Letter Queue(DLQ)를 생성합니다.
     * <p>
     * DLX에서 라우팅된 메시지가 저장되는 큐입니다.
     * </p>
     *
     * @return durable한 DLQ {@link Queue} 빈
     */
    @Bean
    public Queue emailDlq() {
        return new Queue(EMAIL_DLQ, true); // durable
    }

    /**
     * Dead Letter Queue와 Dead Letter Exchange를 바인딩합니다.
     * <p>
     * 라우팅 키는 {@link #EMAIL_DLQ}로 지정됩니다.
     * </p>
     *
     * @param emailDlq Dead Letter Queue 빈
     * @param emailDlx Dead Letter Exchange 빈
     * @return {@link Binding} 빈
     */
    @Bean
    public Binding dlqBinding(Queue emailDlq, DirectExchange emailDlx) {
        return BindingBuilder.bind(emailDlq)
                .to(emailDlx)
                .with(EMAIL_DLQ); // routing key
    }
}
