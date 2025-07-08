package com.kosta.readdam.repository.reservation;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.kosta.readdam.dto.reservation.ReservationDetailListDto;
import com.kosta.readdam.entity.QPlace;
import com.kosta.readdam.entity.QPlaceRoom;
import com.kosta.readdam.entity.QReservation;
import com.kosta.readdam.entity.QReservationDetail;
import com.kosta.readdam.entity.enums.ReservationStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationDslRepositoryImpl implements ReservationDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReservationDetailListDto> findReservations(Pageable pageable, String dateStr, String statusStr, String keyword) {
        QReservation reservation = QReservation.reservation;
        QReservationDetail detail = QReservationDetail.reservationDetail;
        QPlaceRoom room = QPlaceRoom.placeRoom;
        QPlace place = QPlace.place;

        BooleanBuilder where = new BooleanBuilder();
        
        if (statusStr != null && !statusStr.isEmpty()) {
            where.and(reservation.status.eq(ReservationStatus.valueOf(statusStr)));
        }

        if (dateStr != null && !dateStr.isEmpty()) {
            where.and(detail.date.eq(LocalDate.parse(dateStr)));
        }


        if (keyword != null && !keyword.isEmpty()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            keywordBuilder.or(place.name.containsIgnoreCase(keyword));
            keywordBuilder.or(place.basicAddress.containsIgnoreCase(keyword));
            keywordBuilder.or(place.detailAddress.containsIgnoreCase(keyword));
            keywordBuilder.or(room.name.containsIgnoreCase(keyword));
            keywordBuilder.or(reservation.reserverName.containsIgnoreCase(keyword)); 
            where.and(keywordBuilder);
        }

        List<Tuple> tuples = queryFactory
        	    .select(
        	        reservation.reservationId,
        	        place.name,
        	        place.basicAddress,
        	        place.detailAddress,
        	        room.name,
        	        detail.date,
        	        detail.time,
        	        reservation.reserverName,
        	        reservation.participantCount,
        	        reservation.status
        	    )
        	    .from(detail)
        	    .join(detail.reservation, reservation)
        	    .join(reservation.placeRoom, room)
        	    .join(room.place, place)
        	    .where(where)
        	    .orderBy(detail.date.asc(), detail.time.asc())
        	    .fetch();


        // group by reservationId + date
        Map<String, List<com.querydsl.core.Tuple>> grouped = tuples.stream()
            .collect(Collectors.groupingBy(t -> {
                Integer resId = t.get(reservation.reservationId);
                LocalDate date = t.get(detail.date);
                return resId + "|" + date.toString();
            }));

        // start/end time 계산
        List<ReservationDetailListDto> content = grouped.values().stream()
            .map(list -> {
                com.querydsl.core.Tuple any = list.get(0);
                Integer resId = any.get(reservation.reservationId);
                String placeName = any.get(place.name);
                String placeAddr = any.get(place.basicAddress) + " " + any.get(place.detailAddress);
                String roomName = any.get(room.name);
                LocalDate date = any.get(detail.date);
                String reserver = any.get(reservation.reserverName);
                Integer participants = any.get(reservation.participantCount);
                ReservationStatus status = any.get(reservation.status);

                List<java.time.LocalTime> times = list.stream()
                    .map(t -> t.get(detail.time))
                    .sorted()
                    .collect(Collectors.toList());

                return ReservationDetailListDto.builder()
                    .reservationId(resId)
                    .placeName(placeName)
                    .placeAddress(placeAddr)
                    .roomName(roomName)
                    .date(date)
                    .startTime(times.get(0))
                    .endTime(times.get(times.size() - 1).plusHours(1))
                    .reserverName(reserver)
                    .participantCount(participants)
                    .status(status)
                    .build();
            })
            .skip(pageable.getOffset())
            .limit(pageable.getPageSize())
            .collect(Collectors.toList());

        long total = queryFactory
        	    .select(detail.reservation.reservationId, detail.date)
        	    .from(detail)
        	    .join(detail.reservation, reservation)
        	    .join(reservation.placeRoom, room)
        	    .join(room.place, place)
        	    .where(where)
        	    .distinct()
        	    .fetch()
        	    .stream()
        	    .map(t -> t.get(detail.reservation.reservationId) + "|" + t.get(detail.date))
        	    .collect(Collectors.toSet())
        	    .size();

        return new PageImpl<>(content, pageable, total);
    }

}
