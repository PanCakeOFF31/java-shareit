package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class ShareItServerTests {

    @Test
    void test_T0010_PS01_startMain() {
        assertDoesNotThrow(ShareItServer::new);
        assertDoesNotThrow(() -> ShareItServer.main(new String[]{}));
    }
}
