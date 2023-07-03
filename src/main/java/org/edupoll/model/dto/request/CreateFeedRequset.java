package org.edupoll.model.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class CreateFeedRequset {

		private String description; //본문내용
		private List<MultipartFile> attaches; // 파일업로드용
		
}
