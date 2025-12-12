package com.kosta.readdam.repository.place;

import java.util.List;

public interface PlaceTimeDslRepository {
    List<String> findTimeListByPlaceIdAndIsWeekend(Integer placeId, boolean isWeekend);
}