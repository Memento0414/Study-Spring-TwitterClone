package org.edupoll.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Base64;
import java.util.Optional;

import org.edupoll.exception.CertificationException;
import org.edupoll.exception.CreateException;
import org.edupoll.exception.ExistUserEmailException;
import org.edupoll.exception.NotExistResourceException;
import org.edupoll.exception.NotExistUserException;
import org.edupoll.exception.PasswordValidException;
import org.edupoll.exception.VerifyCodeException;
import org.edupoll.model.dto.KakaoAccount;
import org.edupoll.model.dto.UserWrapper;
import org.edupoll.model.dto.request.CodeIssuanceRequest;
import org.edupoll.model.dto.request.EmailDeleteValidateRequest;
import org.edupoll.model.dto.request.PasswordChangeRequest;
import org.edupoll.model.dto.request.UpdateProfileRequest;
import org.edupoll.model.dto.request.UserJoinRequestData;
import org.edupoll.model.dto.request.ValidateUserRequest;
import org.edupoll.model.dto.request.VarifyCodeRequest;
import org.edupoll.model.entity.ProfileImage;
import org.edupoll.model.entity.User;
import org.edupoll.model.entity.VerificationCode;
import org.edupoll.repository.ProfileImageRepository;
import org.edupoll.repository.UserRepository;
import org.edupoll.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.transaction.NotSupportedException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
	
	private final UserRepository userRepository;
	
	private final VerificationCodeRepository verificationCodeRepository;
	
	private final KakaoAPIService kakaoAPIService; 
	
	private final ProfileImageRepository profileImageRepository;
	
	
	@Value("${upload.server}")
	String uploadServer;
	
	@Value("${upload.basedir}")
	String baseDir;
	
	@Transactional
	/**이메일 인증처리를 거친 후 확인하고 회원가입*/
	public void registerNewUser(UserJoinRequestData dto) throws CreateException, VerifyCodeException {
		
		if (!userRepository.findByEmail(dto.getEmail()).isEmpty()) {

			throw new CreateException("이미 가입된 메일입니다. 다시 확인해주세요.");
		}else {
			
			VerificationCode found = verificationCodeRepository.findTopByEmailOrderByCreatedDesc(dto.getEmail())
									.orElseThrow(()-> new VerifyCodeException("이메일 인증 기록이 존재하지 않습니다."));
			
		if(found.getState() == null) {
			throw new VerifyCodeException("먼저 인증을 해주시길 바랍니다.");
		}
			User entity = new User(dto);
			
			userRepository.save(entity);
		}
	
	}
	
	@Transactional
	/**메일 유효성 검사 메서드*/
	public void isVailidUser(ValidateUserRequest dto) throws CertificationException, PasswordValidException {
		
		User found = userRepository.findByEmail(dto.getEmail()).orElseThrow(()-> new CertificationException("해당 이메일은 등록되어 있지 않습니다."));
		
		if(!found.getPassword().equals(dto.getPassword())) {
			throw new PasswordValidException("비밀번호가 일치하지 않습니다.");
		}

	}
	/**메일에 발급된 인증코드를 VerificationCode 저장된 데이터와 비교하여 인증 처리가 될때 상태값을 넣어주는 메서드*/
	@Transactional
	public void verifySpecificCode(@Valid VarifyCodeRequest req) throws VerifyCodeException {
		
		Optional<VerificationCode> result = verificationCodeRepository.findTopByEmailOrderByCreatedDesc(req.getEmail());
		
		VerificationCode found = result.orElseThrow(()-> new VerifyCodeException("인증코드를 발급 받으세요."));
		long elapsed= System.currentTimeMillis()- found.getCreated().getTime();
		
		if(elapsed > 1000 * 60 * 10) { /// 10분이 지나면 인증코드가 만료되겠끔 
			throw new VerifyCodeException("인증코드 인증시간이 만료 되었습니다.");
		}
		
		if(!found.getCode().equals(req.getCode())) { //발급된 코드와 요청한 코드가 다르면 에러 출력
			throw new VerifyCodeException("발급된 코드는 존재하지 않습니다. 다시 확인해주세요.");
			
		}
		
		found.setState("Success");
		
		
		verificationCodeRepository.save(found);
		
	}
	/**접속한 이메일의 비밀번호를 변경하기 위한 메서드*/
	@Transactional
	public void changePassword(PasswordChangeRequest dto, String email) throws PasswordValidException {
		
		User found  = userRepository.findByEmail(email).get();
		
			//저장된 비밀번호와 현재비밀번호가 일치하면 새비밀번호로 저장
		if(found.getPassword().equals(dto.getCurrentPassword())) {
			
			found.setPassword(dto.getChangePassword());
			
			userRepository.save(found);
		} else {
			 //비밀번호가 일치하지 않으면 익셉션
			throw new PasswordValidException("비밀번호가 일치하지 않습니다.");
		}
	}
	//카카오 연결해제시 유저 테이블 정보를 삭제
	@Transactional
	public void deleteSpecificSocialUser(String userEmail) throws NotExistUserException{
		
		var user = userRepository.findByEmail(userEmail).orElseThrow(() -> new NotExistUserException());
		userRepository.delete(user);
	}
	
	

	@Transactional
	public void deleteEmail(String email, EmailDeleteValidateRequest dto)throws PasswordValidException, VerifyCodeException, JsonMappingException, JsonProcessingException {
		
		log.info("delete dto =" + dto);
		// email을 찾을 수 없다면?
		User found = userRepository.findByEmail(email).orElseThrow(()-> new VerifyCodeException("발급된 코드는 존재하지 않습니다. 다시 확인해주세요."));
		VerificationCode VerifyCode = verificationCodeRepository.findTopByEmailOrderByCreatedDesc(email).get();
		if(found.getSocial() != null && found.getPassword() == null) {
			
			kakaoAPIService.deleteSocialUser(email);
			userRepository.delete(found);
		} else {
			// 삭제시 비밀번호 일치하지 않으면 익셉션
			if (!found.getPassword().equals(dto.getPassword())) {
				log.info("password = {}" + found.getPassword());
				throw new PasswordValidException("비밀번호가 일치 하지 않습니다.");
			}else {
				
				verificationCodeRepository.delete(VerifyCode);
				userRepository.delete(found);
				
			}
			
		}
	
	}
	
	@Transactional
	public void emailAvailableCheck(@Valid CodeIssuanceRequest dto) throws ExistUserEmailException {
		boolean rst = userRepository.existsByEmail(dto.getEmail());
		
		
		if(rst) {
			throw new ExistUserEmailException();
		}
	}

	public void updateKakaoUser(KakaoAccount account, String accessToken) {
		//인증코드를 확보한 카카오유저에 해당하는 정보를 UserRepository에 찾는데
		Optional<User>_user =userRepository.findByEmail(account.getEmail());
		if(_user.isPresent()) {
			User saved= _user.get();
			saved.setSocial(accessToken);
			userRepository.save(saved);
			
		}else {
			//그게 아니면 save
			User user = new User();
			user.setEmail(account.getEmail());
			user.setName(account.getNickname());
			user.setProfileImage(account.getProfileImage());
			user.setSocial(accessToken);
			userRepository.save(user);
		}

		
	}
	//특정유저 정보 업데이터
	@Transactional
	public void modifySpecificUser(String userEmail, UpdateProfileRequest req) throws IOException, NotSupportedException {

//		log.info("dto.name = {}", req.getName());
//		log.info("dto.profile = {} / {} ", req.getProfile().getContentType(), req.getProfile().getOriginalFilename());

		// 리퀘스트 객체에서 파일 정보를 뽑기

		var foundUser = userRepository.findByEmail(userEmail).get(); // 있는지 없는지 체크

		foundUser.setName(req.getName());

		if (req.getProfile() != null) {
			MultipartFile multi = req.getProfile();
			
			// 해당파일이 컨텐츠 타입이 이미지인 경우에만 처리
			if (!multi.getContentType().startsWith("image/")) {
				throw new NotSupportedException("이미지 파일만 설정 가능합니다.");
			}

			String emailEncoded = new String(Base64.getEncoder().encode(userEmail.getBytes()));
			// 파일을 옮기는 작업
			File saveDir = new File(baseDir + "/profile/" + emailEncoded); // ===올린 파일을 저장할 곳 설정
			saveDir.mkdirs(); // 디렉토리 생성
			log.info("saveDir ={} ",saveDir);

			// 파일명은 시간과 확장자명을 사용해서 정함
			String fileName = System.currentTimeMillis()
					+ multi.getOriginalFilename().substring(multi.getOriginalFilename().lastIndexOf("."));

			// 두개 조합해서 옮길 장소 설정
			File dest = new File(saveDir, fileName);



			// 옮겨두기는 작업
			multi.transferTo(dest);// 업로드
			foundUser.setProfileImage(uploadServer + "/resource/profile/" + emailEncoded + "/" + fileName);
			
			userRepository.save(foundUser);
		
		} else {
			userRepository.save(foundUser);
		}
	}
	
	public Resource loadResource(String url) throws NotExistResourceException, MalformedURLException {
		log.warn("resource url {} ", url);
		var found = profileImageRepository.findTop1ByUrl(url).orElseThrow(() -> new NotExistResourceException());
		
		return new FileUrlResource(found.getFileAddress());
	}
	
	
	public UserWrapper searchUserByEmail(String tokenEmailValue) throws NotExistUserException {
		var found = userRepository.findByEmail(tokenEmailValue).orElseThrow(()-> new NotExistUserException());
		
		return new UserWrapper(found);
	}

}
