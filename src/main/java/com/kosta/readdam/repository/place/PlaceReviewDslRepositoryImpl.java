package com.kosta.readdam.repository.place;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.kosta.readdam.dto.PlaceReviewDto;
import com.kosta.readdam.entity.PlaceReview;
import com.kosta.readdam.entity.QPlaceReview;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PlaceReviewDslRepositoryImpl implements PlaceReviewDslRepository {
	private final JPAQueryFactory queryFactory;


    @Override
	public Page<PlaceReviewDto> findVisibleReviews(Integer placeId, String username, int page, int size) {
        QPlaceReview r = QPlaceReview.placeReview;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(r.place.placeId.eq(placeId));

        if (username != null) {
            builder.and(
                r.isHide.eq(false)
                .or(r.user.username.eq(username))
            );
        } else {
            builder.and(r.isHide.eq(false));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));

        List<PlaceReviewDto> content = queryFactory
            .selectFrom(r)
            .where(builder)
            .orderBy(r.regTime.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch()
            .stream()
            .map(PlaceReview::toDto)
            .collect(Collectors.toList());

        Long total = queryFactory
            .select(r.count())
            .from(r)
            .where(builder)
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
