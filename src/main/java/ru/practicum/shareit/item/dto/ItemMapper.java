package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {
    public static ItemResponseDto mapToItemResponseDto(final Item item) {
        var building = ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .nextBooking(null)
                .lastBooking(null)
                .requestId(null)
                .comments(CommentMapper.mapToCommentResponseDto(item.getComments()));

        if (item.getRequest() == null)
            return building.build();

        return building.requestId(item.getRequest().getId()).build();
    }

    public static List<ItemResponseDto> mapToItemResponseDto(final Iterable<Item> items) {
        List<ItemResponseDto> dtos = new ArrayList<>();

        for (Item item : items) {
            dtos.add(mapToItemResponseDto(item));
        }

        return dtos;
    }

    public static ItemBookingDto mapToItemBookingDto(final Item item) {
        return ItemBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    public static ItemReqDto mapToItemReqDto(final Item item) {
        return ItemReqDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest().getId())
                .build();
    }

    public static List<ItemReqDto> mapToItemReqDto(final Iterable<Item> items) {
        List<ItemReqDto> dtos = new ArrayList<>();

        for (Item item : items) {
            dtos.add(mapToItemReqDto(item));
        }

        return dtos;
    }

    public static Item mapToItem(final ItemRequestDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .comments(new ArrayList<>())
                .build();
    }

}
