package org.example.colltroller;

import org.example.pojo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/upload")
public class FileUploadController {
	@PostMapping
	public Result<String> upload(MultipartFile  file) throws IOException {
		String originalFilename = file.getOriginalFilename();
		//拼接文件名,年月日时分秒+文件名
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String timeStr = now.format(formatter);
		String fileName = timeStr + originalFilename;
		file.transferTo(new File("E:\\test-project\\Java\\BigEvent\\bigevent\\src\\main\\java\\org\\example\\fileDownload\\"+fileName));
        return Result.success("url地址....");
	}
}
