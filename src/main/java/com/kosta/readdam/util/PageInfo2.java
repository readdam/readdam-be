package com.kosta.readdam.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PageInfo2 {
    private int currentPage;
    private int pageSize;
    private boolean isLastPage;
    private long totalElements;
    private int totalPages;

    public static PageInfo2 from(org.springframework.data.domain.Page<?> page) {
        return new PageInfo2(
            page.getNumber() + 1, // 0-based → 1-based
            page.getSize(),
            page.isLast(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }
}
