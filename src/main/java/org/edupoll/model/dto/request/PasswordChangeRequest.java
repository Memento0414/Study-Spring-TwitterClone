package org.edupoll.model.dto.request;

import lombok.Data;

@Data
public class PasswordChangeRequest {
		private String currentPassword;
		private String changePassword;
		
}
