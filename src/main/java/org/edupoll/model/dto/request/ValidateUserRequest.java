package org.edupoll.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ValidateUserRequest {
	@NotBlank
	private String email;
	@NotBlank
	private String password;
	

	
	
}
