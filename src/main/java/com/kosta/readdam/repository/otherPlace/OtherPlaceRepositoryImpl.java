package com.kosta.readdam.repository.otherPlace;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.kosta.readdam.dto.OtherPlaceDto;
import com.kosta.readdam.dto.PlaceDto;
import com.kosta.readdam.dto.otherPlace.OtherPlaceSummaryDto;
import com.kosta.readdam.dto.place.UnifiedPlaceDto;
import com.kosta.readdam.entity.QOtherPlace;
import com.kosta.readdam.entity.QOtherPlaceLike;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OtherPlaceRepositoryImpl implements OtherPlaceRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<OtherPlaceSummaryDto> findAllByFilter(Pageable pageable, String keyword, String filterBy) {
	    QOtherPlace q = QOtherPlace.otherPlace;
	    QOtherPlaceLike like = QOtherPlaceLike.otherPlaceLike;

	    BooleanExpression condition = null;

	    if (keyword != null && !keyword.isBlank()) {
	        if ("name".equals(filterBy)) {
	            condition = q.name.containsIgnoreCase(keyword);
	        } else if ("basic_address".equals(filterBy)) {
	            condition = q.basicAddress.containsIgnoreCase(keyword);
	        } else {
	            // 전체 검색
	            condition = q.name.containsIgnoreCase(keyword)
	                    .or(q.basicAddress.containsIgnoreCase(keyword));
	        }
	    }

	    List<OtherPlaceSummaryDto> content = queryFactory
	    	    .select(Projections.bean(
	    	        OtherPlaceSummaryDto.class,
	    	        q.otherPlaceId,
	    	        q.name,
	    	        q.basicAddress,
	    	        q.detailAddress,
	    	        q.phone,
	    	        q.domain,
	    	        q.introduce,
	    	        q.img1,
	    	        q.weekdayStime,
	    	        q.weekdayEtime,
	    	        q.weekendStime,
	    	        q.weekendEtime,
	    	        like.likeId.count().as("likeCount"),	    	        
    	            q.tag1,
    	            q.tag2,
    	            q.tag3,
    	            q.tag4,
    	            q.tag5    	        
	    	    ))
	    	    .from(q)
	    	    .leftJoin(like).on(q.eq(like.otherPlace))
	    	    .where(condition)
	    	    .groupBy(q.otherPlaceId)
	    	    .orderBy(q.otherPlaceId.desc())
	    	    .offset(pageable.getOffset())
	    	    .limit(pageable.getPageSize())
	    	    .fetch();


	    
	    long total = queryFactory
	            .select(q.count())
	            .from(q)
	            .where(condition)
	            .fetchOne();

	    return new PageImpl<>(content, pageable, total);
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
        String sortBy
    ) {
        QOtherPlace p = QOtherPlace.otherPlace;
        QOtherPlaceLike pl = QOtherPlaceLike.otherPlaceLike;

//        SubQueryExpression<Long> likeCountSubquery = JPAExpressions
//            .select(Wildcard.count)
//            .from(pl)
//            .where(pl.otherPlace.eq(p));

        BooleanBuilder whereBuilder = new BooleanBuilder();
        if (tag != null) whereBuilder.and(anyTagMatch(p, tag));
        if (keyword != null) whereBuilder.and(keywordMatch(p, keyword));

        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        if ("likes".equalsIgnoreCase(sortBy)) {
            orderSpecifiers.add(pl.likeId.count().desc());
            orderSpecifiers.add(p.otherPlaceId.desc());
        } else {
            orderSpecifiers.add(p.otherPlaceId.desc());
        }
        
        return queryFactory
        	    .select(Projections.constructor(UnifiedPlaceDto.class,
        	        p.otherPlaceId,
        	        p.name,
        	        p.basicAddress,
        	        p.img1,
        	        p.tag1,
        	        p.tag2,
        	        p.tag3,
        	        p.tag4,
        	        p.tag5,
        	        pl.likeId.count(),
        	        Expressions.constant("OTHER")
        	    ))
        	    .from(p)
        	    .leftJoin(pl).on(pl.otherPlace.eq(p))
        	    .where(whereBuilder)
                .groupBy(p.otherPlaceId)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(offset)
                .limit(limit)
                .fetch();


	}

    private BooleanBuilder anyTagMatch(QOtherPlace p, String tag) {
        return new BooleanBuilder()
            .or(p.tag1.eq(tag))
            .or(p.tag2.eq(tag))
            .or(p.tag3.eq(tag))
            .or(p.tag4.eq(tag))
            .or(p.tag5.eq(tag));
    }

    private BooleanBuilder keywordMatch(QOtherPlace p, String keyword) {
        return new BooleanBuilder()
            .or(p.name.containsIgnoreCase(keyword))
            .or(p.basicAddress.containsIgnoreCase(keyword));
    }

	@Override
	public List<PlaceDto> searchForAll(String keyword, String sort, int limit) {
		QOtherPlace q = QOtherPlace.otherPlace;
		BooleanBuilder builder = new BooleanBuilder();
        builder.and(
                q.name.contains(keyword)
                .or(q.basicAddress.contains(keyword))
                .or(q.detailAddress.contains(keyword))
        );

        return queryFactory
                .select(Projections.constructor(
                		PlaceDto.class,
                        q.otherPlaceId,         // otherPlaceId로 매핑
                        q.name,
                        q.basicAddress,
                        q.detailAddress,
                        q.img1,
                        q.tag1,
                        q.tag2,
                        q.tag3,
                        q.tag4,
                        q.tag5,
                        Expressions.constant("OTHER")  // type 필드 : 통합검색용으로 추가
                ))
                .from(q)
                .where(builder)
                .orderBy(q.otherPlaceId.desc())   // 최신순
                .limit(limit)
                .fetch();
    }
}
