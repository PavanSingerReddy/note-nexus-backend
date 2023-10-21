package com.pavansingerreddy.note.services;

import org.springframework.http.ResponseEntity;

import com.pavansingerreddy.note.dto.UserDto;
import com.pavansingerreddy.note.model.UserModel;

public interface UserService {

    UserDto createUser(UserModel userModel);

    UserDto getUserDetailsByEmail(String userEmail);
    
}
