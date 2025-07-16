package com.kosta.readdam.repository.place;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kosta.readdam.entity.PlaceRoom;
import com.kosta.readdam.entity.PlaceTime;

@Repository
public interface PlaceTimeRepository extends JpaRepository<PlaceTime, Integer> {
    List<PlaceTime> findByPlaceRoom(PlaceRoom placeRoom);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM PlaceTime pt WHERE pt.placeRoom.placeRoomId IN (" +
           "SELECT pr.placeRoomId FROM PlaceRoom pr WHERE pr.place.placeId = :placeId)")
    void deleteByPlaceId(@Param("placeId") Integer placeId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PlaceTime pt WHERE pt.placeRoom.placeRoomId = :placeRoomId")
    void deleteByPlaceRoomId(@Param("placeRoomId") Integer placeRoomId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM PlaceTime pt WHERE pt.placeRoom.placeRoomId = :roomId")
    void deleteByPlaceRoom_PlaceRoomId(@Param("roomId") Integer roomId);
    
    List<PlaceTime> findByPlaceRoom_PlaceRoomIdAndActiveAndIsWeekend(Integer placeRoomId, boolean active, boolean isWeekend);
    
    // 방 ID로 시간대 전부 조회
    List<PlaceTime> findByPlaceRoom_PlaceRoomId(Integer placeRoomId);
    
    List<PlaceTime> findByPlaceRoomAndActiveTrue(PlaceRoom placeRoom);
}