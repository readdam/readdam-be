package com.kosta.readdam.entity;

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
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.kosta.readdam.dto.ReservationDto;
import com.kosta.readdam.entity.enums.ReservationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id", nullable = false, updatable = false)
    private Integer reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_room_id", nullable = false)
    private PlaceRoom placeRoom;

    @Column(name = "participant_count", nullable = false)
    private Integer participantCount;

    @Column(name = "reserver_name", nullable = false)
    private String reserverName;

    @Column(name = "reserver_phone", nullable = false)
    private String reserverPhone;

    @Column(name = "request_message", columnDefinition = "TEXT")
    private String requestMessage;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'PENDING'")
    private ReservationStatus status = ReservationStatus.PENDING; ;
    
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime createdAt;

    public void setClassEntity(ClassEntity c) {
        this.classEntity = c;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;
   
    // ↓ ReservationDetail 과의 매핑 추가 ↓
    @OneToMany(
        mappedBy = "reservation",
        cascade = CascadeType.REMOVE,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<ReservationDetail> details = new ArrayList<>();

    public ReservationDto toDto() {
        return ReservationDto.builder()
                .reservationId(reservationId)
                .username(user.getUsername())
                .placeRoomId(placeRoom.getPlaceRoomId())
                .participantCount(participantCount)
                .reserverName(reserverName)
                .reserverPhone(reserverPhone)
                .requestMessage(requestMessage)
                .status(status)
                .createdAt(getCreatedAt())
                .classId(classEntity != null ? classEntity.getClassId() : null)
                .build();
    }
}
