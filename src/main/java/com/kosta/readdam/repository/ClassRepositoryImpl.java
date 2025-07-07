package com.kosta.readdam.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.kosta.readdam.dto.ClassCardDto;
import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.dto.ClassSearchConditionDto;
import com.kosta.readdam.entity.QClassEntity;
import com.kosta.readdam.entity.QClassLike;
import com.kosta.readdam.entity.QClassUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClassRepositoryImpl implements ClassRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	
	@Override
	public Slice<ClassCardDto> searchClasses(ClassSearchConditionDto condition, Pageable pageable) {
		QClassEntity c = QClassEntity.classEntity;
		QClassLike cl = QClassLike.classLike;
		QClassUser cu = QClassUser.classUser;
		
		// 검색: 제목, 태그, 장소명에서 검색어 검색
		BooleanBuilder builder = new BooleanBuilder();
		String keyword = condition.getKeyword();

        if (keyword != null && !keyword.isBlank()) {
            builder.and(
            		c.title.containsIgnoreCase(keyword)
            		.or(c.tag1.containsIgnoreCase(keyword))
            		.or(c.tag2.containsIgnoreCase(keyword))
            		.or(c.tag3.containsIgnoreCase(keyword))
            		.or(c.round1PlaceName.containsIgnoreCase(keyword))
            	);
        }

        if (condition.getTag() != null && !condition.getTag().isBlank()) {
            builder.and(
            		c.tag1.eq(condition.getTag())
		            .or(c.tag2.eq(condition.getTag()))
		            .or(c.tag3.eq(condition.getTag()))
		            );
		        }

        if (condition.getPlace() != null && !condition.getPlace().isBlank()) {
            builder.and(c.round1PlaceName.eq(condition.getPlace()));
        }

        // 다중정렬
//        <OrderSpecifier<?> sortOrder = getSortOrder(condition.getSort(), c, cl);
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        
		if("latest".equals(condition.getSort())) {
			orderSpecifiers.add(c.createdAt.desc());
		}else if ("likes".equals(condition.getSort())) {
			orderSpecifiers.add(Expressions.numberPath(Integer.class, "likeCnt").desc());
			orderSpecifiers.add(c.createdAt.desc());
		}else if ("deadline".equals(condition.getSort())) {
			orderSpecifiers.add(c.maxPerson.asc());
			orderSpecifiers.add(c.createdAt.desc());
		}else {
			orderSpecifiers.add(c.createdAt.desc());
		}
	
        
        List<ClassCardDto> results = queryFactory
            .select(Projections.constructor(ClassCardDto.class,
                c.classId,
                c.title,
                c.shortIntro,
                c.tag1,
                c.tag2,
                c.tag3,
                c.minPerson,
                c.maxPerson,
                c.mainImg,
                c.round1Date,
                c.round1PlaceName,
                cl.count().intValue().as("likeCnt"),	// likeCnt
                cu.count().intValue().as("currentParticipants")	// currentParticipants
            ))
            .from(c)
            .leftJoin(cl).on(cl.classId.eq(c))
            .leftJoin(cu).on(cu.classEntity.eq(c))
            .where(builder)
            .groupBy(c.classId)
            .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize()+1)	// +1개 더 가져와서 hasNext 확인
            .fetch();
        
        boolean hasNext = results.size() > pageable.getPageSize();
        
        if (hasNext) {
        	results.remove(results.size() - 1);	// 마지막 더 가져온 1개 제거
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }

	@Override
	public List<ClassDto> searchForAll(String keyword, String sort, int limit) {
	    QClassEntity c = QClassEntity.classEntity;

	    BooleanBuilder builder = new BooleanBuilder();
	    builder.and(
                c.title.contains(keyword)
                .or(c.shortIntro.contains(keyword))
	    );

        return queryFactory
                .select(Projections.constructor(
                        ClassDto.class,
                        c.classId,
                        c.title,
                        c.mainImg,
                        c.mainImg,            // image 필드 매핑
                        c.tag1,
                        c.tag2,
                        c.tag3,
                        c.shortIntro,
                        c.round1Date,
                        c.round1PlaceName
                ))
                .from(c)
                .where(builder)
                .orderBy(c.createdAt.desc())
                .limit(limit)
                .fetch();
    }
}
