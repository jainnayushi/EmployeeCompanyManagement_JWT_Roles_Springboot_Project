package com.assignment.EmployeeCompany.controller;


import com.assignment.EmployeeCompany.config.JwtGeneratorValidator;
import com.assignment.EmployeeCompany.entity.ResponseMessage;
import com.assignment.EmployeeCompany.entity.User;
import com.assignment.EmployeeCompany.entity.UserDTO;
import com.assignment.EmployeeCompany.repository.UserRepository;
import com.assignment.EmployeeCompany.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {

    @Autowired
    UserRepository userRepo;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JwtGeneratorValidator jwtGenVal;

    @Autowired
    BCryptPasswordEncoder bcCryptPasswordEncoder;
    @Autowired
    UserService userService;


    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/registration")
    public ResponseEntity<Object> registerUser(@RequestBody UserDTO userDto) {
        if (userRepo.findByUserName(userDto.getUserName()) != null) {
            logger.error("Username already exists: " + userDto.getUserName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(Boolean.FALSE, "Username already exists"));
        }
        User users = userService.save(userDto);
        if (users.equals(null)){
            logger.error("Unable to save User");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage(Boolean.FALSE, "Unable to save User"));
        }
        else{
            logger.trace("User saved successfully: " + users.getId());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseMessage(Boolean.TRUE, "User saved successfully"));
        }
    }

    @PostMapping("/genToken")
    public String generateJwtToken(@RequestBody UserDTO userDto) throws Exception {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDto.getUserName(), userDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return jwtGenVal.generateToken(authentication);

        }
        catch (Exception ex) {
            logger.error("Failed authentication : Username/password Incorrect: {}", ex.getMessage());
          throw new Exception("Failed authentication : Username/password Incorrect");
        }

    }

    @GetMapping("/welcomeAdmin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String welcome() {
        return "WelcomeAdmin";
    }

    @GetMapping("/welcomeUser")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String welcomeUser() {
        return "WelcomeUSER";
    }

}
