package org.example.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileInfo {
	private Long id;
	private String originalName;
	private String storedName;
	private String relativePath;
	private String contentType;
	private Long size;
	private String sha256;
	private Integer createUser;
	private LocalDateTime createTime;
}
