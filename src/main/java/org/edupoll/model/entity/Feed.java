package org.edupoll.model.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "feeds")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Feed {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO )
	private Long id;
	
	@ManyToOne
//	@JoinColumn(name="writerId", referencedColumnName = "email") //User 객체의 email 참조 변경
	@JoinColumn(name="writerId")
	private User writer; // 작성자 정보
	
	private String description; //본문
	private Long viewCount;
	

	@OneToMany(mappedBy = "feed")
	private List<FeedAttach> attaches;
	
}
