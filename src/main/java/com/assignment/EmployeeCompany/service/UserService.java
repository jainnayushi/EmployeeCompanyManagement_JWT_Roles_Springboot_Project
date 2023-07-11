package com.assignment.EmployeeCompany.service;

import com.assignment.EmployeeCompany.entity.User;
import com.assignment.EmployeeCompany.entity.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService{
    User save(UserDTO userRegisteredDTO);
}