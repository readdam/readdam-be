package com.kosta.readdam.repository.place;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kosta.readdam.entity.Place;
import com.kosta.readdam.entity.PlaceRoom;

@Repository
public interface PlaceRoomRepository extends JpaRepository<PlaceRoom, Integer> {
    List<PlaceRoom> findByPlace(Place place);
    List<PlaceRoom> findByPlace_PlaceId(Integer placeId);  
    
    @Query("SELECT r FROM PlaceRoom r WHERE r.place.placeId = :placeId")
    List<PlaceRoom> findByPlaceId(@Param("placeId") Integer placeId);
}