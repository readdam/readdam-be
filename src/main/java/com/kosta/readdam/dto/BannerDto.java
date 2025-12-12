package com.kosta.readdam.dto;

import java.time.LocalDateTime;

import com.kosta.readdam.entity.Banner;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerDto {

    private Integer bannerId;
    private String title;
    private String img;
    private Boolean isShow;

    private String titleText;
    private String content;

    private ButtonDto button1;
    private ButtonDto button2;
    
    private LocalDateTime regDate;
    private LocalDateTime updDate;

    public Banner toEntity() {
        return Banner.builder()
                .bannerId(bannerId)
                .title(title)
                .img(img)
                .isShow(isShow)
                .titleText(titleText)
                .content(content)
                .btn1IsShow(button1 != null ? button1.getShow() : false)
                .btn1Name(button1 != null ? button1.getText() : null)
                .btn1Link(button1 != null ? button1.getLink() : null)
                .btn2IsShow(button2 != null ? button2.getShow() : false)
                .btn2Name(button2 != null ? button2.getText() : null)
                .btn2Link(button2 != null ? button2.getLink() : null)
                .build();
    }
    
    public static BannerDto from(Banner entity) {
        BannerDto dto = new BannerDto();
        dto.setBannerId(entity.getBannerId());
        dto.setTitle(entity.getTitle());
        dto.setImg(entity.getImg());
        dto.setTitleText(entity.getTitleText());
        dto.setContent(entity.getContent());
        dto.setIsShow(entity.getIsShow());
        dto.setButton1(
        	    (entity.getBtn1IsShow() != null
        	        || entity.getBtn1Name() != null
        	        || entity.getBtn1Link() != null)
        	        ? new BannerDto.ButtonDto(
        	            entity.getBtn1IsShow(),
        	            entity.getBtn1Name(),
        	            entity.getBtn1Link()
        	        )
        	        : null
        	);
        dto.setButton2(
        	    (entity.getBtn2IsShow() != null
        	        || entity.getBtn2Name() != null
        	        || entity.getBtn2Link() != null)
        	        ? new BannerDto.ButtonDto(
        	            entity.getBtn2IsShow(),
        	            entity.getBtn2Name(),
        	            entity.getBtn2Link()
        	        )
        	        : null
        	);
        dto.setRegDate(entity.getRegDate());
        dto.setUpdDate(entity.getUpdDate());
        return dto;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ButtonDto {
        private Boolean show;
        private String text;
        private String link;
    }
}

