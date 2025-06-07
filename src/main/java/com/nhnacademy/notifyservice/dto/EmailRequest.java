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
    private String roleType; // "ROLE_ADMIN" 또는 "ROLE_ALL"
    private String type; // "TEXT" 또는 "HTML"
}
