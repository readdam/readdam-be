package com.kosta.readdam.entity;

import com.kosta.readdam.dto.BannerDto;
import lombok.*;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "banner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banner_id", nullable = false, updatable = false)
    private Integer bannerId;

    private String title;
    private String img;

    @Column(name = "is_show")
    private Boolean isShow;

    @Column(name = "title_text")
    private String titleText;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "btn1_name")
    private String btn1Name;

    @Column(name = "btn1_link")
    private String btn1Link;

    @Column(name = "btn1_is_show")
    private Boolean btn1IsShow;

    @Column(name = "btn2_name")
    private String btn2Name;

    @Column(name = "btn2_link")
    private String btn2Link;

    @Column(name = "btn2_is_show")
    private Boolean btn2IsShow;
    
    @Column(name = "reg_date", updatable = false)
    private LocalDateTime regDate;

    @Column(name = "upd_date")
    private LocalDateTime updDate;

    @PrePersist
    public void onCreate() {
        this.regDate = LocalDateTime.now();
        this.updDate = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updDate = LocalDateTime.now();
    }

    public BannerDto toDto() {
        return BannerDto.builder()
                .bannerId(bannerId)
                .title(title)
                .img(img)
                .isShow(isShow)
                .titleText(titleText)
                .content(content)
                .button1(
                        (btn1IsShow != null || btn1Name != null || btn1Link != null)
                            ? new BannerDto.ButtonDto(btn1IsShow, btn1Name, btn1Link)
                            : null
                    )
                    .button2(
                        (btn2IsShow != null || btn2Name != null || btn2Link != null)
                            ? new BannerDto.ButtonDto(btn2IsShow, btn2Name, btn2Link)
                            : null
                    )
                .build();
    }
}
