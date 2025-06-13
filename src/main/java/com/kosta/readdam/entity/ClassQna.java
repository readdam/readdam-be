package com.kosta.readdam.entity;

import com.kosta.readdam.dto.ClassQnaDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "class_qna")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassQna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_qna_id", nullable = false, updatable = false)
    private Integer classQnaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;

    @Column(name = "is_secret", nullable = false)
    private Boolean isSecret;

    @Column(name = "is_hide", nullable = false)
    private Boolean isHide;

    public ClassQnaDto toDto() {
        return ClassQnaDto.builder()
                .classQnaId(classQnaId)
                .classId(classEntity.getClassId())
                .username(user.getUsername())
                .content(content)
                .answer(answer)
                .regDate(regDate)
                .isSecret(isSecret)
                .isHide(isHide)
                .build();
    }
}
