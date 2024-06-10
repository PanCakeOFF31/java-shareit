package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemReqGetDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Builder(toBuilder = true)
public class ReqGetDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemReqGetDto> items;
}
