package com.kosta.readdam.dto.place;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kosta.readdam.entity.PlaceRoom;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomDto {
	private Integer roomId;
    private String name;
    private String description;
    private String size;
    private int minCapacity;
    private int maxCapacity;
    private List<String> images;
    private Map<String, Boolean> facilities;
    
    public static RoomDto from(PlaceRoom room) {
        List<String> images = Stream.of(
            room.getImg1(), room.getImg2(), room.getImg3(), room.getImg4(), room.getImg5(),
            room.getImg6(), room.getImg7(), room.getImg8(), room.getImg9(), room.getImg10()
        ).filter(Objects::nonNull).collect(Collectors.toList());

        Map<String, Boolean> facilities = new HashMap<>();
        facilities.put("airConditioner", room.getHasAirConditioner());
        facilities.put("heater", room.getHasHeater());
        facilities.put("wifi", room.getHasWifi());
        facilities.put("window", room.getHasWindow());
        facilities.put("powerOutlet", room.getHasPowerOutlet());
        facilities.put("whiteboard", room.getHasWhiteboard());
        facilities.put("tv", room.getHasTv());
        facilities.put("projector", room.getHasProjector());

        return new RoomDto(
        	room.getPlaceRoomId(),
            room.getName(),
            room.getIntroduce(),
            room.getSize(),
            room.getMinPerson(),
            room.getMaxPerson(),
            images,
            facilities
        );
    }

}
