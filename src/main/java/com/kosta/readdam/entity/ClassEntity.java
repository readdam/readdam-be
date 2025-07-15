package com.kosta.readdam.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Transient;

import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.entity.enums.ClassStatus;
import com.kosta.readdam.entity.enums.ReservationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "`class`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id", nullable = false, updatable = false)
    private Integer classId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_username", referencedColumnName = "username")
    private User leader;

    private String title;
    private String shortIntro;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private String tag1;
    private String tag2;
    private String tag3;

    private Integer minPerson;
    private Integer maxPerson;

    private String mainImg;
    @Column(columnDefinition = "TEXT")
    private String classIntro;

    private String leaderImg;
    @Column(columnDefinition = "TEXT")
    private String leaderIntro;
    
    @Column(name = "is_readdam")
	@ColumnDefault("0") // 기본값설정 추가
    private Boolean isReaddam; 

    private LocalDate round1Date;
    private String round1PlaceName;
    private String round1PlaceLoc;
    private String round1Img;
    @Column(columnDefinition = "TEXT")
    private String round1Content;
    private String round1Bookname;
    private String round1Bookimg;
    private String round1Bookwriter;
    private Double round1Lat;
    private Double round1Log;

    private LocalDate round2Date;
    private String round2PlaceName;
    private String round2PlaceLoc;
    private String round2Img;
    @Column(columnDefinition = "TEXT")
    private String round2Content;
    private String round2Bookname;
    private String round2Bookimg;
    private String round2Bookwriter;
    private Double round2Lat;
    private Double round2Log;

    private LocalDate round3Date;
    private String round3PlaceName;
    private String round3PlaceLoc;
    private String round3Img;
    @Column(columnDefinition = "TEXT")
    private String round3Content;
    private String round3Bookname;
    private String round3Bookimg;
    private String round3Bookwriter;
    private Double round3Lat;
    private Double round3Log;

    private LocalDate round4Date;
    private String round4PlaceName;
    private String round4PlaceLoc;
    private String round4Img;
    @Column(columnDefinition = "TEXT")
    private String round4Content;
    private String round4Bookname;
    private String round4Bookimg;
    private String round4Bookwriter;
    private Double round4Lat;
    private Double round4Log;
    
    @OneToMany(mappedBy="classEntity", cascade = CascadeType.ALL)
    private List<Reservation> reservations;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'PENDING'")
    private ClassStatus status = ClassStatus.PENDING;
    
    @Transient
    public int getTotalTime() {
        int cnt = 0;
        if (round1Date != null) cnt++;
        if (round2Date != null) cnt++;
        if (round3Date != null) cnt++;
        if (round4Date != null) cnt++;
        return cnt;
    }
    
	public ClassDto toDto() {
		return ClassDto.builder()
				.classId(classId)
                .leaderUsername(leader.getUsername())
                .title(title)
                .shortIntro(shortIntro)
                .createdAt(createdAt)
                .tag1(tag1)
                .tag2(tag2)
                .tag3(tag3)
                .minPerson(minPerson)
                .maxPerson(maxPerson)
                .mainImg(mainImg)
                .classIntro(classIntro)
                .leaderImg(leaderImg)
                .leaderIntro(leaderIntro)
                .isReaddam(isReaddam)
                .round1Date(round1Date)
                .round1PlaceName(round1PlaceName)
                .round1PlaceLoc(round1PlaceLoc)
                .round1Img(round1Img)
                .round1Content(round1Content)
                .round1Bookname(round1Bookname)
                .round1Bookimg(round1Bookimg)
                .round1Bookwriter(round1Bookwriter)
                .round1Lat(round1Lat)
                .round1Log(round1Log)
                .round2Date(round2Date)
                .round2PlaceName(round2PlaceName)
                .round2PlaceLoc(round2PlaceLoc)
                .round2Img(round2Img)
                .round2Content(round2Content)
                .round2Bookname(round2Bookname)
                .round2Bookimg(round2Bookimg)
                .round2Bookwriter(round2Bookwriter)
                .round2Lat(round2Lat)
                .round2Log(round2Log)
                .round3Date(round3Date)
                .round3PlaceName(round3PlaceName)
                .round3PlaceLoc(round3PlaceLoc)
                .round3Img(round3Img)
                .round3Content(round3Content)
                .round3Bookname(round3Bookname)
                .round3Bookimg(round3Bookimg)
                .round3Bookwriter(round3Bookwriter)
                .round3Lat(round3Lat)
                .round3Log(round3Log)
                .round4Date(round4Date)
                .round4PlaceName(round4PlaceName)
                .round4PlaceLoc(round4PlaceLoc)
                .round4Img(round4Img)
                .round4Content(round4Content)
                .round4Bookname(round4Bookname)
                .round4Bookimg(round4Bookimg)
                .round4Bookwriter(round4Bookwriter)
                .round4Lat(round4Lat)
                .round4Log(round4Log)
                
                .build();
	}
	
	@org.hibernate.annotations.Formula(
		      "GREATEST(" +
		      "  COALESCE(round1Date, '1900-01-01')," +
		      "  COALESCE(round2Date, '1900-01-01')," +
		      "  COALESCE(round3Date, '1900-01-01')," +
		      "  COALESCE(round4Date, '1900-01-01')" +
		      ")"
		    )
		    private LocalDate endDate;
	
}
