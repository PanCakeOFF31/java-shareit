package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.ReqCreateDto;
import ru.practicum.shareit.request.dto.ReqGetDto;
import ru.practicum.shareit.request.dto.ReqRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestControllerTest {
    private final MockMvc mvc;
    @MockBean
    private RequestService requestService;
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
    public void test_T0010_PS01_createRequest() throws Exception {
        Mockito.when(requestService.createRequest(any(), anyLong()))
                .thenReturn(reqCreateDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(reqRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyRequesterId))
                .andExpect(status().isCreated())
                .andExpect(content().string(mapper.writeValueAsString(reqCreateDto)))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$.id").value(anyRequestId))
                .andExpect(jsonPath("$.description", is(reqCreateDto.getDescription())))
                .andExpect(jsonPath("$.created", is(reqCreateDto.getCreated().toString())));

        Mockito.verify(requestService, Mockito.times(1)).createRequest(any(), anyLong());
        Mockito.verifyNoMoreInteractions(requestService);
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

    @Test
    public void test_T0010_NS02_createRequest_noRequestHeader_requesterId() throws Exception {
        String response = mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(reqRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Required request header 'X-Sharer-User-Id'"));
    }

    @Test
    public void test_T0010_NS03_createRequest_invalidRequestHeader_requesterId() throws Exception {
        String response = mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(reqRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, "text instead long"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }

    @Test
    public void test_T0020_PS01_getRequestForRequester() throws Exception {
        ReqGetDto request1 = reqGetDto.toBuilder().build();
        ReqGetDto request2 = reqGetDto.toBuilder().build();

        List<ReqGetDto> result = List.of(request1, request2);

        Mockito.when(requestService.getRequestsByRequester(anyLong(), anyInt(), anyInt()))
                .thenReturn(result);

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .header(REQUEST_USER_HEADER, anyRequesterId))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(result)))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(anyRequestId))
                .andExpect(jsonPath("$.[1].description", is(reqGetDto.getDescription())))
                .andExpect(jsonPath("$.[0].created", is(reqGetDto.getCreated().toString())))
                .andExpect(jsonPath("$.[1].items.length()").value(0));

        Mockito.verify(requestService, Mockito.times(1)).getRequestsByRequester(anyLong(), anyInt(), anyInt());
        Mockito.verifyNoMoreInteractions(requestService);
    }

    @Test
    public void test_T0020_PS02_getRequestForRequester_defaultParameters() throws Exception {
        ReqGetDto request1 = reqGetDto.toBuilder().build();
        ReqGetDto request2 = reqGetDto.toBuilder().build();

        List<ReqGetDto> result = List.of(request1, request2);

        Mockito.when(requestService.getRequestsByRequester(anyLong(), anyInt(), anyInt()))
                .thenReturn(result);

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyRequesterId))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(result)))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(anyRequestId))
                .andExpect(jsonPath("$.[1].description", is(reqGetDto.getDescription())))
                .andExpect(jsonPath("$.[0].created", is(reqGetDto.getCreated().toString())))
                .andExpect(jsonPath("$.[1].items.length()").value(0));

        Mockito.verify(requestService, Mockito.times(1)).getRequestsByRequester(anyLong(), anyInt(), anyInt());
        Mockito.verifyNoMoreInteractions(requestService);
    }

    @Test
    public void test_T0020_NS01_getRequestForRequester_noRequestHeader_requesterId() throws Exception {
        String response = mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Required request header 'X-Sharer-User-Id'"));
    }

    @Test
    public void test_T0020_NS02_getRequestForRequester_invalidRequestHeader_requesterId() throws Exception {
        String response = mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .header(REQUEST_USER_HEADER, "text instead long"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }

    @Test
    public void test_T0030_PS01_getAllRequests_() throws Exception {
        ReqGetDto request1 = reqGetDto.toBuilder().build();
        ReqGetDto request2 = reqGetDto.toBuilder().build();

        List<ReqGetDto> result = List.of(request1, request2);

        Mockito.when(requestService.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(result);

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .header(REQUEST_USER_HEADER, anyRequesterId))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(result)))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(anyRequestId))
                .andExpect(jsonPath("$.[1].description", is(reqGetDto.getDescription())))
                .andExpect(jsonPath("$.[0].created", is(reqGetDto.getCreated().toString())))
                .andExpect(jsonPath("$.[1].items.length()").value(0));

        Mockito.verify(requestService, Mockito.times(1)).getAll(anyLong(), anyInt(), anyInt());
        Mockito.verifyNoMoreInteractions(requestService);
    }

    @Test
    public void test_T0030_PS02_getAllRequests_defaultParameters() throws Exception {
        ReqGetDto request1 = reqGetDto.toBuilder().build();
        ReqGetDto request2 = reqGetDto.toBuilder().build();

        List<ReqGetDto> result = List.of(request1, request2);

        Mockito.when(requestService.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(result);

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyRequesterId))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(result)))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(anyRequestId))
                .andExpect(jsonPath("$.[1].description", is(reqGetDto.getDescription())))
                .andExpect(jsonPath("$.[0].created", is(reqGetDto.getCreated().toString())))
                .andExpect(jsonPath("$.[1].items.length()").value(0));

        Mockito.verify(requestService, Mockito.times(1)).getAll(anyLong(), anyInt(), anyInt());
        Mockito.verifyNoMoreInteractions(requestService);
    }

    @Test
    public void test_T0030_NS01_getAllRequests_noRequestHeader_requesterId() throws Exception {
        String response = mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Required request header 'X-Sharer-User-Id'"));
    }

    @Test
    public void test_T0030_NS02_getAllRequests_invalidRequestHeader_requesterId() throws Exception {
        String response = mvc.perform(get("/requests/all")
                        .content(mapper.writeValueAsString(reqRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .header(REQUEST_USER_HEADER, "text instead long"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }

    @Test
    public void test_T0040_PS01_getRequestByRequestId_() throws Exception {
        Mockito.when(requestService.getRequestDtoById(anyLong(), anyLong()))
                .thenReturn(reqGetDto);

        mvc.perform(get("/requests/" + anyRequestId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyRequesterId))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(reqGetDto)))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$.id").value(anyRequestId))
                .andExpect(jsonPath("$.description", is(reqCreateDto.getDescription())))
                .andExpect(jsonPath("$.created", is(reqCreateDto.getCreated().toString())));

        Mockito.verify(requestService, Mockito.times(1)).getRequestDtoById(anyLong(), anyLong());
        Mockito.verifyNoMoreInteractions(requestService);
    }

    @Test
    public void test_T0040_NS01_getRequestBuRequestId_noRequestHeader_requesterId() throws Exception {
        String response = mvc.perform(get("/requests/" + anyRequestId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Required request header 'X-Sharer-User-Id'"));
    }

    @Test
    public void test_T0040_NS02_getRequestBuRequestId_invalidRequestHeader_requesterId() throws Exception {
        String response = mvc.perform(get("/requests/" + anyRequestId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, "text instead long"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }

    @Test
    @Disabled
    public void test_T0040_NS03_getRequestBuRequestId_noPathVariable_requestId() throws Exception {
//        Нельзя проверить, есть ednpoint
    }

    @Test
    public void test_T0040_NS04_getRequestBuRequestId_invalidPathVariable_requestId() throws Exception {
        String response = mvc.perform(get("/requests/9*sad")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, String.valueOf(anyRequestId)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));

    }
}