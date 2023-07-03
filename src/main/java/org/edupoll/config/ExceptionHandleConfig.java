package org.edupoll.config;

import org.edupoll.exception.CertificationException;
import org.edupoll.exception.CreateException;
import org.edupoll.exception.PasswordValidException;
import org.edupoll.exception.VerifyCodeException;
import org.edupoll.model.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;

@ControllerAdvice
public class ExceptionHandleConfig {
	
	/**회원가입시 실패했을때 터지는 이셉션 */
	@ExceptionHandler(CreateException.class)
	public ResponseEntity<ErrorResponse> createExceptionHandle(CreateException ex){
		
		ErrorResponse e = new ErrorResponse(400, ex.getMessage(), System.currentTimeMillis());
		
		return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
	}
	/**회원가입시 유효성 검사를 실패했을때*/
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Void> methodArgumentNotValidException(MethodArgumentNotValidException ex){
		
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	/**등록된 이메일이 같지 않으면 에러*/
	@ExceptionHandler(CertificationException.class)
	public ResponseEntity<ErrorResponse> certificationExceptionHandle(CertificationException ex){
		
		ErrorResponse e = new ErrorResponse(400, ex.getMessage(), System.currentTimeMillis());
		
		return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
	}
	/**등록된 패스워드와 일치 하지 않을때 */
	@ExceptionHandler(PasswordValidException.class)
	public ResponseEntity<ErrorResponse> passwordValidExceptionHandle(PasswordValidException ex){
		ErrorResponse response = new ErrorResponse(401, ex.getMessage(), System.currentTimeMillis());
		
		return new ResponseEntity<>( HttpStatus.UNAUTHORIZED);
	}
	/**토큰이 인위적으로 변조되었거나 발급된 토큰이 아닐 때*/
	@ExceptionHandler({JWTDecodeException.class, TokenExpiredException.class})
	public ResponseEntity<ErrorResponse> JWTExceptiionHandle(Exception ex){
		var response = new ErrorResponse(401, "인증토큰이 만료되었거나 손상되었습니다. 다시확인 해주세요.", System.currentTimeMillis());
		
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(VerifyCodeException.class)
	public ResponseEntity<ErrorResponse> verifyCodeExceptionHandle(VerifyCodeException ex){
		var response = new ErrorResponse(400, ex.getMessage(), System.currentTimeMillis());
		
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
}
