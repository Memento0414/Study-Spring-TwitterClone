package org.edupoll.model.dto.response;

import org.edupoll.model.dto.UserWrapper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogonUserInfoResponse {

	private int code;
	private UserWrapper user;
}
