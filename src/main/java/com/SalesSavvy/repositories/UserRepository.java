package com.SalesSavvy.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.SalesSavvy.entities.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
	
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
	
	@Modifying
    @Transactional
    @Query("UPDATE User u SET u.username = :newUsername, u.email = :newEmail WHERE u.id = :userId")
    int updateUserDetails(int userId, String newUsername, String newEmail);
	
	@Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.id = :userId")
    int changePassword(int userId,String newPassword);
}
