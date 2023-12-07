package com.pavansingerreddy.note.services;

import java.util.Map;

import com.pavansingerreddy.note.dto.UserDto;
import com.pavansingerreddy.note.entity.User;
import com.pavansingerreddy.note.exception.InvalidUserDetailsException;
import com.pavansingerreddy.note.exception.PasswordDoesNotMatchException;
import com.pavansingerreddy.note.exception.UserAlreadyExistsException;
import com.pavansingerreddy.note.exception.UserNotFoundException;
import com.pavansingerreddy.note.model.ChangePasswordModel;
import com.pavansingerreddy.note.model.NormalUserModel;
import com.pavansingerreddy.note.model.ResetPasswordModel;
import com.pavansingerreddy.note.model.UserModel;

import jakarta.validation.Valid;

public interface UserService {

    User createUser(UserModel userModel) throws UserAlreadyExistsException,PasswordDoesNotMatchException;

    User getUserDetailsByEmail(String userEmail) throws UserNotFoundException;

    UserDto updateUserInformationByEmail(String userEmail,NormalUserModel normalUserModel) throws UserNotFoundException;

    UserDto deleteUserByEmail(String userEmail) throws UserNotFoundException;

    Map<String,String> loginUser(NormalUserModel normalUserModel);

    void saveVerificationTokenForUser(String token, User user);

    boolean validateVerificationToken(String token);

    void deletePreviousTokenIfExists(User user);

    void deletePreviousPasswordResetTokenIfExists(User user);

    void savePasswordResetToken(String token, User user);

    User validatePasswordResetToken(String token) throws InvalidUserDetailsException;

    String resetPassword(User user, ResetPasswordModel resetPasswordModel) throws InvalidUserDetailsException;

    String changePassword(User user, @Valid ChangePasswordModel changePasswordModel) throws InvalidUserDetailsException;

    void deletePasswordResetToken(String token) throws InvalidUserDetailsException;
    
}
