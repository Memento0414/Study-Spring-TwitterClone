package org.edupoll.controller;

import java.util.Base64;

import org.edupoll.exception.AlreadyVerifiedException;
import org.edupoll.exception.CertificationException;
import org.edupoll.exception.CreateException;
import org.edupoll.exception.ExistUserEmailException;
import org.edupoll.exception.PasswordValidException;
import org.edupoll.exception.VerifyCodeException;
import org.edupoll.model.dto.request.CodeIssuanceRequest;
import org.edupoll.model.dto.request.UserJoinRequestData;
import org.edupoll.model.dto.request.ValidateUserRequest;
import org.edupoll.model.dto.request.VarifyCodeRequest;
import org.edupoll.model.dto.response.ValidateUserResponse;
import org.edupoll.model.dto.response.VerifyEmailResponse;
import org.edupoll.service.JWTService;
import org.edupoll.service.UserService;
import org.edupoll.service.VerificationCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {

	private final UserService userService;
	
	private final VerificationCodeService verificationCodeService;
	
	private final JWTService jwtService;
	//신규 유저 추가해주는 API(완료)
	@PostMapping("/join")
	public ResponseEntity<Void>userJoinHandle(@Valid UserJoinRequestData dto) throws CreateException, VerifyCodeException{
		
	
			 userService.registerNewUser(dto);
			return new ResponseEntity<>(HttpStatus.CREATED);
		
	}
	//이메일 사용가능한지 아닌지 확인해주는 API(완료)
	@GetMapping("/available")
	public ResponseEntity<Void>avaiableHandle(@Valid CodeIssuanceRequest dto) throws ExistUserEmailException {
		
	
			userService.emailAvailableCheck(dto);
			
			return new ResponseEntity<>(HttpStatus.OK);
		
	}
	
	
	
	// 인증 받고자하는 이메일에 인증코드를 발송(완료)
	@PostMapping("/verify-email")
	public ResponseEntity<VerifyEmailResponse> verifyEmailHandle(@Valid CodeIssuanceRequest req) throws MessagingException, VerifyCodeException, AlreadyVerifiedException{
		
		verificationCodeService.codeIssuanceRequest(req);
	 
		var resp = new VerifyEmailResponse( 200, "이메일 인증코드가 정상 발급되었습니다.");
	 return new ResponseEntity<>(resp, HttpStatus.OK);
	}
	
	
	
	
	
	
	//이메일 인증 처리하는 과정에서 인증이 성공하였을 때 state를 Success로 변경(완료)
	@PatchMapping("/verify-email")
	public ResponseEntity<Void> verifyCodeHandle(@Valid VarifyCodeRequest req) throws VerifyCodeException {
		

		userService.verifySpecificCode(req);
	 
		
	 return new ResponseEntity<>(HttpStatus.OK);
	}
	
	//가입된 유저인지 확인한 후 토큰을 생성시켜주는 API
	@PostMapping("/validate")
	public ResponseEntity<ValidateUserResponse> loginUserHandle(@Valid ValidateUserRequest dto) throws CertificationException, PasswordValidException{
		
		userService.isVailidUser(dto);
		
		String token = jwtService.createToken(dto.getEmail());
		
		log.info("token = {} " , token);
		
		var response = new ValidateUserResponse(200,token, dto.getEmail());
		
		return new ResponseEntity<>(response, HttpStatus.OK);
		
	}
	
}
