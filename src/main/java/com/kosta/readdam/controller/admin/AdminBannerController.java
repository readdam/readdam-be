package com.kosta.readdam.controller.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.readdam.dto.BannerDto;
import com.kosta.readdam.service.admin.AdminBannerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/banner")
public class AdminBannerController {

    private final AdminBannerService bannerService;
    
    @GetMapping
    public ResponseEntity<List<BannerDto>> findAll() {
        return ResponseEntity.ok(bannerService.findAll());
    }

    @GetMapping("/{bannerId}")
    public ResponseEntity<BannerDto> findById(@PathVariable Integer bannerId) {
        return ResponseEntity.ok(bannerService.findById(bannerId));
    }

    /*
     * 신규 등록 (FormData 기반)
     * FormData는 nested DTO를 자동 매핑하지 못하므로
     * ModelAttribute로 BannerDto를 받아봐야 button1, button2는 null로 들어옴
     * → Controller에서 수동으로 DTO를 조립해야 한다
     */
    @PostMapping
    public ResponseEntity<BannerDto> save(
            @RequestParam String title,
            @RequestParam(required = false) String titleText,
            @RequestParam(required = false) String content,
            @RequestParam(required = false, defaultValue = "false") boolean isShow,
            @RequestParam(required = false) String btn1Name,
            @RequestParam(required = false) String btn1Link,
            @RequestParam(required = false, defaultValue = "false") boolean btn1IsShow,
            @RequestParam(required = false) String btn2Name,
            @RequestParam(required = false) String btn2Link,
            @RequestParam(required = false, defaultValue = "false") boolean btn2IsShow,
            @RequestParam(required = false) String img,
            @RequestParam(name = "ifile", required = false) MultipartFile ifile
    ) {
        // ButtonDto 수동 조립
        BannerDto.ButtonDto button1 = new BannerDto.ButtonDto(
                btn1IsShow, btn1Name, btn1Link
        );
        BannerDto.ButtonDto button2 = new BannerDto.ButtonDto(
                btn2IsShow, btn2Name, btn2Link
        );

        BannerDto dto = BannerDto.builder()
                .title(title)
                .titleText(titleText)
                .content(content)
                .isShow(isShow)
                .button1(button1)
                .button2(button2)
                .img(img)
                .build();

        return ResponseEntity.ok(bannerService.save(dto, ifile));
    }

    @PutMapping("/{bannerId}")
    public ResponseEntity<BannerDto> update(
            @PathVariable Integer bannerId,
            @RequestParam String title,
            @RequestParam(required = false) String titleText,
            @RequestParam(required = false) String content,
            @RequestParam(required = false, defaultValue = "false") boolean isShow,
            @RequestParam(required = false) String btn1Name,
            @RequestParam(required = false) String btn1Link,
            @RequestParam(required = false, defaultValue = "false") boolean btn1IsShow,
            @RequestParam(required = false) String btn2Name,
            @RequestParam(required = false) String btn2Link,
            @RequestParam(required = false, defaultValue = "false") boolean btn2IsShow,
            @RequestParam(required = false) String img,
            @RequestParam(name = "ifile", required = false) MultipartFile ifile
    ) {
        // ButtonDto 수동 조립
        BannerDto.ButtonDto button1 = new BannerDto.ButtonDto(
                btn1IsShow, btn1Name, btn1Link
        );
        BannerDto.ButtonDto button2 = new BannerDto.ButtonDto(
                btn2IsShow, btn2Name, btn2Link
        );

        BannerDto dto = BannerDto.builder()
                .title(title)
                .titleText(titleText)
                .content(content)
                .isShow(isShow)
                .button1(button1)
                .button2(button2)
                .img(img)
                .build();

        return ResponseEntity.ok(bannerService.update(bannerId, dto, ifile));
    }
    
    @DeleteMapping("/{bannerId}")
    public ResponseEntity<Void> delete(@PathVariable Integer bannerId) {
        bannerService.delete(bannerId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/current/{bannerId}")
    public ResponseEntity<Void> changeCurrent(@PathVariable Integer bannerId) {
        bannerService.changeCurrentBanner(bannerId);
        return ResponseEntity.ok().build();
    }
}
