package com.kosta.readdam.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

	@Value("${iupload.path}") // application.properties에서 설정
	private String uploadDir;
	 
	@Override
	public List<String> save(List<MultipartFile> files) {
        List<String> paths = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String uuid = UUID.randomUUID().toString();
                String originalFilename = file.getOriginalFilename();
                String fileName = uuid + "_" + originalFilename;

                Path path = Paths.get(uploadDir, fileName);
                try {
                    Files.createDirectories(path.getParent());
                    file.transferTo(path);
                    paths.add("/upload/" + fileName); // 웹 경로 (프론트에서 접근 가능하도록 설정 필요)
                } catch (IOException e) {
                    throw new RuntimeException("파일 저장 실패: " + fileName, e);
                }
            }
        }

        return paths;
    }

}
