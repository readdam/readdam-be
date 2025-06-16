package com.kosta.readdam.controller;

import java.io.File;
import java.io.FileInputStream;

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
			FileInputStream fis = new FileInputStream(new File(iuploadPath, filename));
			FileCopyUtils.copy(fis, response.getOutputStream());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
