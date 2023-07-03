package org.edupoll.controller;


import java.io.IOException;
import java.util.List;

import org.edupoll.exception.ExistUserEmailException;
import org.edupoll.model.dto.FeedWrapper;
import org.edupoll.model.dto.request.CreateFeedRequset;
import org.edupoll.model.dto.response.FeedListResponse;
import org.edupoll.service.FeedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/feed")
public class FeedController {
	
	private final FeedService feedService;
	
	//글 등록
	@PostMapping("/create")
	public ResponseEntity<?> createNewFeedHandle(@AuthenticationPrincipal String principal, CreateFeedRequset dto) throws ExistUserEmailException, IllegalStateException, IOException{
		//글 등록 제공하는 API
		
		feedService.createNewFeed(principal, dto);
		
		return new ResponseEntity<Void>(HttpStatus.CREATED);
		
	}
	
	@GetMapping("/list")
	public ResponseEntity<?> feedAllListHandle (@RequestParam(defaultValue ="1")int page) {
		Long total = feedService.totalCount();
		List<FeedWrapper> feeds = feedService.findAllFeed(page);
		FeedListResponse body = new FeedListResponse(total, feeds);
		
		return new ResponseEntity<>(body, HttpStatus.OK);
	}
	
//	@GetMapping
//	public ResponseEntity<?> SpecificFeedHandle(){
//		//특정 글 재공해주는 API
//		
//		return null;
//	}
//	
//	@DeleteMapping
//	public ResponseEntity<?> deleteFeedHandle(){
//		//글 삭제 제공하는 API
//		return null;
//		
//	}
}
