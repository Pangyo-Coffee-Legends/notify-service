package com.nhnacademy.notifyservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "email.queue=email-queue")
class NotifyServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
