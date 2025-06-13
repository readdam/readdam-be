package com.kosta.readdam.dto;

import com.kosta.readdam.entity.Library;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryDto {

    private Integer libraryId;
    private String username;  
    private String name;
    private Integer isShow;

    public Library toEntity(com.kosta.readdam.entity.User user) {
        return Library.builder()
                .libraryId(this.libraryId)
                .user(user)
                .name(this.name)
                .isShow(this.isShow)
                .build();
    }
}
