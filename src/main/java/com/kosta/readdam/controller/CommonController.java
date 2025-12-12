package com.kosta.readdam.controller;


import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CommonController {
	
	@Value("${iupload.path}")
	private String iuploadPath;
	

	@PostConstruct
	public void initDirectories() {
		 new File(iuploadPath).mkdirs();  // 폴더 없으면 자동 생성
	}
	


@RequestMapping("/image")
public void imageView(@RequestParam("filename") String filename, HttpServletResponse response) {
    try {
        File file = new File(iuploadPath, filename);
        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // ✅ Content-Type 자동 설정
        String mimeType = Files.probeContentType(file.toPath());
        response.setContentType(mimeType != null ? mimeType : "application/octet-stream");

        FileInputStream fis = new FileInputStream(file);
        FileCopyUtils.copy(fis, response.getOutputStream());

    } catch(Exception e) {
        e.printStackTrace();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}


}
