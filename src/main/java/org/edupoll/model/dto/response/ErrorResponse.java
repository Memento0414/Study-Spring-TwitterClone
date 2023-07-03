package org.edupoll.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
		
		private int code;
		private String cause;
		private Long timestamp;
		
		
}
