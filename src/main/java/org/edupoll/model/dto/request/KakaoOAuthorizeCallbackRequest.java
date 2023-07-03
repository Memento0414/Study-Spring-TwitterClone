package org.edupoll.model.dto.request;

import lombok.Data;

@Data
public class KakaoOAuthorizeCallbackRequest {
	private String code;
	private String error;
	
	
}
