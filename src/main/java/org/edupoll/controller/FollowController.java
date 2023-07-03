package org.edupoll.controller;

import org.edupoll.model.dto.response.ErrorResponse;
import org.edupoll.service.JWTService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j //lombok을 사용하면 로그를 남기는 어노테이션

public class FollowController {
	
	
	private final JWTService jwtService;
	
	@GetMapping("/{user}/following")
	public ResponseEntity<ErrorResponse> getFollowingList(@PathVariable String user, @RequestHeader(name="token") String token){
		
		
		if(token == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		//토큰 검증
		jwtService.verifyToken(token);
		
		
		
		return new ResponseEntity<> (HttpStatus.OK);
	}
	
	
//	@GetMapping("/{user}/follower")
	
	
}
