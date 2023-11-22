package com.pavansingerreddy.note.services;

import java.util.Map;

import com.pavansingerreddy.note.dto.UserDto;
import com.pavansingerreddy.note.entity.User;
import com.pavansingerreddy.note.exception.UserNotFoundException;
import com.pavansingerreddy.note.model.NormalUserModel;
import com.pavansingerreddy.note.model.UserModel;

public interface UserService {

    User createUser(UserModel userModel) throws Exception;

    UserDto getUserDetailsByEmail(String userEmail) throws UserNotFoundException;

    UserDto updateUserInformationByEmail(String userEmail,NormalUserModel normalUserModel) throws UserNotFoundException;

    UserDto deleteUserByEmail(String userEmail) throws UserNotFoundException;

    Map<String,String> loginUser(NormalUserModel normalUserModel);

    void saveVerificationTokenForUser(String token, User user);

    boolean validateVerificationToken(String token);
    
}
