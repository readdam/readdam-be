package com.kosta.readdam.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.kosta.readdam.entity.Banner;

public interface BannerRepository extends JpaRepository<Banner, Integer> {
    Optional<Banner> findByIsShowTrue();
    
    @Modifying
    @Query("UPDATE Banner b SET b.isShow = false")
    void updateAllIsShowFalse();
}
