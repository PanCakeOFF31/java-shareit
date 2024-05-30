package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ReqCreateDto;
import ru.practicum.shareit.request.dto.ReqGetDto;
import ru.practicum.shareit.request.dto.ReqRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO: реализовать тесты для Gateway Controller
@WebMvcTest(controllers = RequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestGatewayControllerTest {
    private final MockMvc mvc;
    @MockBean
    private RequestClient requestClient;
    private final ObjectMapper mapper;
    private static final String REQUEST_USER_HEADER = "X-Sharer-User-Id";
    private ReqRequestDto reqRequestDto;
    private ReqCreateDto reqCreateDto;
    private ReqGetDto reqGetDto;
    private long anyRequestId;
    private long anyRequesterId;
    private LocalDateTime ldt;

    @BeforeEach
    public void preTestInitialization() throws Exception {
        anyRequestId = 32L;
        anyRequesterId = 123L;

        ldt = LocalDateTime.of(2020, 10, 12, 15, 45, 30);

        reqRequestDto = ReqRequestDto.builder().description("any description text").build();

        reqCreateDto = ReqCreateDto.builder()
                .id(anyRequestId)
                .description(reqRequestDto.getDescription())
                .created(ldt)
                .build();

        reqGetDto = ReqGetDto.builder()
                .id(anyRequestId)
                .description(reqRequestDto.getDescription())
                .created(ldt)
                .items(new ArrayList<>())
                .build();
    }

    @Test
    public void test_T0010_NS01_createRequest_invalidContent() throws Exception {
        String response = mvc.perform(post("/requests")
                        .content("{}")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyRequesterId))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Validation failed for argument"));
    }

}
