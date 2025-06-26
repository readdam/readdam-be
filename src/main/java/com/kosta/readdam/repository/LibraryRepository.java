package com.kosta.readdam.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kosta.readdam.entity.Library;

public interface LibraryRepository extends JpaRepository<Library, Integer> {

	List<Library> findByUser_Username(String username);

	void deleteByLibraryIdAndUser_Username(Integer libraryId, String username);
	
	Optional<Library> findByUser_UsernameAndName(String username, String name);

	@Modifying
	@Transactional
	@Query("UPDATE Library l SET l.isShow = :isShow WHERE l.user.username = :username")
	int updateIsShowByUsername(@Param("username") String username, @Param("isShow") Integer isShow);

}
