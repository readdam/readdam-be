package com.kosta.readdam.repository.otherPlace;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.kosta.readdam.entity.OtherPlace;
import com.kosta.readdam.entity.QOtherPlace;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OtherPlaceRepositoryImpl implements OtherPlaceRepositoryCustom {
	private final JPAQueryFactory queryFactory;

    @Override
    public Page<OtherPlace> findAllByFilter(Pageable pageable, String keyword, String filterBy) {
        QOtherPlace q = QOtherPlace.otherPlace;

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

        List<OtherPlace> content = queryFactory
                .selectFrom(q)
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(q.otherPlaceId.desc())
                .fetch();

        long total = queryFactory
                .select(q.count())
                .from(q)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
