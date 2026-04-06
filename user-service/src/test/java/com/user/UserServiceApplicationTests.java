package com.user;

import com.user.repository.PasswordResetAuditRepository;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceApplicationTests {

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @MockBean
    private PasswordResetAuditRepository passwordResetAuditRepository;

    @MockBean
    private RuntimeService runtimeService;

    @MockBean
    private HistoryService historyService;

    @Test
    void contextLoads() {
    }
}