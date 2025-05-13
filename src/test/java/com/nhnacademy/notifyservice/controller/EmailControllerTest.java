package com.nhnacademy.notifyservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.notifyservice.dto.EmailRequest;
import com.nhnacademy.notifyservice.service.EmailService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmailController.class)
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    EmailService emailService;


    @Test
    @DisplayName("이메일 발송 성공 - 텍스트")
    void sendTextEmail() throws Exception {
        EmailRequest request = new EmailRequest("test@test.com", "이메일 발송(text)", "이메일 발송 테스트");
        String body = mapper.writeValueAsString(request);
        doNothing().when(emailService).sendTextEmail(request);

        mockMvc.perform(
                post("/api/v1/email/text")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        )
                .andExpect(status().isOk())
                .andExpect(content().string("텍스트 이메일이 성공적으로 발송되었습니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("이메일 발송 성공 - html")
    void sendHtmlEmail() throws Exception {
        EmailRequest request = new EmailRequest("test@example.com","테스트", "<h1>안녕하세요</h1>");
        String body = mapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/v1/email/html")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("HTML 이메일이 성공적으로 발송되었습니다."));
    }

    @Test
    @DisplayName("이메일 발송 실패 - html")
    void sendHtmlEmail_exception_case() throws Exception {
        EmailRequest request = new EmailRequest("test@example.com","테스트", "<h1>안녕하세요</h1>");
        String body = mapper.writeValueAsString(request);

        doThrow(new MessagingException("SMTP 오류"))
                .when(emailService).sendHtmlEmail(Mockito.any(EmailRequest.class));

        mockMvc.perform(
                post("/api/v1/email/html")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("이메일 발송 중 오류가 발생했습니다: SMTP 오류"));
    }

}