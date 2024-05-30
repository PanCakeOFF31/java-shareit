package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserGatewayControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    private final ItemClient userService;

    private long anyUserId;
    private UserRequestDto userRequestDto;
    private UserResponseDto userResponseDto;

    @BeforeEach
    public void preTestInitialization() {
        anyUserId = 123L;

        userRequestDto = UserRequestDto.builder()
                .name("maxim")
                .email("mak@yandex.ru")
                .build();

        userResponseDto = UserResponseDto.builder()
                .id(anyUserId)
                .name(userRequestDto.getName())
                .email(userRequestDto.getEmail())
                .build();
    }
//
//    @Test
//    public void test_T0020_NS01_updateUser_noPathVariable_userId() throws Exception {
//        mvc.perform(patch("/users/")
//                        .content(mapper.writeValueAsString(userRequestDto))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isMethodNotAllowed());
//    }

    //
//    @ParameterizedTest
//    @MethodSource("giveArgsFor_T0010_NS01")
//    public void test_T0010_NS01_createUser_invalidContent(final String content, final String handler) throws Exception {
//        String response = mvc.perform(post("/users")
//                        .content(content)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andReturn().getResponse().getContentAsString();
//
//        assertTrue(response.contains(handler));
//    }
//
//    private static Stream<Arguments> giveArgsFor_T0010_NS01() {
//        return Stream.of(arguments("", "Required request body is missing"),
//                arguments("{\"name\": \"maxim\"}", "Validation failed for argument"),
//                arguments("{\"email\": \"user@user.com\"}", "Validation failed for argument"));
//    }
}
