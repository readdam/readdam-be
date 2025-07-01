package com.kosta.readdam.repository;

import java.util.List;

import com.kosta.readdam.dto.ClassCardDto;
import com.kosta.readdam.dto.ClassSearchConditionDto;
import com.kosta.readdam.entity.QClassEntity;
import com.kosta.readdam.entity.QClassLike;
import com.kosta.readdam.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClassRepositoryImpl implements ClassRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	
	@Override
	public List<ClassCardDto> searchClasses(ClassSearchConditionDto condition) {
		QClassEntity c = QClassEntity.classEntity;
		QClassLike cl = QClassLike.classLike;
		QUser u = QUser.user;
		
		BooleanBuilder builder = new BooleanBuilder();

        if (condition.getKeyword() != null) {
            builder.and(c.title.containsIgnoreCase(condition.getKeyword()));
        }

        if (condition.getTag() != null) {
            builder.and(c.tag1.eq(condition.getTag()))
            .and(c.tag2.eq(condition.getTag()))
            .and(c.tag3.eq(condition.getTag()));
        }

        if (condition.getPlace() != null) {
            builder.and(c.round1PlaceName.eq(condition.getPlace()));
        }

        JPAQuery<ClassCardDto> query = queryFactory
            .select(Projections.constructor(ClassCardDto.class,
                c.classId,
                c.title,
                c.mainImg,
                u.nickname,
                c.round1PlaceName,
                c.tag1,
                c.tag2,
                c.tag3,
                c.round1Date
            ))
            .from(c)
            .join(c.leader, u)
            .where(builder);

        // 정렬 처리
        if ("latest".equals(condition.getSort())) {
            query.orderBy(c.createdDate.desc());
        } else if ("popular".equals(condition.getSort())) {
            query.orderBy(cl.count().desc());
        }

        return query.fetch();
    }

}
