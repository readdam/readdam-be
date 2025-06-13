package com.kosta.readdam.dto;

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
    private String style;
    private String img;
    private Boolean isShow;
    private Integer seq;
    private String titleText;
    private String content;

    private String btn1Name;
    private String btn1Link;
    private String btn1Color;
    private Boolean btn1IsShow;

    private String btn2Name;
    private String btn2Link;
    private String btn2Color;
    private Boolean btn2IsShow;

    private String backColor;
    private String textColor;

    public Banner toEntity() {
        return Banner.builder()
                .bannerId(bannerId)
                .title(title)
                .style(style)
                .img(img)
                .isShow(isShow)
                .seq(seq)
                .titleText(titleText)
                .content(content)
                .btn1Name(btn1Name)
                .btn1Link(btn1Link)
                .btn1Color(btn1Color)
                .btn1IsShow(btn1IsShow)
                .btn2Name(btn2Name)
                .btn2Link(btn2Link)
                .btn2Color(btn2Color)
                .btn2IsShow(btn2IsShow)
                .backColor(backColor)
                .textColor(textColor)
                .build();
    }
}
