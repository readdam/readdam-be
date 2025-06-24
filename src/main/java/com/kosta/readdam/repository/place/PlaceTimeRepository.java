package com.kosta.readdam.repository.place;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.readdam.entity.PlaceRoom;
import com.kosta.readdam.entity.PlaceTime;

@Repository
public interface PlaceTimeRepository extends JpaRepository<PlaceTime, Integer> {
    List<PlaceTime> findByPlaceRoom(PlaceRoom placeRoom);
}