package com.kosta.readdam.repository;

//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.util.List;

import com.kosta.readdam.dto.UserSimpleDto;
import com.kosta.readdam.entity.QLibrary;
import com.kosta.readdam.entity.QLibraryBook;
import com.kosta.readdam.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LibraryBookRepositoryImpl implements LibraryBookRepositoryCustom {

	private final JPAQueryFactory queryFactory;

    QLibraryBook libraryBook = QLibraryBook.libraryBook;
    QLibrary library = QLibrary.library;
    QUser user = QUser.user;
    
    @Override
    public List<UserSimpleDto> findTop15UsersByIsbnAndLibraryName(String isbn, String libraryName) {
        return queryFactory
                .select(
                        com.querydsl.core.types.Projections.constructor(
                                UserSimpleDto.class,
                                user.nickname,
                                user.profileImg
                        )
                )
                .from(libraryBook)
                .join(libraryBook.library, library)
                .join(library.user, user)
                .where(
                        library.name.eq(libraryName),
                        libraryBook.isbn.eq(isbn)
                )
                .orderBy(library.libraryId.desc())  // 최신순
                .limit(15)
                .fetch();
    }

}
