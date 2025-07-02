package com.kosta.readdam.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.kosta.readdam.dto.BookListDto;
import com.kosta.readdam.entity.QBook;
import com.kosta.readdam.entity.QBookList;
import com.kosta.readdam.entity.enums.BookListCategory;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class BookListDslRepository {

    private final JPAQueryFactory query;

    public List<BookListDto> findBookListWithStats(BookListCategory category) {
        QBookList bl = QBookList.bookList;
        QBook book = QBook.book;

        return query
            .select(Projections.constructor(
                BookListDto.class,
                bl.id,
                bl.title,
                bl.isbn,
                bl.imageName,
                bl.author,
                bl.publisher,
                bl.ranking,
                bl.category,
                book.reviewCnt.coalesce(0),
                book.rating.coalesce(BigDecimal.ZERO)
            ))
            .from(bl)
            .leftJoin(book).on(bl.isbn.eq(book.bookIsbn))
            .where(bl.category.eq(category))
            .orderBy(bl.ranking.asc())
            .fetch();
    }

    public Page<BookListDto> findBookListPageWithStats(BookListCategory category, PageRequest pageable) {
        QBookList bl = QBookList.bookList;
        QBook book = QBook.book;

        QueryResults<BookListDto> qr = query
            .select(Projections.constructor(
                BookListDto.class,
                bl.id,
                bl.title,
                bl.isbn,
                bl.imageName,
                bl.author,
                bl.publisher,
                bl.ranking,
                bl.category,
                book.reviewCnt.coalesce(0),
                book.rating.coalesce(BigDecimal.ZERO)
            ))
            .from(bl)
            .leftJoin(book).on(bl.isbn.eq(book.bookIsbn))
            .where(bl.category.eq(category))
            .orderBy(bl.ranking.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();

        return new PageImpl<>(
            qr.getResults(),
            pageable,
            qr.getTotal()
        );
    }
}
