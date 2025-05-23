package com.nhnacademy.notifyservice.dto;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EmailRequest implements Serializable {

    private String to;
    private String subject;
    private String content;
    private String type; // "TEXT" 또는 "HTML"

    public EmailRequest(String to, String subject, String content) {
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.type = null;
    }
}
