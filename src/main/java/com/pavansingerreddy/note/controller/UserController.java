package com.pavansingerreddy.note.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pavansingerreddy.note.dto.UserDto;
import com.pavansingerreddy.note.exception.UserNotFoundException;
import com.pavansingerreddy.note.model.UpdateUserModel;
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


    @GetMapping("/{userEmail}")
    public ResponseEntity<UserDto> getUserDetailsByEmail(@PathVariable("userEmail") String userEmail) throws UserNotFoundException{
        return ResponseEntity.ok(userService.getUserDetailsByEmail(userEmail));
    }

    @PutMapping("/{userEmail}")
    public ResponseEntity<UserDto> updateUserInformationByEmail(@PathVariable("userEmail") String userEmail, @RequestBody UpdateUserModel updateUserModel) throws UserNotFoundException {
        return ResponseEntity.ok(userService.updateUserInformationByEmail(userEmail,updateUserModel));
    }
    


}
