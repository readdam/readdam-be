package com.kosta.readdam.dto;

import com.kosta.readdam.entity.PlaceRoom;
import com.kosta.readdam.entity.PlaceTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceTimeDto {

    private Integer placeTimeId;
    private Integer placeRoomId;
    private Boolean isWeekend;
    private Boolean active;
    private String time;

    public PlaceTime toEntity(PlaceRoom placeRoom) {
        return PlaceTime.builder()
                .placeTimeId(placeTimeId)
                .placeRoom(placeRoom)
                .isWeekend(isWeekend)
                .active(active)
                .time(time)
                .build();
    }
}
