package com.kosta.readdam.dto;

import com.kosta.readdam.entity.Place;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceDto {

    private Integer placeId;
    private String name;
    private String basicAddress;
    private String detailAddress;
    private Double lat;
    private Double lng;
    private String phone;
    private String introduce;

    private String tag1;
    private String tag2;
    private String tag3;
    private String tag4;
    private String tag5;
    private String tag6;
    private String tag7;
    private String tag8;
    private String tag9;
    private String tag10;

    private String img1;
    private String img2;
    private String img3;
    private String img4;
    private String img5;
    private String img6;
    private String img7;
    private String img8;
    private String img9;
    private String img10;
    
    private long likeCount;
    private boolean liked;
    
    private String type; // 통합검색 시 장소 구분용. "OTHER" = OtherPlace, "PLACE" = Place
    private String id; 
    
    // 통합검색용 : QueryDSL에서 PlaceDto로 바로 Projection 하기 위해 생성자 추가
    public PlaceDto(
    		String id,
            Integer placeId,
            String name,
            String basicAddress,
            String detailAddress,
            String img1,
            String tag1,
            String tag2,
            String tag3,
            String tag4,
            String tag5,
            String type
    ) {
    	this.id = id;
        this.placeId = placeId;
        this.name = name;
        this.basicAddress = basicAddress;
        this.detailAddress = detailAddress;
        this.img1 = img1;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.tag3 = tag3;
        this.tag4 = tag4;
        this.tag5 = tag5;
        this.type = type;
    }

    public Place toEntity() {
        return Place.builder()
                .placeId(placeId)
                .name(name)
                .basicAddress(basicAddress)
                .detailAddress(detailAddress)
                .lat(lat)
                .lng(lng)
                .phone(phone)
                .introduce(introduce)
                .tag1(tag1)
                .tag2(tag2)
                .tag3(tag3)
                .tag4(tag4)
                .tag5(tag5)
                .tag6(tag6)
                .tag7(tag7)
                .tag8(tag8)
                .tag9(tag9)
                .tag10(tag10)
                .img1(img1)
                .img2(img2)
                .img3(img3)
                .img4(img4)
                .img5(img5)
                .img6(img6)
                .img7(img7)
                .img8(img8)
                .img9(img9)
                .img10(img10)
                .build();
    }
}
