package com.pavansingerreddy.note.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pavansingerreddy.note.entity.Role;

public interface RoleRepository extends JpaRepository<Role,Long>{
    
}
