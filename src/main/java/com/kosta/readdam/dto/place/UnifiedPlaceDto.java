package com.kosta.readdam.dto.place;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnifiedPlaceDto {
    private Integer id;
    private String name;
    private String basicAddress;
    private String img1;
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
    private Integer likeCount;
    private String type; // "PLACE" or "OTHER"
    private Double lat;
    private Double lng;
    private Double distanceKm; // ✅ 거리 (km)
    private Boolean liked;
    
    public UnifiedPlaceDto(
    	    Integer id,
    	    String name,
    	    String basicAddress,
    	    String img1,
    	    String tag1,
    	    String tag2,
    	    String tag3,
    	    String tag4,
    	    String tag5,
    	    Long likeCount,
    	    String type,
    	    Double lat,
    	    Double lng
    	) {
    	    this.id = id;
    	    this.name = name;
    	    this.basicAddress = basicAddress;
    	    this.img1 = img1;
    	    this.tag1 = tag1;
    	    this.tag2 = tag2;
    	    this.tag3 = tag3;
    	    this.tag4 = tag4;
    	    this.tag5 = tag5;
    	    this.likeCount = likeCount != null ? likeCount.intValue() : 0;
    	    this.type = type;
    	    this.lat = lat;
    	    this.lng = lng;
    	}

}
