package com.pavansingerreddy.note.services;

import java.util.Map;

import com.pavansingerreddy.note.dto.UserDto;
import com.pavansingerreddy.note.entity.Users;
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

    Users createUser(UserModel userModel,int mailNoToUse) throws UserAlreadyExistsException,PasswordDoesNotMatchException;

    Users getUserDetailsByEmail(String userEmail) throws UserNotFoundException;

    UserDto updateUserInformationByEmail(String userEmail,NormalUserModel normalUserModel) throws UserNotFoundException;

    UserDto deleteUserByEmail(String userEmail) throws UserNotFoundException;

    Map<String,String> loginUser(NormalUserModel normalUserModel);

    void saveVerificationTokenForUser(String token, Users user);

    boolean validateVerificationToken(String token);

    void deletePreviousTokenIfExists(Users user);

    void deletePreviousPasswordResetTokenIfExists(Users user);

    void savePasswordResetToken(String token, Users user);

    Users validatePasswordResetToken(String token) throws InvalidUserDetailsException;

    String resetPassword(Users user, ResetPasswordModel resetPasswordModel) throws InvalidUserDetailsException;

    String changePassword(Users user, @Valid ChangePasswordModel changePasswordModel) throws InvalidUserDetailsException;

    void deletePasswordResetToken(String token) throws InvalidUserDetailsException;

    Integer getLatestEmailToUse() throws InvalidUserDetailsException;
    
}
