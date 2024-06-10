package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class ShareItGatewayTests {


    @Test
    void test_T0010_PS01_startMain() {
        assertDoesNotThrow(ShareItGateway::new);
        assertDoesNotThrow(() -> ShareItGateway.main(new String[]{}));
    }

}
