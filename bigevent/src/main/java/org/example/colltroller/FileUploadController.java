package org.example.colltroller;

import org.example.pojo.FileInfo;
import org.example.pojo.FileSignedUrlResponse;
import org.example.pojo.FileUploadResponse;
import org.example.pojo.Result;
import org.example.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/files")
public class FileUploadController {
    @Autowired
    private IFileService fileService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping
    public Result<FileUploadResponse> upload(MultipartFile file) throws IOException {
        FileInfo info = fileService.upload(file);

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String downloadUrl = baseUrl + "/files/" + info.getId() + "/download";
        String previewUrl = baseUrl + "/files/" + info.getId() + "/preview";

        FileUploadResponse resp = new FileUploadResponse();
        resp.setId(info.getId());
        resp.setOriginalName(info.getOriginalName());
        resp.setContentType(info.getContentType());
        resp.setSize(info.getSize());
        resp.setDownloadUrl(downloadUrl);
        resp.setPreviewUrl(previewUrl);
        return Result.success(resp);
    }

    @GetMapping("/{id}")
    public Result<FileInfo> detail(@PathVariable Long id) {
        FileInfo info = fileService.findById(id);
        if (info == null) {
            return Result.error("文件不存在");
        }
        return Result.success(info);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws IOException {
        return buildFileResponse(id, true);
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<Resource> preview(@PathVariable Long id) throws IOException {
        return buildFileResponse(id, false);
    }

	@GetMapping("/{id}/signed-url")
	public Result<FileSignedUrlResponse> signedUrl(@PathVariable Long id,
											@RequestParam(defaultValue = "300") Long expireSeconds) {
		FileInfo info = fileService.findById(id);
		if (info == null) {
			return Result.error("文件不存在");
		}
		if (expireSeconds == null || expireSeconds <= 0 || expireSeconds > 86400) {
			return Result.error("expireSeconds 参数不合法");
		}

		String fileToken = UUID.randomUUID().toString().replace("-", "");
		long expAt = Instant.now().getEpochSecond() + expireSeconds;
		redisTemplate.opsForValue().set("file:token:" + fileToken, id + ":" + expAt, expireSeconds, TimeUnit.SECONDS);

		String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
		String downloadUrl = baseUrl + "/files/" + id + "/download?fileToken=" + fileToken;
		String previewUrl = baseUrl + "/files/" + id + "/preview?fileToken=" + fileToken;

		FileSignedUrlResponse resp = new FileSignedUrlResponse();
		resp.setId(id);
		resp.setDownloadUrl(downloadUrl);
		resp.setPreviewUrl(previewUrl);
		resp.setExpireSeconds(expireSeconds);
		resp.setExpAt(expAt);
		return Result.success(resp);
	}

    private ResponseEntity<Resource> buildFileResponse(Long id, boolean attachment) throws IOException {
        FileInfo info = fileService.findById(id);
        if (info == null) {
            return ResponseEntity.notFound().build();
        }
        Path path = fileService.resolvePath(info);
        if (path == null || !Files.exists(path) || !Files.isRegularFile(path)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new org.springframework.core.io.FileSystemResource(path);

        String contentType = info.getContentType();
        if (!StringUtils.hasText(contentType)) {
            contentType = Files.probeContentType(path);
        }
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (StringUtils.hasText(contentType)) {
            try {
                mediaType = MediaType.parseMediaType(contentType);
            } catch (Exception ignored) {
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }
        }

        String filename = StringUtils.hasText(info.getOriginalName()) ? info.getOriginalName() : path.getFileName().toString();
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        String dispositionType = attachment ? "attachment" : "inline";
        String contentDisposition = dispositionType + "; filename=\"" + filename.replace("\"", "") + "\"; filename*=UTF-8''" + encoded;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
}
