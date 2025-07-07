package com.kosta.readdam.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.readdam.dto.WriteDto;
import com.kosta.readdam.dto.WriteSearchRequestDto;
import com.kosta.readdam.entity.Write;

public interface WriteDslRepository {
    Page<Write> searchWrites(WriteSearchRequestDto requestDto, Pageable pageable);
    List<WriteDto> searchForAll(String keyword, String sort, int limit);
}
