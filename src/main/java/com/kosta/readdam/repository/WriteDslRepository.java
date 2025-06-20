package com.kosta.readdam.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kosta.readdam.dto.WriteSearchRequestDto;
import com.kosta.readdam.entity.Write;

public interface WriteDslRepository {
    Page<Write> searchWrites(WriteSearchRequestDto requestDto, Pageable pageable);
}
