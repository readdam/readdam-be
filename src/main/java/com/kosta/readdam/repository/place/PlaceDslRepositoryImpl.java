package com.kosta.readdam.repository.place;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.kosta.readdam.dto.PlaceDto;
import com.kosta.readdam.dto.SearchResultDto;
import com.kosta.readdam.dto.place.PlaceSummaryDto;
import com.kosta.readdam.dto.place.UnifiedPlaceDto;
import com.kosta.readdam.entity.QPlace;
import com.kosta.readdam.entity.QPlaceLike;
import com.kosta.readdam.entity.QPlaceRoom;
import com.kosta.readdam.entity.QPlaceTime;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PlaceDslRepositoryImpl implements PlaceDslRepository {
	private final JPAQueryFactory query;

	private Map<Integer, Integer> fetchPlaceLikeCountMap() {
		QPlaceLike like = QPlaceLike.placeLike;

		List<Tuple> result = query.select(like.place.placeId, like.count()).from(like).groupBy(like.place.placeId)
				.fetch();

		return result.stream()
				.collect(Collectors.toMap(t -> t.get(like.place.placeId), t -> t.get(like.count()).intValue()));
	}

	// 요일별 운영 시간 맵 조회
	private Map<Integer, List<String>> fetchOperatingTimes(boolean isWeekend) {
		QPlace place = QPlace.place;
		QPlaceRoom room = QPlaceRoom.placeRoom;
		QPlaceTime time = QPlaceTime.placeTime;

		List<Tuple> tuples = query.select(place.placeId, time.time).from(place).join(room)
				.on(room.place.placeId.eq(place.placeId)).join(time).on(time.placeRoom.placeRoomId.eq(room.placeRoomId))
				.where(time.active.eq(true).and(time.isWeekend.eq(isWeekend))).fetch();

		return tuples.stream().collect(Collectors.groupingBy(t -> t.get(place.placeId),
				Collectors.mapping(t -> t.get(time.time), Collectors.toList())));
	}

	@Override
	public Page<PlaceSummaryDto> findPlaceList(Pageable pageable, String keyword, String filterBy) {
		QPlace place = QPlace.place;
		QPlaceRoom room = QPlaceRoom.placeRoom;

		// 좋아요 수 조회 맵
		Map<Integer, Integer> likeMap = fetchPlaceLikeCountMap();

		// 시간대 맵 조회
		Map<Integer, List<String>> weekdayMap = fetchOperatingTimes(false);
		Map<Integer, List<String>> weekendMap = fetchOperatingTimes(true);

		// 조건 필터링
		BooleanExpression condition = null;
		if (keyword != null && !keyword.isBlank()) {
			if ("name".equals(filterBy)) {
				condition = place.name.containsIgnoreCase(keyword);
			} 
			else if ("basic_address".equals(filterBy)) {
				condition = place.basicAddress.containsIgnoreCase(keyword);
			}
		}

		// SELECT
		List<Tuple> results = query
				.select(place.placeId, place.name, place.basicAddress, place.detailAddress, place.introduce, place.phone,
						room.placeRoomId.countDistinct(),

						place.tag1, place.tag2, place.tag3, place.tag4, place.tag5, place.tag6, place.tag7, place.tag8,
						place.tag9, place.tag10,

						place.img1)
				.from(place).leftJoin(room).on(room.place.placeId.eq(place.placeId)).where(condition) // 필터 조건
				.groupBy(place.placeId).orderBy(place.placeId.desc()).offset(pageable.getOffset())
				.limit(pageable.getPageSize()).fetch();

		// DTO 변환
		List<PlaceSummaryDto> content = results.stream().map(tuple -> {
			Integer id = tuple.get(place.placeId);
			String name = tuple.get(place.name);
			String basicAddress = tuple.get(place.basicAddress);
			String detailAddress = tuple.get(place.detailAddress);
			String introduce = tuple.get(place.introduce);
			String phone = tuple.get(place.phone);
			Long roomCount = tuple.get(room.placeRoomId.countDistinct());

			List<String> tags = Stream.of(
				    tuple.get(place.tag1), tuple.get(place.tag2), tuple.get(place.tag3), tuple.get(place.tag4),
				    tuple.get(place.tag5), tuple.get(place.tag6), tuple.get(place.tag7), tuple.get(place.tag8),
				    tuple.get(place.tag9), tuple.get(place.tag10)
				).filter(t -> t != null && !t.isBlank())
				 .collect(Collectors.toList());

			List<String> images = Stream.of(
				    tuple.get(place.img1), tuple.get(place.img2), tuple.get(place.img3), tuple.get(place.img4),
				    tuple.get(place.img5), tuple.get(place.img6), tuple.get(place.img7), tuple.get(place.img8),
				    tuple.get(place.img9), tuple.get(place.img10)
				).filter(img -> img != null && !img.isBlank())
				 .collect(Collectors.toList());
			
			String thumbnail = images.isEmpty() ? null : images.get(0);
			Integer likeCount = likeMap.getOrDefault(id, 0);

			return new PlaceSummaryDto(id, name, basicAddress, detailAddress, introduce, phone, roomCount, tags, thumbnail, images,
					weekdayMap.getOrDefault(id, List.of()), weekendMap.getOrDefault(id, List.of()), likeCount);
		}).collect(Collectors.toList());

		// 총 개수
		Long total = query.select(place.count()).from(place).where(condition) // 조건 동일하게 적용
				.fetchOne();

		return new PageImpl<>(content, pageable, total == null ? 0 : total);
	}
	
	@Override
	public List<UnifiedPlaceDto> searchPlaces(
	    String tag,
	    String keyword,
	    Double lat,
	    Double lng,
	    Double radiusKm,
	    int offset,
	    int limit,
	    String sortBy // 정렬 기준 파라미터 추가
	) {
	    QPlace p = QPlace.place;
	    QPlaceLike pl = QPlaceLike.placeLike;

	    // 기본 where 조건
	    BooleanBuilder whereBuilder = new BooleanBuilder();
	    if (tag != null) {
	        whereBuilder.and(anyTagMatch(p, tag));
	    }
	    if (keyword != null) {
	        whereBuilder.and(keywordMatch(p, keyword));
	    }

	    // 거리 계산식(선택)
	    // DoubleExpression distanceExpression = ...;

	    // 정렬 조건
	    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
	    if ("likes".equalsIgnoreCase(sortBy)) {
	        orderSpecifiers.add(pl.likeId.count().desc());
	        orderSpecifiers.add(p.placeId.desc());
	    } else {
	        // 최신순(등록 id 내림차순)
	        orderSpecifiers.add(p.placeId.desc());
	    }

	    return query
	        .select(Projections.constructor(UnifiedPlaceDto.class,
	            p.placeId,
	            p.name,
	            p.basicAddress,
	            p.img1,
	            p.tag1,
	            p.tag2,
	            p.tag3,
	            p.tag4,
	            p.tag5,
	            pl.likeId.count(), // 좋아요 개수
	            Expressions.constant("PLACE"),
	            p.lat,
	            p.lng
	        ))
	        .from(p)
	        .leftJoin(pl).on(pl.place.eq(p))
	        .where(whereBuilder)
	        .groupBy(p.placeId)
	        .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
	        .offset(offset)
	        .limit(limit)
	        .fetch();
	}


	    private BooleanBuilder anyTagMatch(QPlace p, String tag) {
	        return new BooleanBuilder()
	            .or(p.tag1.eq(tag))
	            .or(p.tag2.eq(tag))
	            .or(p.tag3.eq(tag))
	            .or(p.tag4.eq(tag))
	            .or(p.tag5.eq(tag));
	    }

	    private BooleanBuilder keywordMatch(QPlace p, String keyword) {
	        return new BooleanBuilder()
	            .or(p.name.containsIgnoreCase(keyword))
	            .or(p.basicAddress.containsIgnoreCase(keyword));
	    }

		@Override
		public SearchResultDto<PlaceDto> searchForAll(String keyword, String sort, int limit) {
			QPlace p = QPlace.place;
			QPlaceLike pl = QPlaceLike.placeLike;
		        BooleanBuilder builder = new BooleanBuilder();
		        builder.and(
		                p.name.contains(keyword)
		                .or(p.basicAddress.contains(keyword))
		                .or(p.detailAddress.contains(keyword))
		        );

		        long count = query
		                .select(p.count())
		                .from(p)
		                .where(builder)
		                .fetchOne();

		        List<PlaceDto> result = query
		                .select(Projections.constructor(
		                        PlaceDto.class,
		                        Expressions.stringTemplate("'place-' || {0}", p.placeId),
		                        //Expressions.numberTemplate(Integer.class, "place_id"),
		                        p.placeId,
		                        p.name,
		                        p.basicAddress,
		                        p.detailAddress,
		                        p.img1,
		                        p.tag1,
		                        p.tag2,
		                        p.tag3,
		                        p.tag4,
		                        p.tag5,
		                        JPAExpressions
		                        .select(pl.count())
		                        .from(pl)
		                        .where(pl.place.eq(p)),
		                        Expressions.constant("PLACE")
		                ))
		                .from(p)
		                .where(builder)
		                .orderBy(p.placeId.desc())
		                .limit(limit)
		                .fetch();
		        
		        return new SearchResultDto<>(result, (int) count);    
		}
}
