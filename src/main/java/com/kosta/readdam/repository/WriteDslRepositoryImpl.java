package com.kosta.readdam.repository;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.kosta.readdam.dto.WriteSearchRequestDto;
import com.kosta.readdam.entity.QWrite;
import com.kosta.readdam.entity.Write;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class WriteDslRepositoryImpl implements WriteDslRepository {

    private final JPAQueryFactory queryFactory;

    public WriteDslRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
	
	@Override
	public Page<Write> searchWrites(WriteSearchRequestDto requestDto, Pageable pageable) {
		 QWrite write = QWrite.write;

	        BooleanBuilder builder = new BooleanBuilder();
	        builder.and(write.isHide.eq(false));

	        if (requestDto.getType() != null && !requestDto.getType().equals("all")) {
	            builder.and(write.type.eq(requestDto.getType()));
	        }

	        if (requestDto.getStatus() != null && !requestDto.getStatus().equals("all")) {
	            if (requestDto.getStatus().equals("open")) {
	                builder.and(write.endDate.gt(LocalDateTime.now()));
	            } else if (requestDto.getStatus().equals("closed")) {
	                builder.and(write.endDate.loe(LocalDateTime.now()));
	            }
	        }

	        if (requestDto.getKeyword() != null && !requestDto.getKeyword().isBlank()) {
	            String keyword = requestDto.getKeyword();
	            builder.and(
	                write.title.containsIgnoreCase(keyword)
	                .or(write.content.containsIgnoreCase(keyword))
	                .or(write.user.username.contains(keyword))
	                .or(write.tag1.containsIgnoreCase(keyword))
	                .or(write.tag2.containsIgnoreCase(keyword))
	                .or(write.tag3.containsIgnoreCase(keyword))
	                .or(write.tag4.containsIgnoreCase(keyword))
	                .or(write.tag5.containsIgnoreCase(keyword))
	            );
	        }

	        // 정렬 조건
	        OrderSpecifier<?> order;

	        if ("view".equals(requestDto.getSort())) {
	            order = write.viewCnt.desc();
	        } else {
	            order = write.regDate.desc(); // 기본: 최신순
	        }

	        List<Write> result = queryFactory
	            .selectFrom(write)
	            .where(builder)
	            .orderBy(order)
	            .offset(pageable.getOffset())
	            .limit(pageable.getPageSize())
	            .fetch();

	        long count = queryFactory
	            .select(write.count())
	            .from(write)
	            .where(builder)
	            .fetchOne();

	        return new PageImpl<>(result, pageable, count);
	    }
	}