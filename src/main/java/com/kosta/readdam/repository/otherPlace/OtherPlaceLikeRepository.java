package com.kosta.readdam.repository.otherPlace;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kosta.readdam.entity.OtherPlace;
import com.kosta.readdam.entity.OtherPlaceLike;
import com.kosta.readdam.entity.User;

public interface OtherPlaceLikeRepository extends JpaRepository<OtherPlaceLike, Integer> {
	Optional<OtherPlaceLike> findByUserAndOtherPlace(User user, OtherPlace otherPlace);
    boolean existsByUserAndOtherPlace(User user, OtherPlace otherPlace);
    Integer countByOtherPlace(OtherPlace otherPlace);
    List<OtherPlaceLike> findByUser(User user);
}
