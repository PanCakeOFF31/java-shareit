package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {
    public static ItemRequestDto mapToItemRequestDto(final Item item) {
        return ItemRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static List<ItemRequestDto> mapToItemRequestDto(final Iterable<Item> items) {
        List<ItemRequestDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(mapToItemRequestDto(item));
        }
        return dtos;
    }


    public static ItemResponseDto mapToItemResponseDto(final Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .nextBooking(null)
                .lastBooking(null)
                .comments(CommentMapper.mapToCommentResponseDto(item.getComments()))
                .build();
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

    public static Item mapToItem(final ItemRequestDto itemDto, final long ownerId) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(new User(ownerId, null, null))
                .build();
    }

    public static Item mapToItem(final ItemRequestDto itemDto, final User user) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(user)
                .build();
    }
}
