package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    private final UserService userService;

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

    @Test
    public void test_T0010_PS01_createUser() throws Exception {
        Mockito.when(userService.createUser(any(UserRequestDto.class)))
                .thenReturn(userResponseDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(mapper.writeValueAsString(userResponseDto)))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$.id").value(anyUserId))
                .andExpect(jsonPath("$.name", is(userRequestDto.getName())))
                .andExpect(jsonPath("$.email", is(userRequestDto.getEmail())));

        Mockito.verify(userService, Mockito.times(1)).createUser(any(UserRequestDto.class));
        Mockito.verifyNoMoreInteractions(userService);
    }

    @ParameterizedTest
    @MethodSource("giveArgsFor_T0010_NS01")
    public void test_T0010_NS01_createUser_invalidContent(final String content, final String handler) throws Exception {
        String response = mvc.perform(post("/users")
                        .content(content)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains(handler));
    }

    private static Stream<Arguments> giveArgsFor_T0010_NS01() {
        return Stream.of(arguments("", "Required request body is missing"),
                arguments("{\"name\": \"maxim\"}", "Validation failed for argument"),
                arguments("{\"email\": \"user@user.com\"}", "Validation failed for argument"));
    }

    @Test
    public void test_T0020_PS01_updateUser() throws Exception {
        Mockito.when(userService.updateUser(userRequestDto, anyUserId))
                .thenReturn(userResponseDto);

        mvc.perform(patch("/users/" + anyUserId)
                        .content(mapper.writeValueAsString(userRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$.id").value(anyUserId))
                .andExpect(jsonPath("$.name", is(userRequestDto.getName())))
                .andExpect(jsonPath("$.email", is(userRequestDto.getEmail())));

        Mockito.verify(userService, Mockito.times(1)).updateUser(userRequestDto, anyUserId);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    public void test_T0020_PS02_updateUser_withJsonString() throws Exception {
        String content = "{\"name\": \"maxim\", \"email\": \"maxim@yandex.com\"}";

        mvc.perform(patch("/users/1")
                        .content(content)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void test_T0020_NS01_updateUser_noPathVariable_userId() throws Exception {
        mvc.perform(patch("/users/")
                        .content(mapper.writeValueAsString(userRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void test_T0020_NS02_updateUser_invalidPathVariable_userId() throws Exception {
        String response = mvc.perform(patch("/users/a8*")
                        .content(mapper.writeValueAsString(userRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }

    @Test
    public void test_T0020_NS03_updateUser_invalidContent_emptyBody() throws Exception {
        mvc.perform(patch("/users/1")
                        .content("")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    //    Валидации нет на уровне контроллера при обновлении
    @ParameterizedTest
    @ValueSource(strings = {
            "{\"email\": \"user@user.com\"}",
            "{\"email\": \"user@user.com\"}"
    })
    public void test_T0020_NS04_updateUser_invalidContent_missingField(final String content) throws Exception {
        mvc.perform(patch("/users/1")
                        .content(content)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void test_T0030_PS01_getUserById() throws Exception {
        Mockito.when(userService.getUserResponseDtoById(anyUserId))
                .thenReturn(userResponseDto);

        mvc.perform(get("/users/" + anyUserId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$.id").value(anyUserId))
                .andExpect(jsonPath("$.name", is(userResponseDto.getName())))
                .andExpect(jsonPath("$.email", is(userResponseDto.getEmail())));

        Mockito.verify(userService, Mockito.times(1)).getUserResponseDtoById(anyUserId);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    public void test_T0030_NS02_getUserById_invalidPathVariable_userId() throws Exception {
        String response = mvc.perform(get("/users/897asd")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }

    @Test
    public void test_T0040_PS01_deleteUserById() throws Exception {
        Mockito.when(userService.deleteUserById(anyUserId))
                .thenReturn(userResponseDto);

        mvc.perform(delete("/users/" + anyUserId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$.id").value(anyUserId))
                .andExpect(jsonPath("$.name", is(userResponseDto.getName())))
                .andExpect(jsonPath("$.email", is(userResponseDto.getEmail())));

        Mockito.verify(userService, Mockito.times(1)).deleteUserById(anyUserId);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    public void test_T0040_NS01_deleteUserById_noPathVariable_userId() throws Exception {
        mvc.perform(delete("/users/")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void test_T0040_NS02_deleteUserById_invalidPathVariable_userId() throws Exception {
        String response = mvc.perform(get("/users/897asd")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }

    @Test
    public void test_T0050_PS01_getUsers() throws Exception {
        UserResponseDto user1 = userResponseDto.toBuilder().build();
        UserResponseDto user2 = userResponseDto.toBuilder().build();
        UserResponseDto user3 = userResponseDto.toBuilder().build();

        Mockito.when(userService.getAll())
                .thenReturn(List.of(user1, user2, user3));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));

        Mockito.verify(userService, Mockito.times(1)).getAll();
        Mockito.verifyNoMoreInteractions(userService);
    }
}