package com.pavansingerreddy.note.services;

import com.pavansingerreddy.note.dto.UserDto;
import com.pavansingerreddy.note.exception.UserNotFoundException;
import com.pavansingerreddy.note.model.UpdateUserModel;
import com.pavansingerreddy.note.model.UserModel;

public interface UserService {

    UserDto createUser(UserModel userModel);

    UserDto getUserDetailsByEmail(String userEmail) throws UserNotFoundException;

    UserDto updateUserInformationByEmail(String userEmail,UpdateUserModel updateUserModel) throws UserNotFoundException;

    UserDto deleteUserByEmail(String userEmail) throws UserNotFoundException;
    
}
