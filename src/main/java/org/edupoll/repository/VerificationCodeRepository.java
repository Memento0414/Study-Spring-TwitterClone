package org.edupoll.repository;

import java.util.Optional;

import org.edupoll.model.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long>{

	public Optional<VerificationCode> findTopByEmailOrderByCreatedDesc(String email);

	Optional<VerificationCode> findByEmail(String email);

	
}
