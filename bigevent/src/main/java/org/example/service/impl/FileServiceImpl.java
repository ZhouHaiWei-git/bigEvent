package org.example.service.impl;

import org.example.mapper.FileInfoMapper;
import org.example.pojo.FileInfo;
import org.example.pojo.User;
import org.example.service.IFileService;
import org.example.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class FileServiceImpl implements IFileService {

	@Value("${file.storage.root:./fileDownload}")
	private String storageRoot;

	@Autowired
	private FileInfoMapper fileInfoMapper;

	@Override
	public FileInfo upload(MultipartFile file) throws IOException {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("文件不能为空");
		}

		String originalName = file.getOriginalFilename();
		if (!StringUtils.hasText(originalName)) {
			originalName = "unknown";
		}

		String dateFolder = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
		String ext = "";
		int idx = originalName.lastIndexOf('.');
		if (idx >= 0 && idx < originalName.length() - 1) {
			ext = originalName.substring(idx);
		}

		String storedName = UUID.randomUUID().toString().replace("-", "") + ext;
		Path dir = Path.of(storageRoot, dateFolder);
		Files.createDirectories(dir);
		Path target = dir.resolve(storedName);

		String sha256;
		try (InputStream in = file.getInputStream()) {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			try (DigestInputStream dis = new DigestInputStream(in, md)) {
				Files.copy(dis, target, StandardCopyOption.REPLACE_EXISTING);
			}
			sha256 = HexFormat.of().formatHex(md.digest());
		} catch (Exception e) {
			Files.deleteIfExists(target);
			if (e instanceof IOException) {
				throw (IOException) e;
			}
			throw new RuntimeException("文件保存失败", e);
		}

		FileInfo info = new FileInfo();
		info.setOriginalName(originalName);
		info.setStoredName(storedName);
		info.setRelativePath(dateFolder + "/" + storedName);
		info.setContentType(file.getContentType());
		info.setSize(file.getSize());
		info.setSha256(sha256);

		User user = ThreadLocalUtil.get();
		if (user != null) {
			info.setCreateUser(user.getId());
		}

		fileInfoMapper.insert(info);
		return info;
	}

	@Override
	public FileInfo findById(Long id) {
		return fileInfoMapper.findById(id);
	}

	@Override
	public Path resolvePath(FileInfo info) {
		if (info == null) {
			return null;
		}
		Path root = Path.of(storageRoot).toAbsolutePath().normalize();
		Path resolved = root.resolve(info.getRelativePath()).normalize();
		if (!resolved.startsWith(root)) {
			throw new IllegalArgumentException("非法文件路径");
		}
		return resolved;
	}
}
