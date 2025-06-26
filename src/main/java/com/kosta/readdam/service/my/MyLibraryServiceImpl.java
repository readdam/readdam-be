package com.kosta.readdam.service.my;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.LibraryBookDto;
import com.kosta.readdam.dto.LibraryDto;
import com.kosta.readdam.entity.Library;
import com.kosta.readdam.entity.LibraryBook;
import com.kosta.readdam.entity.User;
import com.kosta.readdam.repository.LibraryBookRepository;
import com.kosta.readdam.repository.LibraryRepository;
import com.kosta.readdam.repository.UserRepository;

@Service
@Transactional
public class MyLibraryServiceImpl implements MyLibraryService {

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LibraryBookRepository libraryBookRepository;

    @Override
    public List<LibraryDto> getMyLibraryList(String username) throws Exception {
        User user = userRepository.findById(username)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 1) 현재 저장된 서재 전체 조회
        List<Library> libraries = libraryRepository.findByUser_Username(username);

        // 2) 기본 서재 이름 목록
        List<String> defaultNames = List.of("인생 책", "읽은 책");

        // 3) 기본 서재가 없으면 생성
        for (String name : defaultNames) {
            boolean exists = libraries.stream()
                .anyMatch(lib -> name.equals(lib.getName()));
            if (!exists) {
                Library lib = Library.builder()
                    .user(user)
                    .name(name)
                    .isShow(1)
                    .build();
                libraryRepository.save(lib);
                libraries.add(lib);
            }
        }

        // 4) DTO 변환: 기본 서재 순서 유지
        List<LibraryDto> result = new ArrayList<>();
        for (String name : defaultNames) {
            Library lib = libraries.stream()
                .filter(l -> name.equals(l.getName()))
                .findFirst()
                .get();
            List<LibraryBookDto> books = lib.getLibraryBooks().stream()
                .map(LibraryBook::toDto)
                .collect(Collectors.toList());
            result.add(LibraryDto.builder()
                .libraryId(lib.getLibraryId())
                .username(username)
                .name(name)
                .isShow(lib.getIsShow())
                .books(books)
                .build());
        }

        // 5) 커스텀 서재들 추가
        libraries.stream()
            .filter(lib -> !defaultNames.contains(lib.getName()))
            .forEach(lib -> {
                List<LibraryBookDto> books = lib.getLibraryBooks().stream()
                    .map(LibraryBook::toDto)
                    .collect(Collectors.toList());
                result.add(LibraryDto.builder()
                    .libraryId(lib.getLibraryId())
                    .username(username)
                    .name(lib.getName())
                    .isShow(lib.getIsShow())
                    .books(books)
                    .build());
            });

        return result;
    }

    @Override
    public void addLibrary(String username, LibraryDto dto) {
        User user = userRepository.findById(username)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        // 1. 서재 저장
        Library library = Library.builder()
            .name(dto.getName())
            .user(user)
            .isShow(1)
            .build();

        Library savedLibrary = libraryRepository.save(library);

        // 2. 책 목록 저장
        List<LibraryBook> books = dto.getBooks().stream().map(bookDto -> {
            return LibraryBook.builder()
                .library(savedLibrary)
                .isbn(bookDto.getIsbn())
                .title(bookDto.getTitle())
                .authors(String.join(", ", bookDto.getAuthors())) // authors 배열을 문자열로 저장
                .thumbnail(bookDto.getThumbnail())
                .publisher(bookDto.getPublisher())
                .datetime(bookDto.getDatetime())
                .build();
        }).collect(Collectors.toList());

        libraryBookRepository.saveAll(books);
    }


    @Override
    @Transactional
    public LibraryDto updateLibrary(LibraryDto dto) throws Exception {
        // 1) 사용자 엔티티는 dto에 username이 담겨있다고 가정
        User user = userRepository.findById(dto.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Library library;
        if (dto.getLibraryId() == null) {
            // 기본 서재: 이름으로 조회
            library = libraryRepository.findByUser_UsernameAndName(dto.getUsername(), dto.getName())
                .orElseGet(() -> {
                    // 없으면 새로 생성
                    Library lib = Library.builder()
                        .user(user)
                        .name(dto.getName())
                        .isShow(1)
                        .build();
                    return libraryRepository.save(lib);
                });
        } else {
            // 커스텀 서재: ID로 조회
            library = libraryRepository.findById(dto.getLibraryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 서재입니다."));
            // 기본 서재명 변경 금지
            if (!List.of("인생 책", "읽은 책").contains(library.getName())) {
                library.setName(dto.getName());
            }
        }

        // 2) 기존 책 삭제
        libraryBookRepository.deleteAllByLibrary(library);

        // 3) 새 책 저장
        List<LibraryBook> newBooks = dto.getBooks().stream()
            .map(bookDto -> LibraryBook.builder()
                .library(library)
                .isbn(bookDto.getIsbn())
                .title(bookDto.getTitle())
                .authors(bookDto.getAuthors() != null
                         ? String.join(", ", bookDto.getAuthors())
                         : null)
                .thumbnail(bookDto.getThumbnail())
                .publisher(bookDto.getPublisher())
                .datetime(bookDto.getDatetime())
                .build())
            .collect(Collectors.toList());
        libraryBookRepository.saveAll(newBooks);

        // 4) 반환용 DTO
        return LibraryDto.builder()
            .libraryId(library.getLibraryId())
            .username(library.getUser().getUsername())
            .name(library.getName())
            .isShow(library.getIsShow())
            .books(newBooks.stream().map(LibraryBook::toDto).collect(Collectors.toList()))
            .build();
    }



    @Override
    public void deleteLibrary(Integer libraryId) throws Exception{
        Library library = libraryRepository.findById(libraryId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 서재입니다."));

        if (List.of("인생 책", "읽은 책").contains(library.getName())) {
            throw new IllegalStateException("기본 서재는 삭제할 수 없습니다.");
        }

        libraryRepository.delete(library);
    }
    
    @Override
    public List<LibraryDto> toggleShowAll(String username, Integer isShow) throws Exception {
        libraryRepository.updateIsShowByUsername(username, isShow);
        return getMyLibraryList(username);
    }

}
