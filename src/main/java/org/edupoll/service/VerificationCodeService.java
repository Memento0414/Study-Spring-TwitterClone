package org.edupoll.service;

import java.util.Date;
import java.util.Optional;

import org.edupoll.exception.AlreadyVerifiedException;
import org.edupoll.exception.VerifyCodeException;
import org.edupoll.model.dto.request.CodeIssuanceRequest;
import org.edupoll.model.entity.VerificationCode;
import org.edupoll.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

@Service
public class VerificationCodeService {

	@Autowired
	private VerificationCodeRepository verificationCodeRepository;
	
	@Autowired
	private JavaMailSender mailSender;
	
	/**특정 이메일 인증코드를 보내면서 VerificationCode 테이블 데이터 저장하기 위한 메서드*/
	@Transactional
	public void codeIssuanceRequest(CodeIssuanceRequest dto) throws MessagingException, VerifyCodeException, AlreadyVerifiedException {
		//이미 인증을 통과했는지 확인하는 작업
		Optional<VerificationCode> foundConde = verificationCodeRepository.findByEmail(dto.getEmail());
		if(foundConde.isPresent() && foundConde.get().getState() != null) {
			throw new AlreadyVerifiedException("인증처리가 완료 되었습니다.");
		}
		
		int codeNum = (int)(Math.random()*1_000_000);
		String code = String.format("%06d", codeNum);
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(dto.getEmail());
		message.setFrom("codingtest1023@gmail.com");
		message.setSubject("[인증메일] 인증코드 확인 메일입니다.");
		message.setText(""" 
				
						이메일 본인 인증 절차에 따라 인증코드를 보냅니다.
				
						인증코드: #{code}
				
				
						""".replace("#{code}", code));
		mailSender.send(message);
		
		
		//리포지토리에 코드값 저장
		VerificationCode one = new VerificationCode();
			one.setCode(code);
			one.setEmail(dto.getEmail());
			one.setCreated(new Date());
	
		//인증코드 전후 DB에 저장하는데 비어 있으면 저장, 값이 있다면 DB에 있는 이메일 코드만 바뀌게 처리
		
		Optional<VerificationCode> found = verificationCodeRepository.findByEmail(dto.getEmail());
		
		if (found.isEmpty()) {
			verificationCodeRepository.save(one);

		} else {
			VerificationCode updateCode = found
					.orElseThrow(() -> new VerifyCodeException("인증코드가 일치하지 않거나 존재하지 않습니다. 다시 확인해주세요."));

			updateCode.setCode(one.getCode());

			verificationCodeRepository.save(updateCode);
		}
	}
}
