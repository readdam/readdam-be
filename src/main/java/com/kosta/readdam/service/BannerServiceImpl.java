package com.kosta.readdam.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.BannerDto;
import com.kosta.readdam.entity.Banner;
import com.kosta.readdam.repository.BannerRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {
	
    private final BannerRepository bannerRepository;

	@Override
	public BannerDto getHomeBanner() {
        Optional<Banner> optionalBanner = bannerRepository.findFirstByIsShowTrue();
        
        if (optionalBanner.isPresent()) {
            Banner banner = optionalBanner.get();
            return BannerDto.from(banner);
        } else {
            return BannerDto.builder()
                    .title("읽고, 담고, 나누는 이야기")
                    .content("당신의 기록이 누군가의 공감이 됩니다.")
                    .img("homeDefaultBanner.png")
                    .isShow(true)
                    .button1(new BannerDto.ButtonDto(true, "글 쓰러 가기", "/writeList"))
                    .button2(new BannerDto.ButtonDto(true, "모임 참여하기", "/classList"))
                    .build();
        }
    }

}
