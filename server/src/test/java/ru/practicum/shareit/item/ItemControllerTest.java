package ru.practicum.shareit.item;

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
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    @MockBean
    private final ItemService itemService;
    private static final String REQUEST_USER_HEADER = "X-Sharer-User-Id";
    private ItemRequestDto itemRequestDto;
    private ItemResponseDto itemResponseDto;
    private CommentRequestDto commentRequestDto;
    private CommentResponseDto commentResponseDto;
    private long anyItemId;
    private long anyOwnerId;
    private long anyCommentId;
    private LocalDateTime ldt;

    @BeforeEach
    public void preTestInitialization() {
        anyOwnerId = 9999;
        anyItemId = 111;
        anyCommentId = 123;

        ldt = LocalDateTime.of(2020, 10, 12, 15, 45, 30);

        itemRequestDto = ItemRequestDto.builder()
                .name("clock")
                .description("random description")
                .available(true)
                .requestId(null)
                .build();

        itemResponseDto = ItemResponseDto.builder()
                .id(anyItemId)
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .available(itemRequestDto.getAvailable())
                .lastBooking(null)
                .nextBooking(null)
                .comments(new ArrayList<>())
                .requestId(itemRequestDto.getRequestId())
                .build();

        commentRequestDto = CommentRequestDto.builder().text("some text info").build();
        commentResponseDto = CommentResponseDto.builder()
                .id(anyCommentId)
                .text("any comment text")
                .authorName("comment author name")
                .created(ldt)
                .build();
    }

    @Test
    public void test_T0010_PS01_creteItem() throws Exception {
        Mockito.when(itemService.createItem(Mockito.any(), Mockito.anyLong()))
                .thenReturn(itemResponseDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyOwnerId))
                .andExpect(status().isCreated())
                .andExpect(content().string(mapper.writeValueAsString(itemResponseDto)))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$.id").value(anyItemId))
                .andExpect(jsonPath("$.name", is(itemRequestDto.getName())))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemRequestDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", nullValue()))
                .andExpect(jsonPath("$.nextBooking", nullValue()))
                .andExpect(jsonPath("$.comments", emptyIterable()))
                .andExpect(jsonPath("$.requestId", nullValue()));

        Mockito.verify(itemService, Mockito.times(1)).createItem(Mockito.any(), Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    public void test_T0010_NS01_creteItem_invalidContent() throws Exception {
        String response = mvc.perform(post("/items")
                        .content("")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyOwnerId))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Required request body is missing"));
    }

    @Test
    public void test_T0010_NS02_createItem_noRequestHeader() throws Exception {
        String response = mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Required request header 'X-Sharer-User-Id'"));
    }

    @Test
    public void test_T0010_NS03_createItem_invalidRequestHeader() throws Exception {
        String response = mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, "text instead long"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }

    @Test
    public void test_T0020_PS01_updateItem_() throws Exception {
        Mockito.when(itemService.updateItem(any(), anyLong(), anyLong()))
                .thenReturn(itemResponseDto);

        mvc.perform(patch("/items/" + anyItemId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyOwnerId))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(itemResponseDto)))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$.id").value(anyItemId))
                .andExpect(jsonPath("$.name", is(itemRequestDto.getName())))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemRequestDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", nullValue()))
                .andExpect(jsonPath("$.nextBooking", nullValue()))
                .andExpect(jsonPath("$.comments", emptyIterable()))
                .andExpect(jsonPath("$.requestId", nullValue()));

        Mockito.verify(itemService, Mockito.times(1)).updateItem(any(), anyLong(), anyLong());
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    public void test_T0020_PS02_updateItem_invalidContent() throws Exception {
//        Нет валидации контента isOk()
    }

    @Test
    public void test_T0020_NS01_updateItem_noRequestHeader() throws Exception {
        String response = mvc.perform(patch("/items/" + anyItemId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Required request header 'X-Sharer-User-Id'"));
    }

    @Test
    public void test_T0020_NS02_updateItem_invalidRequestHeader() throws Exception {
        String response = mvc.perform(patch("/items/" + anyItemId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, "text instead long"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }

    @Test
    public void test_T0020_NS03_updateItem_noPathVariable_itemId() throws Exception {
        mvc.perform(patch("/items/")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyOwnerId))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void test_T0020_NS04_updateItem_invalidPathVariable_itemId() throws Exception {
        String response = mvc.perform(patch("/items/a8SD")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyOwnerId))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }

    @Test
    public void test_T0030_PS01_getItem() throws Exception {
        Mockito.when(itemService.getItemDtoById(anyLong(), anyLong()))
                .thenReturn(itemResponseDto);

        mvc.perform(get("/items/" + anyItemId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyOwnerId))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(itemResponseDto)))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$.id").value(anyItemId))
                .andExpect(jsonPath("$.name", is(itemRequestDto.getName())))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemRequestDto.getAvailable())))
                .andExpect(jsonPath("$.lastBooking", nullValue()))
                .andExpect(jsonPath("$.nextBooking", nullValue()))
                .andExpect(jsonPath("$.comments", emptyIterable()))
                .andExpect(jsonPath("$.requestId", nullValue()));

        Mockito.verify(itemService, Mockito.times(1)).getItemDtoById(anyLong(), anyLong());
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    public void test_T0030_NS01_getItem_noRequestHeader_ownerId() throws Exception {
        String response = mvc.perform(get("/items/" + anyItemId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Required request header 'X-Sharer-User-Id'"));
    }

    @Test
    public void test_T0030_NS02_getItem_invalidRequestHeader_ownerId() throws Exception {
        String response = mvc.perform(get("/items/" + anyItemId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, "text instead long"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }

    @Test
    public void test_T0030_NS03_getItem_invalidPathVariable_itemId() throws Exception {
        String response = mvc.perform(get("/items/a8SD")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyOwnerId))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }

    //    -
    @Test
    public void test_T0040_PS01_getItemsByOwner() throws Exception {
        long itemIdIndividually = 754;

        ItemResponseDto item1 = itemResponseDto.toBuilder().build();
        ItemResponseDto item2 = itemResponseDto.toBuilder().id(itemIdIndividually).build();
        ItemResponseDto item3 = itemResponseDto.toBuilder().build();

        List<ItemResponseDto> response = List.of(item1, item2, item3);

        Mockito.when(itemService.getItemsByOwner(anyLong(), anyInt(), anyInt()))
                .thenReturn(response);

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .header(REQUEST_USER_HEADER, anyOwnerId))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(response)))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$.[0].id").value(anyItemId))
                .andExpect(jsonPath("$.[1].id").value(itemIdIndividually))
                .andExpect(jsonPath("$.[2].comments").isArray())
                .andExpect(jsonPath("$.[2].comments.length()").value(0));

        Mockito.verify(itemService, Mockito.times(1)).getItemsByOwner(anyLong(), anyInt(), anyInt());
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    public void test_T0040_PS02_getItemsByOwner_defaultParameters() throws Exception {
        long itemIdIndividually = 754;

        ItemResponseDto item1 = itemResponseDto.toBuilder().build();
        ItemResponseDto item2 = itemResponseDto.toBuilder().id(itemIdIndividually).build();
        ItemResponseDto item3 = itemResponseDto.toBuilder().build();

        List<ItemResponseDto> response = List.of(item1, item2, item3);

        Mockito.when(itemService.getItemsByOwner(anyLong(), anyInt(), anyInt()))
                .thenReturn(response);

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyOwnerId)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(response)))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$.[0].id").value(anyItemId))
                .andExpect(jsonPath("$.[1].id").value(itemIdIndividually))
                .andExpect(jsonPath("$.[2].comments").isArray())
                .andExpect(jsonPath("$.[2].comments.length()").value(0));

        Mockito.verify(itemService, Mockito.times(1)).getItemsByOwner(anyLong(), anyInt(), anyInt());
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    public void test_T0040_NS01_getItemsByOwner_noRequestHeader_ownerId() throws Exception {
        String response = mvc.perform(get("/items")
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
    public void test_T0040_NS02_getItemsByOwner_invalidRequestHeader_ownerId() throws Exception {
        String response = mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, "text instead long")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }

    @Test
    public void test_T0050_PS01_searchItems_() throws Exception {
        long itemIdIndividually = 754;

        ItemResponseDto item1 = itemResponseDto.toBuilder().build();
        ItemResponseDto item2 = itemResponseDto.toBuilder().id(itemIdIndividually).build();
        ItemResponseDto item3 = itemResponseDto.toBuilder().build();

        List<ItemResponseDto> response = List.of(item1, item2, item3);

        Mockito.when(itemService.searchItems(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(response);

        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("text", "any text")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .header(REQUEST_USER_HEADER, anyOwnerId))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(response)))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$.[0].id").value(anyItemId))
                .andExpect(jsonPath("$.[1].id").value(itemIdIndividually))
                .andExpect(jsonPath("$.[2].comments").isArray())
                .andExpect(jsonPath("$.[2].comments.length()").value(0));

        Mockito.verify(itemService, Mockito.times(1)).searchItems(anyLong(), anyString(), anyInt(), anyInt());
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    public void test_T0050_PS02_searchItems_defaultParameters() throws Exception {
        long itemIdIndividually = 754;

        ItemResponseDto item1 = itemResponseDto.toBuilder().build();
        ItemResponseDto item2 = itemResponseDto.toBuilder().id(itemIdIndividually).build();
        ItemResponseDto item3 = itemResponseDto.toBuilder().build();

        List<ItemResponseDto> response = List.of(item1, item2, item3);

        Mockito.when(itemService.searchItems(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(response);

        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("text", "any text")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .header(REQUEST_USER_HEADER, anyOwnerId))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(response)))
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$.[0].id").value(anyItemId))
                .andExpect(jsonPath("$.[1].id").value(itemIdIndividually))
                .andExpect(jsonPath("$.[2].comments").isArray())
                .andExpect(jsonPath("$.[2].comments.length()").value(0));

        Mockito.verify(itemService, Mockito.times(1)).searchItems(anyLong(), anyString(), anyInt(), anyInt());
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    public void test_T0050_NS01_searchItems_noRequestHeader_ownerId() throws Exception {
        String response = mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("text", "any text")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Required request header 'X-Sharer-User-Id'"));
    }

    @Test
    public void test_T0050_NS02_searchItems_invalidRequestHeader_ownerId() throws Exception {
        String response = mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, "text instead long")
                        .param("text", "any text")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }

    @Test
    public void test_T0050_NS03_searchItems_noRequestParameter_text() throws Exception {
        String response = mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyOwnerId)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Required request parameter"));
    }

    @Test
    public void test_T0060_PS01_createComment() throws Exception {
        commentResponseDto.setCreated(LocalDateTime.of(2030, 10, 15, 10, 15, 25));

        Mockito.when(itemService.createComment(any(), anyLong(), anyLong()))
                .thenReturn(commentResponseDto);

        mvc.perform(post("/items/" + anyItemId + "/comment")
                        .content(mapper.writeValueAsString(commentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, anyOwnerId))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(commentResponseDto)))
                .andExpect(jsonPath(("$")).hasJsonPath())
                .andExpect(jsonPath(("$.id")).value(commentResponseDto.getId()))
                .andExpect(jsonPath("$.text", is(commentResponseDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentResponseDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentResponseDto.getCreated().toString())));

        Mockito.verify(itemService, Mockito.times(1)).createComment(any(), anyLong(), anyLong());
        Mockito.verifyNoMoreInteractions(itemService);
    }


    @Test
    public void test_T0060_NS01_createComment_noRequestHeader_ownerId() throws Exception {
        String response = mvc.perform(post("/items/" + anyItemId + "/comment")
                        .content(mapper.writeValueAsString(commentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Required request header 'X-Sharer-User-Id'"));
    }

    @Test
    public void test_T0060_NS02_createComment_invalidRequestHeader_ownerId() throws Exception {
        String response = mvc.perform(post("/items/" + anyItemId + "/comment")
                        .content(mapper.writeValueAsString(commentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, "text instead long"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }

    @Test
    public void test_T0060_NS03_createComment_noPathVariable_itemId() throws Exception {
        mvc.perform(post("/items//comment")
                        .content(mapper.writeValueAsString(commentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, String.valueOf(anyOwnerId)))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void test_T0060_NS04_createComment_invalidPathVariable_itemId() throws Exception {
        String response = mvc.perform(post("/items/9*sad/comment")
                        .content(mapper.writeValueAsString(commentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_USER_HEADER, String.valueOf(anyOwnerId)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Failed to convert value"));
    }
}