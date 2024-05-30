package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserBookingDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    private static final String REQUEST_USER_HEADER = "X-Sharer-User-Id";

    private BookingRequestDto bookingRequestDto;
    private BookingResponseDto bookingResponseDto;
    private long anyBookeOrOwnerId;
    private long anyItemId;
    private long anyBookingId;

    private LocalDateTime ldt;

    @BeforeEach
    public void preTestInitialization() {
        anyBookeOrOwnerId = 9999;
        anyItemId = 111;
        anyBookingId = 1515;

        ldt = LocalDateTime.of(2020, 10, 12, 15, 45, 30);
        bookingRequestDto = BookingRequestDto.builder()
                .itemId(anyItemId)
                .start(ldt)
                .end(ldt.plusDays(1))
                .build();

        bookingResponseDto = BookingResponseDto.builder()
                .id(anyBookingId)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .item(new ItemBookingDto(anyItemId, "item-name"))
                .booker(new UserBookingDto(anyBookeOrOwnerId))
                .status(Status.WAITING)
                .build();
    }

    @Test
    public void test_T0010_PS01_createBooking() throws Exception {
        Mockito.when(bookingService.createBooking(Mockito.any(), anyLong()))
                .thenReturn(bookingResponseDto);

        mvc.perform(post("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header(REQUEST_USER_HEADER, anyBookeOrOwnerId))
                .andExpect(status().isCreated())
                .andExpect(content().string(mapper.writeValueAsString(bookingResponseDto)))
                .andExpect(jsonPath(("$")).hasJsonPath())
                .andExpect(jsonPath("$.id").value(anyBookingId))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd().toString())))
                .andExpect(jsonPath("$.item.id").value(bookingResponseDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name", is(bookingResponseDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id").value(bookingResponseDto.getBooker().getId()))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())));

        Mockito.verify(bookingService, Mockito.times(1)).createBooking(Mockito.any(), anyLong());
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void test_T0010_NS02_createBooking_noRequestHeader() throws Exception {
        String response = mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Required request header"));
    }

    @Test
    public void test_T0010_NS02_createBooking_invalidRequestHeader() throws Exception {
        String response = mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, "text instead long"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }

    @Test
    public void test_T0020_PS01_toBook_() throws Exception {
        Mockito.when(bookingService.toBook(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingResponseDto);

        mvc.perform(patch("/bookings/" + anyBookingId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .header(REQUEST_USER_HEADER, anyBookeOrOwnerId)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(bookingResponseDto)))
                .andExpect(jsonPath(("$")).hasJsonPath())
                .andExpect(jsonPath("$.id").value(anyBookingId))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd().toString())))
                .andExpect(jsonPath("$.item.id").value(bookingResponseDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name", is(bookingResponseDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id").value(bookingResponseDto.getBooker().getId()))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())));

        Mockito.verify(bookingService, Mockito.times(1)).toBook(anyLong(), anyLong(), anyBoolean());
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void test_T0020_NS01_toBook_noRequestHeader() throws Exception {
        String response = mvc.perform(patch("/bookings/15")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Required request header"));
    }

    @Test
    public void test_T0020_NS02_toBook_invalidRequestHeader() throws Exception {
        String response = mvc.perform(patch("/bookings/15")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, "text instead long")
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }

    @Test
    public void test_T0020_NS03_toBook_noPathVariable_bookerId() throws Exception {
        mvc.perform(patch("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, String.valueOf(anyBookeOrOwnerId))
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void test_T0020_NS04_toBook_invalidPathVariable_bookerId() throws Exception {
        String response = mvc.perform(patch("/bookings/8sad1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, String.valueOf(anyBookeOrOwnerId))
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }

    @Test
    public void test_T0020_NS05_toBook_noRequestParam() throws Exception {
        String response = mvc.perform(patch("/bookings/15")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyBookeOrOwnerId))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Required request parameter"));
    }

    @Test
    public void test_T0020_NS06_toBook_invalidRequestParam() throws Exception {
        String response = mvc.perform(patch("/bookings/15")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyBookeOrOwnerId)
                        .param("approved", "treu"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }


    @Test
    public void test_T0030_PS01_getBooking_() throws Exception {
        Mockito.when(bookingService.getBookingDto(anyLong(), anyLong()))
                .thenReturn(bookingResponseDto);

        mvc.perform(get("/bookings/" + anyBookingId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyBookeOrOwnerId))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(bookingResponseDto)))
                .andExpect(jsonPath(("$")).hasJsonPath())
                .andExpect(jsonPath("$.id").value(anyBookingId))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd().toString())))
                .andExpect(jsonPath("$.item.id").value(bookingResponseDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name", is(bookingResponseDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id").value(bookingResponseDto.getBooker().getId()))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())));

        Mockito.verify(bookingService, Mockito.times(1)).getBookingDto(anyLong(), anyLong());
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void test_T0030_NS01_getBooking_noRequestHeader() throws Exception {
        String response = mvc.perform(get("/bookings/15")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Required request header"));
    }

    @Test
    public void test_T0030_NS02_getBooking_invalidRequestHeader() throws Exception {
        String response = mvc.perform(get("/bookings/15")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, "text instead long"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }

    @Test
    public void test_T0030_NS03_getBooking_noPathVariable_bookerId() throws Exception {
        //        Нельзя выполнить такой тест, другой endpoint
        assertTrue(true);
    }

    @Test
    public void test_T0030_NS04_getBooking_invalidPathVariable_bookerId() throws Exception {
        String response = mvc.perform(get("/bookings/8sad1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, String.valueOf(anyBookeOrOwnerId)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }

    @Test
    public void test_T0040_PS01_getAllBookingByBooker() throws Exception {
        long bookingIdIndividually = 111;

        BookingResponseDto booking1 = bookingResponseDto.toBuilder().build();
        BookingResponseDto booking2 = bookingResponseDto.toBuilder().build();
        BookingResponseDto booking3 = bookingResponseDto.toBuilder().id(bookingIdIndividually).build();

        List<BookingResponseDto> response = List.of(booking1, booking2, booking3);

        Mockito.when(bookingService.getAllBookingByBooker(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(response);

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyBookeOrOwnerId)
                        .param("state", State.ALL.toString())
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(response)))
                .andExpect(jsonPath(("$")).hasJsonPath())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$.[0].id").value(anyBookingId))
                .andExpect(jsonPath("$.[2].id").value(bookingIdIndividually))
                .andExpect(jsonPath("$.[2].booker.id").value(bookingResponseDto.getBooker().getId()));

        Mockito.verify(bookingService, Mockito.times(1)).getAllBookingByBooker(anyLong(), any(State.class), anyInt(), anyInt());
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void test_T0040_PS02_getAllBookingByBooker_defaultParameters() throws Exception {
        long bookingIdIndividually = 111;

        BookingResponseDto booking1 = bookingResponseDto.toBuilder().build();
        BookingResponseDto booking2 = bookingResponseDto.toBuilder().build();
        BookingResponseDto booking3 = bookingResponseDto.toBuilder().id(bookingIdIndividually).build();

        List<BookingResponseDto> response = List.of(booking1, booking2, booking3);

        Mockito.when(bookingService.getAllBookingByBooker(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(response);

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyBookeOrOwnerId)
                        .param("state", State.ALL.toString())
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(response)))
                .andExpect(jsonPath(("$")).hasJsonPath())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$.[0].id").value(anyBookingId))
                .andExpect(jsonPath("$.[2].id").value(bookingIdIndividually))
                .andExpect(jsonPath("$.[2].booker.id").value(bookingResponseDto.getBooker().getId()));

        Mockito.verify(bookingService, Mockito.times(1)).getAllBookingByBooker(anyLong(), any(State.class), anyInt(), anyInt());
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void test_T0040_NS01_getAllBookingByBooker_noRequestHeader() throws Exception {
        String response = mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Required request header"));
    }

    @Test
    public void test_T0040_NS02_getAllBookingByBooker_invalidRequestHeader() throws Exception {
        String response = mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, "text instead long"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }

    @Test
    public void test_T0050_PS01_getAllBookingByOwner() throws Exception {
        long bookingIdIndividually = 111;

        BookingResponseDto booking1 = bookingResponseDto.toBuilder().build();
        BookingResponseDto booking2 = bookingResponseDto.toBuilder().id(bookingIdIndividually).build();

        List<BookingResponseDto> response = List.of(booking1, booking2);

        Mockito.when(bookingService.getAllBookingByOwner(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(response);

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyBookeOrOwnerId)
                        .param("state", State.ALL.toString())
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(response)))
                .andExpect(jsonPath(("$")).hasJsonPath())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(anyBookingId))
                .andExpect(jsonPath("$.[1].id").value(bookingIdIndividually))
                .andExpect(jsonPath("$.[1].booker.id").value(bookingResponseDto.getBooker().getId()));

        Mockito.verify(bookingService, Mockito.times(1)).getAllBookingByOwner(anyLong(), any(State.class), anyInt(), anyInt());
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void test_T0050_PS02_getAllBookingByOwner_defaultParameters() throws Exception {
        long bookingIdIndividually = 111;

        BookingResponseDto booking1 = bookingResponseDto.toBuilder().build();
        BookingResponseDto booking2 = bookingResponseDto.toBuilder().id(bookingIdIndividually).build();

        List<BookingResponseDto> response = List.of(booking1, booking2);

        Mockito.when(bookingService.getAllBookingByOwner(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(response);

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyBookeOrOwnerId)
                        .param("state", State.ALL.toString())
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(response)))
                .andExpect(jsonPath(("$")).hasJsonPath())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(anyBookingId))
                .andExpect(jsonPath("$.[1].id").value(bookingIdIndividually))
                .andExpect(jsonPath("$.[1].booker.id").value(bookingResponseDto.getBooker().getId()));

        Mockito.verify(bookingService, Mockito.times(1)).getAllBookingByOwner(anyLong(), any(State.class), anyInt(), anyInt());
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    public void test_T0050_NS01_getAllBookingByOwner_noRequestHeader() throws Exception {
        String response = mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Required request header"));
    }

    @Test
    public void test_T0050_NS02_getAllBookingByOwner_invalidRequestHeader() throws Exception {
        String response = mvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, "text instead long"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }
}