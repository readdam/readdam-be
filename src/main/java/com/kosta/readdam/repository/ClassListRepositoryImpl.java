package com.kosta.readdam.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.kosta.readdam.dto.ClassListDto;
import com.kosta.readdam.entity.QClassEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ClassListRepositoryImpl implements ClassListRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<ClassListDto> searchClasses(String keyword, String status,
	                                        LocalDate fromDate, LocalDate toDate, Pageable pageable) {
	    QClassEntity cls = QClassEntity.classEntity;
	    LocalDate today = LocalDate.now();

	    DateExpression<LocalDate> calculatedEndDate = new CaseBuilder()
	        .when(cls.round4Date.isNotNull()).then(cls.round4Date)
	        .when(cls.round3Date.isNotNull()).then(cls.round3Date)
	        .when(cls.round2Date.isNotNull()).then(cls.round2Date)
	        .otherwise(cls.round1Date);

	    BooleanBuilder builder = new BooleanBuilder();

	    // 🔍 모임명 OR 닉네임 검색
	    if (keyword != null && !keyword.isEmpty()) {
	        builder.and(
	            cls.title.containsIgnoreCase(keyword)
	            .or(cls.leader.name.containsIgnoreCase(keyword))
	            .or(cls.leader.nickname.containsIgnoreCase(keyword))
	        );
	    }

	    // 📌 상태 필터
	    if (status != null) {
	        switch (status) {
	            case "모집중":
	                builder.and(cls.round1Date.gt(today));
	                break;
	            case "진행중":
	                builder.and(cls.round1Date.loe(today).and(calculatedEndDate.goe(today)));
	                break;
	            case "종료":
	                builder.and(calculatedEndDate.lt(today));
	                break;
	        }
	    }

	    // 🗓️ 기간 필터
	    if (fromDate != null && toDate != null) {
	        builder.and(cls.createdAt.between(fromDate.atStartOfDay(), toDate.atTime(23, 59, 59)));
	    }

	    List<ClassListDto> content = queryFactory
	        .select(Projections.constructor(ClassListDto.class,
	                Expressions.constant(0),
	                cls.classId,
	                cls.title,
	                cls.leader.name,
	                cls.leader.nickname,
	                cls.createdAt,
	                cls.round1Date,
	                calculatedEndDate,
	                getStatusCase(cls, today, calculatedEndDate)))
	        .from(cls)
	        .where(builder)
	        .offset(pageable.getOffset())
	        .limit(pageable.getPageSize())
	        .orderBy(cls.createdAt.desc())
	        .fetch();

	    long total = queryFactory
	        .select(cls.count())
	        .from(cls)
	        .where(builder)
	        .fetchOne();

	    return new PageImpl<>(content, pageable, total);
	}

	@Override
	public long countClasses(String titleKeyword, String leaderKeyword, String status,
	                         LocalDate fromDate, LocalDate toDate) {
	    QClassEntity cls = QClassEntity.classEntity;
	    LocalDate today = LocalDate.now();

	    DateExpression<LocalDate> calculatedEndDate =
	    	    new CaseBuilder()
	    	        .when(cls.round4Date.isNotNull()).then(cls.round4Date)
	    	        .when(cls.round3Date.isNotNull()).then(cls.round3Date)
	    	        .when(cls.round2Date.isNotNull()).then(cls.round2Date)
	    	        .otherwise(cls.round1Date);


	    BooleanBuilder builder = new BooleanBuilder();

	    if (titleKeyword != null && !titleKeyword.isEmpty()) {
	        builder.and(cls.title.containsIgnoreCase(titleKeyword));
	    }
	    
	    if (leaderKeyword != null && !leaderKeyword.isEmpty()) {
	        builder.and(cls.leader.name.containsIgnoreCase(leaderKeyword));
	    }

	    if (leaderKeyword != null && !leaderKeyword.isEmpty()) {
	        builder.and(cls.leader.nickname.containsIgnoreCase(leaderKeyword));
	    }

	    if (status != null) {
	        switch (status) {
	            case "모집중":
	                builder.and(cls.round1Date.gt(today));
	                break;
	            case "진행중":
	                builder.and(cls.round1Date.loe(today).and(calculatedEndDate.goe(today)));
	                break;
	            case "종료":
	                builder.and(calculatedEndDate.lt(today));
	                break;
	        }
	    }

	    if (fromDate != null && toDate != null) {
	        builder.and(cls.createdAt.between(fromDate.atStartOfDay(), toDate.atTime(23, 59, 59)));
	    }

	    return queryFactory.select(cls.count())
	            .from(cls)
	            .where(builder)
	            .fetchOne();
	}

	private StringExpression getStatusCase(
	        QClassEntity cls, LocalDate today, DateExpression<LocalDate> calculatedEndDate) {
	    return new CaseBuilder()
	            .when(cls.round1Date.gt(today)).then("모집중")
	            .when(cls.round1Date.loe(today).and(calculatedEndDate.goe(today))).then("진행중")
	            .otherwise("종료");
	}


}
