package com.nhnacademy.notifyservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.notifyservice.dto.EmailRequest;
import com.nhnacademy.notifyservice.producer.EmailQueueProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmailController.class)
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailQueueProducer emailQueueProducer;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("텍스트 이메일 발송 API는 200 OK와 성공 메시지를 반환한다")
    void sendTextEmail_returnsOk() throws Exception {
        EmailRequest request = new EmailRequest("test@example.com", "테스트", "내용");

        mockMvc.perform(post("/api/v1/email/text")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("텍스트 이메일이 성공적으로 발송되었습니다."));

        verify(emailQueueProducer, times(1)).sendTextEmail(Mockito.any(EmailRequest.class));
    }

    @Test
    @DisplayName("HTML 이메일 발송 API는 200 OK와 성공 메시지를 반환한다")
    void sendHtmlEmail_returnsOk() throws Exception {
        EmailRequest request = new EmailRequest("test@example.com", "테스트", "<b>내용</b>");

        mockMvc.perform(post("/api/v1/email/html")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("HTML 이메일이 성공적으로 발송되었습니다."));

        verify(emailQueueProducer, times(1)).sendHtmlEmail(Mockito.any(EmailRequest.class));
    }
}