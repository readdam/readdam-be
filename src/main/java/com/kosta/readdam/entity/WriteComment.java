package com.kosta.readdam.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import com.kosta.readdam.dto.WriteCommentDto;

import lombok.*;

@Entity
@Table(name = "write_comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WriteComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "write_comment_id", updatable = false, nullable = false)
    private Integer writeCommentId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "write_id", nullable = false)
    private Write write;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Column(nullable = false)
    private Boolean adopted;

    @Column(name = "is_secret", nullable = false)
    private Boolean isSecret;

    @Column(name = "is_hide", nullable = false)
    private Boolean isHide;
    
    public WriteCommentDto toDto() {
    	return WriteCommentDto.builder()
    		        .writeCommentId(writeCommentId)
    		        .content(content)
    		        .isSecret(isSecret)
    		        .adopted(adopted)
                    .regDate(regDate)
    		        .username(user != null ? user.getUsername() : null)
    		        .nickname(user != null ? user.getNickname() : null)
    		        .profileImg(user != null ? user.getProfileImg() : null)
    		        .build();
    		}
}
