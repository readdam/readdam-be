package com.kosta.readdam.service.admin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.BannerDto;
import com.kosta.readdam.entity.Banner;
import com.kosta.readdam.repository.BannerRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
@Transactional
public class AdminBannerServiceImpl implements AdminBannerService {

    private final BannerRepository bannerRepository;
    
	@Value("${iupload.path}")
	private String iuploadPath;
	
	private String getUniqueFileName(String originalName) {
	    String saveName = originalName;
	    File upFile = new File(iuploadPath, saveName);

	    int count = 1;
	    String namePart = originalName;
	    String extension = "";

	    int dotIndex = originalName.lastIndexOf(".");
	    if (dotIndex != -1) {
	        namePart = originalName.substring(0, dotIndex);
	        extension = originalName.substring(dotIndex);
	    }

	    while (upFile.exists()) {
	        saveName = namePart + "_" + count + extension;
	        upFile = new File(iuploadPath, saveName);
	        count++;
	    }

	    return saveName;
	}

    @Override
    @Transactional(readOnly = true)
    public List<BannerDto> findAll() {
        return bannerRepository.findAll().stream()
                .map(BannerDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BannerDto findById(Integer bannerId) {
        return bannerRepository.findById(bannerId)
                .map(BannerDto::from)
                .orElseThrow(() -> new EntityNotFoundException("배너를 찾을 수 없습니다."));
    }

	@Override
	public BannerDto save(BannerDto bannerDto, MultipartFile ifile) {
	    Banner entity = bannerDto.toEntity();
	    entity.setIsShow(false); // 무조건 비노출로 등록

	    if (ifile != null && !ifile.isEmpty()) {
	        String newFileName = getUniqueFileName(ifile.getOriginalFilename());
	        try {
	            ifile.transferTo(new File(iuploadPath, newFileName));
	            entity.setImg(newFileName);
	        } catch (IOException e) {
	            throw new RuntimeException(e);
	        }
	    }

	    Banner saved = bannerRepository.save(entity);
	    return BannerDto.from(saved);
	}

	@Override
	public BannerDto update(Integer bannerId, BannerDto bannerDto, MultipartFile ifile) {
	    Banner banner = bannerRepository.findById(bannerId)
	            .orElseThrow(() -> new EntityNotFoundException("배너를 찾을 수 없습니다."));

	    banner.setTitle(bannerDto.getTitle());
	    banner.setTitleText(bannerDto.getTitleText());
	    banner.setContent(bannerDto.getContent());
	    banner.setIsShow(bannerDto.getIsShow());
	    
	    if (bannerDto.getImg() != null && !bannerDto.getImg().isBlank()) {
	        banner.setImg(bannerDto.getImg());
	    }
	    
	    if (bannerDto.getButton1() != null) {
	        banner.setBtn1IsShow(bannerDto.getButton1().getShow());
	        banner.setBtn1Name(bannerDto.getButton1().getText());
	        banner.setBtn1Link(bannerDto.getButton1().getLink());
	    } else {
	        banner.setBtn1IsShow(false);
	        banner.setBtn1Name(null);
	        banner.setBtn1Link(null);
	    }

	    if (bannerDto.getButton2() != null) {
	        banner.setBtn2IsShow(bannerDto.getButton2().getShow());
	        banner.setBtn2Name(bannerDto.getButton2().getText());
	        banner.setBtn2Link(bannerDto.getButton2().getLink());
	    } else {
	        banner.setBtn2IsShow(false);
	        banner.setBtn2Name(null);
	        banner.setBtn2Link(null);
	    }

	    // 새 파일 업로드 시 저장
	    if (ifile != null && !ifile.isEmpty()) {
	        String newFileName = getUniqueFileName(ifile.getOriginalFilename());
	        try {
	            ifile.transferTo(new File(iuploadPath, newFileName));
	            banner.setImg(newFileName);
	        } catch (IOException e) {
	            throw new RuntimeException(e);
	        }
	    }

	    return BannerDto.from(banner);
	}

	@Override
	public void delete(Integer bannerId) {
	    Banner banner = bannerRepository.findById(bannerId)
	            .orElseThrow(() -> new EntityNotFoundException("배너를 찾을 수 없습니다."));
	    bannerRepository.delete(banner);

	}

	@Override
	public void changeCurrentBanner(Integer bannerId) {
        // 모든 배너 노출 false
	    bannerRepository.updateAllIsShowFalse();

        // 선택 배너만 true
        Banner current = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new EntityNotFoundException("배너를 찾을 수 없습니다."));
        current.setIsShow(true);
    }

}
