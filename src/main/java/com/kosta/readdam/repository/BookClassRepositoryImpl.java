package com.kosta.readdam.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.entity.QClassEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BookClassRepositoryImpl implements BookClassRepository {
	
	private final JPAQueryFactory queryFactory;
	
	@Override
	public List<ClassEntity> findByBookTitleAndAuthor(String title, String author) {
        QClassEntity c = QClassEntity.classEntity;
        BooleanBuilder builder = new BooleanBuilder();

        builder.or(c.round1Bookname.eq(title).and(c.round1Bookwriter.contains(author)));
        builder.or(c.round2Bookname.eq(title).and(c.round2Bookwriter.contains(author)));
        builder.or(c.round3Bookname.eq(title).and(c.round3Bookwriter.contains(author)));
        builder.or(c.round4Bookname.eq(title).and(c.round4Bookwriter.contains(author)));

        return queryFactory.selectFrom(c)
                .where(builder)
                .fetch();
    }
	
	
}
