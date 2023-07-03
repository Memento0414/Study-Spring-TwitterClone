package org.edupoll.config.support;

import java.io.IOException;
import java.util.List;

import org.edupoll.service.JWTService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter{

	
	
	private final JWTService jwtService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		//사용자가 JWT Token을  안 가지고 왔다면
		String authorization = request.getHeader("Authorization");
		log.info("Authorization header value : {}", authorization);
		
		if(authorization == null) {
			log.info("Did not process authentication request since falled to find authorization header", authorization);
			filterChain.doFilter(request, response); // 통과 시켜주면 된다.
			
			return;
		}
		
		try {
			
			//JWT 유효성 검사해서 통과하면 
			String email = jwtService.verifyToken(authorization);
			Authentication authentication = new UsernamePasswordAuthenticationToken(email, authorization, List.of(new SimpleGrantedAuthority("ROLE_MEMBER")));
			//1. principal ===> 인증 주체자: UserDetails 객체가 보통 설정됨. @AuthenticationPrincipal 했을 때 나오는 값
			//2. Credential ==> 인증에 사용됐던 정보
			//3. 권한(authorities) ==> 권한 : role에 따른 차단 
			
			//여기까지 왔으면 통과 한거다.
			//인증통과 상태로 만드는 것이 목적이다.
			//log.info("{} ", authentication);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.info("email = {}",SecurityContextHolder.getContext().getAuthentication().toString());
		} catch (Exception e) {
			// 토큰이 만료됐거나 위조됐거나 한 상황
			log.error("Verify token fail. {}", e.getMessage());
			throw new BadCredentialsException("Invalid authentication token");
		}
		
		
		filterChain.doFilter(request, response);
	}
}