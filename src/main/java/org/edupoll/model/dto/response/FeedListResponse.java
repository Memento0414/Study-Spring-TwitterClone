package org.edupoll.model.dto.response;

import java.util.List;

import org.edupoll.model.dto.FeedWrapper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeedListResponse {

	private Long total;
	private List<FeedWrapper> feeds;
	
	
	
	
}
