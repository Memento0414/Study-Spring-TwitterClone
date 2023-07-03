package org.edupoll.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CodeIssuanceRequest {
	@NotNull
	@Email
	private String email;
	
}
