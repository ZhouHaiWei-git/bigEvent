package org.example.service;

import org.example.pojo.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface IFileService {
	FileInfo upload(MultipartFile file) throws IOException;

	FileInfo findById(Long id);

	Path resolvePath(FileInfo info);
}
