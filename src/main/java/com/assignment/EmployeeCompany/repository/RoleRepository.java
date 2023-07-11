package com.assignment.EmployeeCompany.repository;

import com.assignment.EmployeeCompany.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRole(String role);
}
