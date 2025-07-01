package com.kosta.readdam.dto;

import java.util.List;

import com.kosta.readdam.util.PageInfo2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class PagedResponse<T> {
	
	private List<T> content;
    private PageInfo2 pageInfo;

}
