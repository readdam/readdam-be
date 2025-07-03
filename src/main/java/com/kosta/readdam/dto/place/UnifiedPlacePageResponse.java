package com.kosta.readdam.dto.place;

import java.util.List;

import com.kosta.readdam.util.PageInfo2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnifiedPlacePageResponse {
    private List<UnifiedPlaceDto> content;
    private PageInfo2 pageInfo;
}
