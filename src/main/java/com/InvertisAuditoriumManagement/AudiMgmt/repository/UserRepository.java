package com.InvertisAuditoriumManagement.AudiMgmt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.InvertisAuditoriumManagement.AudiMgmt.entity.User;
@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	User findByUserId(String userId);

	User findByEmail(String email);

}
