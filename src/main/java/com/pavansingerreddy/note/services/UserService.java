package com.pavansingerreddy.note.services;

import java.util.Map;

import com.pavansingerreddy.note.dto.UserDto;
import com.pavansingerreddy.note.entity.User;
import com.pavansingerreddy.note.exception.UserNotFoundException;
import com.pavansingerreddy.note.model.ChangePasswordModel;
import com.pavansingerreddy.note.model.NormalUserModel;
import com.pavansingerreddy.note.model.PasswordModel;
import com.pavansingerreddy.note.model.UserModel;

import jakarta.validation.Valid;

public interface UserService {

    User createUser(UserModel userModel) throws Exception;

    User getUserDetailsByEmail(String userEmail) throws UserNotFoundException;

    UserDto updateUserInformationByEmail(String userEmail,NormalUserModel normalUserModel) throws UserNotFoundException;

    UserDto deleteUserByEmail(String userEmail) throws UserNotFoundException;

    Map<String,String> loginUser(NormalUserModel normalUserModel);

    void saveVerificationTokenForUser(String token, User user);

    boolean validateVerificationToken(String token);

    void deletePreviousTokenIfExists(User user);

    void deletePreviousPasswordResetTokenIfExists(User user);

    void savePasswordResetToken(String token, User user);

    User validatePasswordResetToken(String token) throws Exception;

    String resetPassword(User user, PasswordModel passwordModel) throws Exception;

    String changePassword(User user, @Valid ChangePasswordModel changePasswordModel) throws Exception;
    
}
