package org.edupoll.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.edupoll.exception.ExistUserEmailException;
import org.edupoll.model.dto.FeedWrapper;
import org.edupoll.model.dto.request.CreateFeedRequset;
import org.edupoll.model.dto.response.FeedListResponse;
import org.edupoll.model.entity.Feed;
import org.edupoll.model.entity.FeedAttach;
import org.edupoll.model.entity.User;
import org.edupoll.repository.FeedAttachsRepository;
import org.edupoll.repository.FeedRepository;
import org.edupoll.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {

	private final FeedRepository feedRepository;
	
	private final FeedAttachsRepository feedAttachsRepository;
	
	private final UserRepository userRepository;
	
	@Value("${upload.server}")
	private String uploadServer;
	
	@Value("${upload.basedir}")
	private String baseDir;
	
	/**인증을 받은 유저가 피드를 등록하기 위한 메서드*/
	@Transactional
	public void createNewFeed(String principal, CreateFeedRequset req) throws ExistUserEmailException, IllegalStateException, IOException {
		
		// 1. 피드 엔티티를 생성해서 save
		User user = userRepository.findByEmail(principal).orElseThrow(() -> new ExistUserEmailException());
		
		Feed feed = new Feed();
		feed.setDescription(req.getDescription());
		feed.setViewCount(0L);
		feed.setWriter(user);

		Feed saved = feedRepository.save(feed);
		// ================================================================
		// 2.feedAttachs들을 생성 후 save

//		log.info("Attaches is Exist ? {}",req.getAttaches() != null);
		
		//2. 요청 중 파일 업로드에 필요한 코드 작성
		if (req.getAttaches() != null) { // 파일이 넘어왔다면
			File saveDir = new File(baseDir + "/feed/" + saved.getId()); // ===업로드한 실제 파일들을 저장소 설정
			saveDir.mkdirs(); //  save디렉토리 생성

			for (MultipartFile multi : req.getAttaches()) {// 하나씩 반복문 돌면서
				log.info("multi.getOriginalFilename = {}", multi.getOriginalFilename());
				// 파일을 옮기는 작업
				// 파일명은 시간과 확장자명을 사용해서 정함
				String fileName = String.valueOf(System.currentTimeMillis());
				String extension =multi.getOriginalFilename().split("\\.")[1];
	

				// 두개 조합해서 옮길 장소 설정
				File dest = new File(saveDir, fileName+"."+extension);

				multi.transferTo(dest);

				FeedAttach attaches = new FeedAttach();
					attaches.setType(multi.getContentType());
					attaches.setMediaUrl(uploadServer + "/resource/feed/" + saved.getId() + "/" + fileName + "." + extension);
					attaches.setFeed(saved);

				feedAttachsRepository.save(attaches);

			}
		}
	}
	
	
	public List<FeedWrapper> findAllFeed(int page){
		
		
		List<Feed> entityList = feedRepository.findAll(PageRequest.of(page-1, 10,Sort.by("id").descending())).toList();
		
		return entityList.stream().map(t -> new FeedWrapper(t)).toList();
	
	}

	public Long totalCount() {
	 
		return feedRepository.count();
	}
	
	
	
}
