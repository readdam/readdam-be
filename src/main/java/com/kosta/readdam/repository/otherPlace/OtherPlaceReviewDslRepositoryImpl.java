package com.kosta.readdam.repository.otherPlace;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.kosta.readdam.dto.OtherPlaceReviewDto;
import com.kosta.readdam.entity.OtherPlaceReview;
import com.kosta.readdam.entity.QOtherPlaceReview;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OtherPlaceReviewDslRepositoryImpl implements OtherPlaceReviewDslRepository {

	private final JPAQueryFactory queryFactory;
	
	@Override
    public Page<OtherPlaceReviewDto> findVisibleReviews(Integer otherPlaceId, String username, int page, int size) {
        QOtherPlaceReview r = QOtherPlaceReview.otherPlaceReview;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(r.otherPlace.otherPlaceId.eq(otherPlaceId));

        if (username != null) {
            builder.andAnyOf(
                    r.isHide.eq(false),
                    r.user.username.eq(username)
            );
        } else {
            builder.and(r.isHide.eq(false));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));

        List<OtherPlaceReviewDto> content = queryFactory
                .selectFrom(r)
                .where(builder)
                .orderBy(r.regTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(OtherPlaceReview::toDto)
                .collect(Collectors.toList());

        long total = queryFactory
        	    .select(r.count())
        	    .from(r)
        	    .where(builder)
        	    .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

}
