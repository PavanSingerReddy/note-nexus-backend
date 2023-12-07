package com.pavansingerreddy.note.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pavansingerreddy.note.entity.Role;

// The @Repository annotation tells Spring that this interface is a Repository.
// Repositories in Spring are used for data access. They can fetch, save, update, and delete data.
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}
