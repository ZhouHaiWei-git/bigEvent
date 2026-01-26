package org.example.pojo;

import lombok.Data;

@Data
public class FileSignedUrlResponse {
	private Long id;
	private String downloadUrl;
	private String previewUrl;
	private Long expireSeconds;
	private Long expAt;
}
