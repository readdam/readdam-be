package com.kosta.readdam.repository.place;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.readdam.entity.Place;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Integer> {
}
