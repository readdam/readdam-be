package com.kosta.readdam.dto;

import java.time.LocalDateTime;

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
public class WriteShortLikeDto {

    private Integer likeId;
    private String username;
    private Integer writeshortId;
    private LocalDateTime date;

}
