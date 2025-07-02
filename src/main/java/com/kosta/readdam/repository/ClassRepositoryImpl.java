package com.kosta.readdam.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.kosta.readdam.dto.ClassCardDto;
import com.kosta.readdam.dto.ClassSearchConditionDto;
import com.kosta.readdam.entity.QClassEntity;
import com.kosta.readdam.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClassRepositoryImpl implements ClassRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	
	@Override
	public Slice<ClassCardDto> searchClasses(ClassSearchConditionDto condition, Pageable pageable) {
		QClassEntity c = QClassEntity.classEntity;
		
		BooleanBuilder builder = new BooleanBuilder();

        if (condition.getKeyword() != null && !condition.getKeyword().isBlank()) {
            builder.and(c.title.containsIgnoreCase(condition.getKeyword()));
        }

        if (condition.getTag() != null && !condition.getTag().isBlank()) {
            builder.and
            (c.tag1.eq(condition.getTag()))
            .or(c.tag2.eq(condition.getTag()))
            .or(c.tag3.eq(condition.getTag()));
        }

        if (condition.getPlace() != null && !condition.getPlace().isBlank()) {
            builder.and(c.round1PlaceName.eq(condition.getPlace()));
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
                c.round1PlaceName
            ))
            .from(c)
            .where(builder)
//            .orderBy(getSortOrder(condition.getSort(),c))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize()+1)	// +1개 더 가져와서 hasNext 확인
            .fetch();
        
        boolean hasNext = results.size() > pageable.getPageSize();
        
        if (hasNext) {
        	results.remove(results.size() - 1);	// 마지막 더 가져온 1개 제거
        }

        // 정렬 처리
//        if ("latest".equals(condition.getSort())) {
//            query.orderBy(c.createdDate.desc());
//        } else if ("popular".equals(condition.getSort())) {
//            query.orderBy(cl.count().desc());
//        }

        return new SliceImpl<>(results, pageable, hasNext);
    }

}
