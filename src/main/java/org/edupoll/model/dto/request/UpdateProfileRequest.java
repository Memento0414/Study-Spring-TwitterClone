package org.edupoll.model.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/*
 * 서블릿으로 처리할때랑은 다르게 Spring boot에서는
 * Multipart 요청도 처리 할 수 있게 기본 설정이 되어있다.
 * 
 * File 은 타입을 MultipartFile
 * Text 는 알아서 자료형
 */
@Data
public class UpdateProfileRequest {
	
	private String name;
	
	private MultipartFile profile;
	

		
}
