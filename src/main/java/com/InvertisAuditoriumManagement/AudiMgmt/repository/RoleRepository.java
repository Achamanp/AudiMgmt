package com.InvertisAuditoriumManagement.AudiMgmt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.InvertisAuditoriumManagement.AudiMgmt.entity.Role;
@Repository
public interface RoleRepository  extends JpaRepository<Role, Long>{

  Role findByName(String string);

}
