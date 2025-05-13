package com.nhnacademy.notifyservice.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class EmailRequest {

    private String to;
    private String subject;
    private String content;

}
