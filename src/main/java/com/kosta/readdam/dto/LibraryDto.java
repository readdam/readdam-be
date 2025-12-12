package com.kosta.readdam.dto;

import java.util.List;

import com.kosta.readdam.entity.Library;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryDto {

    private Integer libraryId;
    private String username;  
    private String name;
    private Integer isShow;
    private List<LibraryBookDto> books;

    public Library toEntity(com.kosta.readdam.entity.User user) {
        return Library.builder()
                .libraryId(this.libraryId)
                .user(user)
                .name(this.name)
                .isShow(this.isShow)
                .build();
    }

}
