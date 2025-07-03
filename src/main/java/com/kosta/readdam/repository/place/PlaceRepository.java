package com.kosta.readdam.repository.place;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.readdam.entity.Place;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Integer> {
    List<Place> findAllByOrderByPlaceIdDesc(Pageable pageable); // home 최신순 내림차순 + limit 4개 조회용 
}
