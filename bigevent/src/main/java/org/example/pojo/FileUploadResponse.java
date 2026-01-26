package org.example.pojo;

import lombok.Data;

@Data
public class FileUploadResponse {
	private Long id;
	private String originalName;
	private String contentType;
	private Long size;
	private String downloadUrl;
	private String previewUrl;
}
