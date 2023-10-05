package com.example.demo.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.ERole;
import com.example.demo.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,Integer>{
    List<Role> findByName(ERole name);
}
