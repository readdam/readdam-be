package com.kosta.readdam.repository.place;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.Place;
import com.kosta.readdam.entity.PlaceLike;
import com.kosta.readdam.entity.User;

public interface PlaceLikeRepository extends JpaRepository<PlaceLike, Integer>{

	List<PlaceLike> findByUser(User user);
    Optional<PlaceLike> findByUserAndPlace(User user, Place place);
    long countByPlace(Place place);
    void deleteByUserAndPlace(User user, Place place);
    
}
