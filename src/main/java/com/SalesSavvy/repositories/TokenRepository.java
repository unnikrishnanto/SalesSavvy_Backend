package com.SalesSavvy.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.SalesSavvy.entities.JWTToken;

import jakarta.transaction.Transactional;

@Repository
public interface TokenRepository extends JpaRepository<JWTToken, Integer>{
	// Custom Query to find Tokens based on userId
	@Query("SELECT t FROM JWTToken t WHERE t.user.userId = :userId")
	Optional<JWTToken> findByUserId(int userId);
	
//	Custom Query to delete Tokens based on userId
	@Modifying  // because were modifying the table
	@Transactional // its a transaction
	@Query("DELETE FROM JWTToken t WHERE t.user.userId = :userId")
	void deleteByUserId(int userId);
	
	Optional<JWTToken> findByToken(String Token);
	

}
