package org.edupoll.controller;

import org.edupoll.model.dto.KakaoAccount;
import org.edupoll.model.dto.request.KakaoOAuthorizeCallbackRequest;
import org.edupoll.model.dto.request.ValidKakaoRequest;
import org.edupoll.model.dto.response.KakaoAccessTokenWrapper;
import org.edupoll.model.dto.response.OAuthSignResponse;
import org.edupoll.model.dto.response.ValidateUserResponse;
import org.edupoll.service.JWTService;
import org.edupoll.service.KakaoAPIService;
import org.edupoll.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/oauth")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j

public class OAuthController {
	
		private final KakaoAPIService kakaoAPIService;
		
		private final UserService userService;
		private final JWTService jwtService;
		
		@Value("${kakao.restapi.key}")
		String kakaoRestApiKey;
		
		@Value("${kakao.redirect.url}")
		String kakaoRedrectUrl;
	
		
		
		//카카오 인증을 할 수 있는 주소를 보내주는 API(완)
		@GetMapping("/kakao")
		public ResponseEntity<OAuthSignResponse> oAthKakaoHandle() {
			var response = new OAuthSignResponse(200, "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+kakaoRestApiKey+"&redirect_uri="+ kakaoRedrectUrl);
			
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
		
		// 이 부분은 카카오 로그인 후 코드를 받는 곳인데 이 부분은 프론트에서 처리를 하기 때문에 나중에 백에서 제외
		// 프론트에서 받은 코드를 백엔드로 전달해주는 방식으로 바뀌게 된다.
		/*
		@GetMapping("/kakao/callback")
		public ResponseEntity<Void> oAuthKakaoCallbackHandle(KakaoOAuthorizeCallbackRequest req) {
			
			log.info("code = {} " , req.getCode());
			
			
			return new ResponseEntity<>(HttpStatus.OK);
			
		}
		*/
		//카카오 인증 코드로 사용자 정보를 얻어내는 API(완)
		@PostMapping("/kakao")
		public ResponseEntity<ValidateUserResponse> oauthKakaoPostHandle(ValidKakaoRequest req) throws Exception{
			
			KakaoAccessTokenWrapper wrapper =kakaoAPIService.getAccessToken(req.getCode());
		
			KakaoAccount account =kakaoAPIService.getUserInfo(wrapper.getAccessToken());
			userService.updateKakaoUser(account, wrapper.getAccessToken());
			
			log.info("kakaoUserInfo = {}", account.toString());
			
			String token = jwtService.createToken(account.getEmail());
			
			ValidateUserResponse response = new ValidateUserResponse(200, token, account.getEmail());
			
			
			return new ResponseEntity<>(response ,HttpStatus.OK);
		}
		
		@PostMapping("/kakao/delete")
		public ResponseEntity<Void> deletePostHandle(ValidKakaoRequest req){
			
			
			
			
			return new ResponseEntity<Void> (HttpStatus.OK);
			
		}
		
}
