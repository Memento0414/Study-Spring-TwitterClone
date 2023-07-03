package org.edupoll.model.dto;

import java.util.List;

import org.edupoll.model.entity.Feed;

import lombok.Data;

@Data
public class FeedWrapper {
	private Long id;
	private UserWrapper writer;
	private String description;
	private Long viewCount;
	private List<FeedAttachWrapper> attaches; 
	
	public FeedWrapper(Feed feed) {
		this.id = feed.getId();
		this.viewCount = feed.getViewCount();
		this.description = feed.getDescription();
		this.writer = new UserWrapper(feed.getWriter());
		this.attaches = feed.getAttaches().stream().map(t -> new FeedAttachWrapper(t)).toList();
	}
}
