package org.edupoll.service;


import java.net.URI;

import org.edupoll.exception.NotExistUserException;
import org.edupoll.model.dto.KakaoAccount;
import org.edupoll.model.dto.response.KakaoAccessTokenWrapper;
import org.edupoll.model.entity.User;
import org.edupoll.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.NotAcceptableStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KakaoAPIService {
	@Value("${kakao.restapi.key}")
	String kakaoRestApiKey;
	
	@Value("${kakao.redirect.url}")
	String kakaoRedrectUrl;
	
	@Autowired
	UserRepository userRepository;

	public KakaoAccessTokenWrapper getAccessToken(String code) {
		// 콜백으로 받은 인증코드를 이용해서 카카오에서 유저를 받아와야 한다.

		String tokenURL = "https://kauth.kakao.com/oauth/token";

		RestTemplate template = new RestTemplate(); 
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("client_id", kakaoRestApiKey);
		body.add("redirect_uri", kakaoRedrectUrl);
		body.add("code", code);
		
		/*
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

		ResponseEntity<KakaoAccessTokenWrapper> result = template.postForEntity(tokenURL, entity,
														 KakaoAccessTokenWrapper.class);
		*/
		System.out.println(body);
		RequestEntity<?> request = new RequestEntity<>(body,headers, HttpMethod.POST, URI.create(tokenURL));
		ResponseEntity<KakaoAccessTokenWrapper> response = template.exchange(request, KakaoAccessTokenWrapper.class);//
		
		log.info("result.statuscode = {} ", response.getStatusCode());
		log.info("result.body = {}", response.getBody());

		return response.getBody();
	}
	/*
	 * Spring framework 에서 REST API를 호출하는 걸 도와주기 위해서
	 * 	1. RestTemplate - 동기(blocking IO) => 코드가 간결해짐
	 * 	2. Webclient - 비동기(Non-blocking IO) => 코드가 길어질 수 있음
	 * 
	 */

	public KakaoAccount getUserInfo(String accessToken) throws Exception {
		// accessToken을 가지고 실제 카카오 사용자의 정보를 가지고 와야하는 메서드

		String dest = "https://kapi.kakao.com/v2/user/me";

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Authorization", "Bearer " + accessToken); // Bearer를 쓸 때 한칸 띄우고 써야하기 때문에 꼭 확인하자
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		RestTemplate template = new RestTemplate();

		RequestEntity<Void> request = new RequestEntity<>(headers, HttpMethod.GET, URI.create(dest));

		ResponseEntity<String> response = template.exchange(request, String.class);//
		log.info("repsonse.statuscode = {} ", response.getStatusCode());
		log.info("repsonse.body = {} ", response.getBody());

		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(response.getBody());

		String email = node.get("id").asText("") + "@kakao.com";
		String nickname = node.get("kakao_account").get("profile").get("nickname").asText();
		String proflieImage = node.get("kakao_account").get("profile").get("profile_image_url").asText();

		return new KakaoAccount(email, nickname, proflieImage);

	}
	//카카오톡 연결 해제를 하는 메서드
	@Transactional
	public void deleteSocialUser(String tokenEmailValue) throws JsonMappingException, JsonProcessingException {
		User found = userRepository.findByEmail(tokenEmailValue).orElseThrow();
		
		String accessToken = found.getSocial();
		
		String unlink = "https://kapi.kakao.com/v1/user/unlink";
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Authorization", "Bearer "+ accessToken);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		RestTemplate template = new RestTemplate();
		
		RequestEntity<Void> request = new RequestEntity<>(headers, HttpMethod.POST, URI.create(unlink));
		ResponseEntity<String> response = template.exchange(request, String.class);
	}
	
}
