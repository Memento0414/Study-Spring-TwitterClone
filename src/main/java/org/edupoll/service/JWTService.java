package org.edupoll.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JWTService {
	

	@Value("${jwt.secret.key}")
	String secretKey;
	
	public String createToken(String email) {
		Algorithm algorithm = Algorithm.HMAC256(secretKey);
		
		
	return JWT.create()
		   .withIssuer("finalApp")
		   .withClaim("email", email)
		   .withIssuedAt(new Date(System.currentTimeMillis()))
		   .withExpiresAt(new Date(System.currentTimeMillis()+1000*60*30))//유효시간 1800초 30분
		   .sign(algorithm);
		
		
	}
	// 발급된 토큰의 유효성 검사
	public String verifyToken(String token) {
		
		Algorithm alogrithm = Algorithm.HMAC256(secretKey);
		var verifier = JWT.require(alogrithm).withIssuer("finalApp").build();
		DecodedJWT decodedJWT = verifier.verify(token);
//		log.info("Issuer = " + decodedJWT.getIssuer());
//		log.info("Email = " + decodedJWT.getClaim("email").asString());
	
		return decodedJWT.getClaim("email").asString();
	}

}
