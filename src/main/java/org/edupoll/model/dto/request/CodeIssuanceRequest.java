package org.edupoll.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data

public class CodeIssuanceRequest {
	@NotNull
	@Email
	private String email;
	
	
	
}
