package org.edupoll.controller;

import java.io.IOException;

import org.edupoll.exception.NotExistUserException;
import org.edupoll.exception.PasswordValidException;
import org.edupoll.exception.VerifyCodeException;
import org.edupoll.model.dto.UserWrapper;
import org.edupoll.model.dto.request.EmailDeleteValidateRequest;
import org.edupoll.model.dto.request.PasswordChangeRequest;
import org.edupoll.model.dto.request.UpdateProfileRequest;
import org.edupoll.model.dto.response.ErrorResponse;
import org.edupoll.model.dto.response.LogonUserInfoResponse;
import org.edupoll.repository.UserRepository;
import org.edupoll.service.JWTService;
import org.edupoll.service.KakaoAPIService;
import org.edupoll.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.transaction.NotSupportedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user/private")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class PrivateControlller {

	private final UserService userService;
	private final JWTService jwtService;
	private final KakaoAPIService kakaoAPIService;
	
	@GetMapping
	public ResponseEntity<?> getLogonUserHandle(Authentication authentication) throws NotExistUserException{
		
		//String tokenEmailValue = jwtService.verifyToken(token);
		log.info("authentication : {}, {}", authentication, authentication.getPrincipal());
		
		String principal = (String)authentication.getPrincipal();
		
		UserWrapper wrapper= userService.searchUserByEmail(principal);
		
		LogonUserInfoResponse response = new LogonUserInfoResponse(200, wrapper);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PatchMapping
	public ResponseEntity<ErrorResponse> updatePasswordHandle(@AuthenticationPrincipal String principal, PasswordChangeRequest dto) throws PasswordValidException{
		
		if(principal == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
//		String email = jwtService.verifyToken(principal);
		
		userService.changePassword(dto, principal);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	

	@DeleteMapping
	public ResponseEntity<Void> deleteEmailHandle(@AuthenticationPrincipal String principal, EmailDeleteValidateRequest dto) throws  PasswordValidException, VerifyCodeException, JsonMappingException, JsonProcessingException, NotExistUserException{
//		String email = jwtService.verifyToken(token);
		
		if(principal.endsWith("@kakao.com") ) {
			//DB에서 데이터 삭제
			kakaoAPIService.deleteSocialUser(principal);
			// 카카오에서 ulink 요청 (access Token 필요)
	
			userService.deleteSpecificSocialUser(principal);
		}else {
			
			//자체 관리 중인 유저 삭제하기 
			userService.deleteEmail(principal, dto);
			
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	//사용자(프로필 이미지 / 이름) 엡데이트 처리할 API
	//파일 업로드는 컨텐츠타입이  multipart/form-data로 들어옴.
	//(file과 text 유형이 섞여 있음.)
	@PostMapping("/info")
	public ResponseEntity<?> updateProfileHandle(@AuthenticationPrincipal String principal, UpdateProfileRequest req) throws IOException, NotSupportedException, NotExistUserException{
	
//		UserWrapper wrapper = userService.modifySpecificUser(emailValue, req);
		
		userService.modifySpecificUser(principal, req);
		var wrapper = userService.searchUserByEmail(principal);
		
		var repsonse = new LogonUserInfoResponse(200, wrapper);
		
		return new ResponseEntity<LogonUserInfoResponse> (repsonse, HttpStatus.OK);
	}
}
