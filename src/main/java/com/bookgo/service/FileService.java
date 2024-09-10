package com.bookgo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    // 절대 경로 설정
    private static final String UPLOAD_DIR = "C:/backenddeveloper/JSPCoding/BookGo/src/main/resources/static/uploads/";

    // 단일 파일 저장 메서드
    public String saveFile(MultipartFile file) throws IOException {
        // 업로드 폴더 경로 설정
        Path uploadPath = Paths.get(UPLOAD_DIR);

        // 디렉토리가 존재하지 않으면 생성
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath); // 디렉토리가 없으면 생성
                logger.info("업로드 디렉토리가 생성되었습니다: {}", uploadPath.toAbsolutePath());
            } catch (IOException e) {
                logger.error("디렉토리 생성 실패: {}", e.getMessage());
                throw new IOException("디렉토리 생성에 실패했습니다.", e);
            }
        }

        // 파일명 변경: UUID를 사용하여 고유한 파일명 생성
        String originalFilename = file.getOriginalFilename();
        String extension = "";

        // 파일 확장자 추출
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // UUID로 파일명 생성
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // 파일 저장 경로 설정
        Path filePath = uploadPath.resolve(uniqueFilename);
        File destinationFile = filePath.toFile();

        // 파일 저장
        file.transferTo(destinationFile);

        logger.debug("파일이 저장되었습니다: {}", filePath.toString());
        return uniqueFilename; // 웹에서 접근 가능한 경로 반환
    }
}
