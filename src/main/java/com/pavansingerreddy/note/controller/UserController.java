package com.pavansingerreddy.note.controller;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pavansingerreddy.note.dto.UserDto;
import com.pavansingerreddy.note.exception.UserNotFoundException;
import com.pavansingerreddy.note.model.NormalUserModel;
import com.pavansingerreddy.note.model.UserModel;
import com.pavansingerreddy.note.services.UserService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserModel userModel ){
        return ResponseEntity.ok(userService.createUser(userModel));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> loginUser(@RequestBody NormalUserModel normalUserModel ){
        return ResponseEntity.ok(userService.loginUser(normalUserModel));
    }


    @GetMapping("/{userEmail}")
    @RolesAllowed("USER")
    public ResponseEntity<UserDto> getUserDetailsByEmail(@PathVariable("userEmail") String userEmail) throws UserNotFoundException{
        return ResponseEntity.ok(userService.getUserDetailsByEmail(userEmail));
    }

    @PutMapping("/{userEmail}")
    @RolesAllowed("USER")
    public ResponseEntity<UserDto> updateUserInformationByEmail(@PathVariable("userEmail") String userEmail, @RequestBody NormalUserModel normalUserModel) throws UserNotFoundException {
        return ResponseEntity.ok(userService.updateUserInformationByEmail(userEmail,normalUserModel));
    }
    
    @DeleteMapping("/{userEmail}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<UserDto> deleteUserByEmail(@PathVariable("userEmail") String userEmail) throws UserNotFoundException{
        return ResponseEntity.ok(userService.deleteUserByEmail(userEmail));
    }

}
