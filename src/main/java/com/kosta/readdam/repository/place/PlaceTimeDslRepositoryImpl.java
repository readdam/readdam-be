package com.kosta.readdam.repository.place;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kosta.readdam.entity.QPlace;
import com.kosta.readdam.entity.QPlaceRoom;
import com.kosta.readdam.entity.QPlaceTime;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class PlaceTimeDslRepositoryImpl implements PlaceTimeDslRepository {
	 private final JPAQueryFactory queryFactory;

	    public PlaceTimeDslRepositoryImpl(JPAQueryFactory queryFactory) {
	        this.queryFactory = queryFactory;
	    }

	    @Override
	    public List<String> findTimeListByPlaceIdAndIsWeekend(Integer placeId, boolean isWeekend) {
	        QPlaceTime placeTime = QPlaceTime.placeTime;
	        QPlaceRoom placeRoom = QPlaceRoom.placeRoom;
	        QPlace place = QPlace.place;

	        return queryFactory
	                .select(placeTime.time)
	                .from(placeTime)
	                .join(placeTime.placeRoom, placeRoom)
	                .join(placeRoom.place, place)
	                .where(
	                    place.placeId.eq(placeId),
	                    placeTime.isWeekend.eq(isWeekend),
	                    placeTime.active.isTrue()
	                )
	                .fetch();
	    }
}
