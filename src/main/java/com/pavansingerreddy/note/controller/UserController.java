package com.pavansingerreddy.note.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pavansingerreddy.note.dto.UserDto;
import com.pavansingerreddy.note.model.UserModel;
import com.pavansingerreddy.note.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserModel userModel ){
        return ResponseEntity.ok(userService.createUser(userModel));
    }
    


}
