package com.nhnacademy.notifyservice.controller;


import com.nhnacademy.notifyservice.dto.EmailRequest;
import com.nhnacademy.notifyservice.producer.EmailQueueProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailQueueProducer emailQueueProducer;

    /**
     * 일반 텍스트 이메일을 발송합니다.
     *
     * @param request 이메일 수신자, 제목, 본문 정보를 담은 요청 객체
     * @return 응답 메시지
     */
    @PostMapping("/text")
    public ResponseEntity<String> sendTextEmail(@Validated @RequestBody EmailRequest request) {
        emailQueueProducer.sendTextEmail(request);

        return ResponseEntity.ok("텍스트 이메일이 성공적으로 발송되었습니다.");
    }

    /**
     * HTML 형식의 이메일을 발송합니다.
     *
     * @param request 이메일 수신자, 제목, HTML 본문을 담은 요청 객체
     * @return 응답 메시지
     */
    @PostMapping("/html")
    public ResponseEntity<String> sendHtmlEmail(@Validated @RequestBody EmailRequest request) {
        emailQueueProducer.sendHtmlEmail(request);

        return ResponseEntity.ok("HTML 이메일이 성공적으로 발송되었습니다.");
    }
}
