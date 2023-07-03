package org.edupoll.model.dto;

import org.edupoll.model.entity.FeedAttach;

import lombok.Data;

@Data
public class FeedAttachWrapper {

	private String type;
	private String mediaUrl;
	
	
	public FeedAttachWrapper(FeedAttach t) {
		
		this.type = t.getType();
		this.mediaUrl = t.getMediaUrl();
	}
}
