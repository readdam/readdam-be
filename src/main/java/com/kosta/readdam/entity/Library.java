package com.kosta.readdam.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.kosta.readdam.dto.LibraryDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "library")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Library {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name  = "library_id", nullable = false, updatable = false)
	private Integer libraryId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "username", nullable = false)
	private User user;
	
	@Column(name="name", nullable = false)
	private String name;
	
	@Column(name="is_show", nullable = false)
	private Integer isShow;
	
	@OneToMany(mappedBy = "library", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<LibraryBook> libraryBooks = new ArrayList<>();
	
	public static LibraryDto fromEntity(Library library) {
	    return LibraryDto.builder()
	            .libraryId(library.getLibraryId())
	            .username(library.getUser().getUsername()) 
	            .name(library.getName())
	            .isShow(library.getIsShow())
	            .build();
	}

}
