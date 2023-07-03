package org.edupoll.model.entity;

import org.edupoll.model.dto.request.UserJoinRequestData;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name="users")
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String email;
	private String password;
	private String name;
	private String profileImage;
	private String social;
	
	public User(UserJoinRequestData dto) {
		this.email = dto.getEmail();
		this.name = dto.getName();
		this.password = dto.getPassword();
		this.profileImage = "";
		this.social = "";
	}

	
	
}
