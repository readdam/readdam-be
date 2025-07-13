package com.kosta.readdam.service.admin;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.BannerDto;

public interface AdminBannerService {
    List<BannerDto> findAll();

    BannerDto findById(Integer bannerId);

    BannerDto save(BannerDto bannerDto, MultipartFile ifile);

    BannerDto update(Integer bannerId, BannerDto bannerDto, MultipartFile ifile);

    void delete(Integer bannerId);

    void changeCurrentBanner(Integer bannerId);
}
