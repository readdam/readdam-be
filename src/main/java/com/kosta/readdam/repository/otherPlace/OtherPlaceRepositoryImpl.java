package com.kosta.readdam.repository.otherPlace;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.kosta.readdam.dto.otherPlace.OtherPlaceSummaryDto;
import com.kosta.readdam.entity.QOtherPlace;
import com.kosta.readdam.entity.QOtherPlaceLike;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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

}
