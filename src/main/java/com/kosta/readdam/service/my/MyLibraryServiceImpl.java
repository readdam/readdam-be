package com.kosta.readdam.service.my;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public List<LibraryDto> getMyLibraryList(String username) throws Exception{
        User user = userRepository.findById(username)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 사용자 서재 전체 조회
        List<Library> libraries = libraryRepository.findByUser_Username(username);

        // 기본 서재 이름
        List<String> defaultNames = List.of("인생 책", "읽은 책");

        // name 기준으로 빠르게 접근할 수 있도록 Map 변환
        Map<String, Library> libraryMap = libraries.stream()
            .collect(Collectors.toMap(Library::getName, lib -> lib));

        List<LibraryDto> result = new ArrayList<>();

        // 1. 기본 서재 포함 (DB에 없으면 빈 서재로 대체)
        for (String name : defaultNames) {
            if (libraryMap.containsKey(name)) {
                Library lib = libraryMap.get(name);
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
            } else {
                result.add(LibraryDto.builder()
                    .libraryId(null)
                    .username(username)
                    .name(name)
                    .isShow(1)
                    .books(new ArrayList<>())
                    .build());
            }
        }

        // 2. 사용자 커스텀 서재들 추가
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
        Library library = libraryRepository.findById(dto.getLibraryId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 서재입니다."));

        // 1. 이름 수정 (기본 서재는 수정 불가)
        if (!List.of("인생 책", "읽은 책").contains(library.getName())) {
            library.setName(dto.getName());
        }

        // 2. 기존 책 삭제
        libraryBookRepository.deleteAllByLibrary(library);

        // 3. 새 책 저장
        List<LibraryBook> newBooks = dto.getBooks().stream()
            .map(bookDto -> LibraryBook.builder()
                .library(library)
                .isbn(bookDto.getIsbn())
                .title(bookDto.getTitle())
                .authors(bookDto.getAuthors() != null ? String.join(", ", bookDto.getAuthors()) : null)
                .thumbnail(bookDto.getThumbnail())
                .publisher(bookDto.getPublisher())
                .datetime(bookDto.getDatetime())
                .build())
            .collect(Collectors.toList());

        libraryBookRepository.saveAll(newBooks);

        // 4. 반환용 DTO
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
