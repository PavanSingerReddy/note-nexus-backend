package com.pavansingerreddy.note.services;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pavansingerreddy.note.dto.UserDto;
import com.pavansingerreddy.note.entity.PasswordResetToken;
import com.pavansingerreddy.note.entity.Role;
import com.pavansingerreddy.note.entity.User;
import com.pavansingerreddy.note.entity.VerificationToken;
import com.pavansingerreddy.note.exception.UserAlreadyExistsException;
import com.pavansingerreddy.note.exception.UserNotFoundException;
import com.pavansingerreddy.note.model.NormalUserModel;
import com.pavansingerreddy.note.model.PasswordModel;
import com.pavansingerreddy.note.model.UserModel;
import com.pavansingerreddy.note.repository.PasswordResetTokenRepository;
import com.pavansingerreddy.note.repository.UserRepository;
import com.pavansingerreddy.note.repository.VerificationTokenRepository;
import com.pavansingerreddy.note.utils.DTOConversionUtil;
import com.pavansingerreddy.note.utils.JWTUtil;

@Service
public class UserServiceImplementation implements UserService {

    @Value("${verification.Token.expiry.seconds}")
    private Long TokenExpireTimeInSeconds;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    UserServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public User createUser(UserModel userModel) throws Exception {

        Optional<User> userOptional = userRepository.findByEmail(userModel.getEmail());

        // checks if the password and retyped password matches and also checks if the
        // user is not already present in the database
        if (userModel.ValidatePasswordAndRetypedPassword()) {

            if (userOptional.isPresent()) {
                throw new UserAlreadyExistsException("User already exists in the database");
            }
            User user = new User();
            BeanUtils.copyProperties(userModel, user);
            Role role = new Role();
            role.setName("ROLE_USER");
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return user;
        }
        throw new Exception("Passwords do not match");

    }

    @Override
    public User getUserDetailsByEmail(String userEmail) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user;
        }
        throw new UserNotFoundException("User Does not exists");

    }

    @Override
    public UserDto updateUserInformationByEmail(String userEmail, NormalUserModel normalUserModel)
            throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (normalUserModel.getEmail() != null && !normalUserModel.getEmail().isEmpty()) {
                user.setEmail(normalUserModel.getEmail());
            }
            if (normalUserModel.getUsername() != null && !normalUserModel.getUsername().isEmpty()) {
                user.setUsername(normalUserModel.getUsername());
            }
            if (normalUserModel.getPassword() != null && !normalUserModel.getPassword().isEmpty()) {
                user.setPassword(normalUserModel.getPassword());
            }
            userRepository.save(user);
            return DTOConversionUtil.userToUserDTO(user);

        }
        throw new UserNotFoundException("User Does not exists");
    }

    @Override
    public UserDto deleteUserByEmail(String userEmail) throws UserNotFoundException {

        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            userRepository.deleteByEmail(userEmail);
            return DTOConversionUtil.userToUserDTO(user);
        }
        throw new UserNotFoundException("User Does not exists");
    }

    @Override
    public Map<String, String> loginUser(NormalUserModel normalUserModel) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                normalUserModel.getEmail(), normalUserModel.getPassword());

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        String jwt = jwtUtil.generateJwt(authentication);

        Map<String, String> jwtToken = new HashMap<>();

        jwtToken.put("jwt", jwt);

        return jwtToken;
    }

    @Override
    public void saveVerificationTokenForUser(String token, User user) {

        VerificationToken verificationToken = new VerificationToken(user, token, TokenExpireTimeInSeconds);
        verificationTokenRepository.save(verificationToken);

    }

    @Override
    public boolean validateVerificationToken(String token) {

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            return false;
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();

        // if the verification token time is less than the current time then the token
        // is expired so we delete the token from the database and return false
        if ((verificationToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0) {
            verificationTokenRepository.delete(verificationToken);
            return false;
        }

        user.setEnabled(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
        return true;

    }

    @Override
    public void deletePreviousTokenIfExists(User user) {

        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByUser(user);

        if (verificationToken.isPresent()) {
            verificationTokenRepository.delete(verificationToken.get());
        }

    }

    // deletes the previous password reset token for a user if they exists
    @Override
    public void deletePreviousPasswordResetTokenIfExists(User user) {
        Optional<PasswordResetToken> passwordToken = passwordResetTokenRepository.findByUser(user);
        if (passwordToken.isPresent()) {
            passwordResetTokenRepository.delete(passwordToken.get());
        }
    }

    // The method takes the random UUID token and the user object and saves the
    // password reset token in the database with that user id
    @Override
    public void savePasswordResetToken(String token, User user) {
        // calling the constructor of the PasswordResetToken entity which set's the user
        // ,token, and Token expiry time in the PasswordResetToken
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token, TokenExpireTimeInSeconds);
        // after saving the password reset token details in the password reset token
        // object now it saves that object to the database using
        // passwordResetTokenRepository
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    // This method takes the UUID token which came as a query parameter with the url
    // and checks if the token exists and is not expired and returns the user
    // assosiated with the token
    public User validatePasswordResetToken(String token) throws Exception {
        // getting the passwordResetToken token from the database
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        // if the token does not exists we are throwing an exception
        if (passwordResetToken == null) {
            throw new Exception("Not a valid password reset Token");
        }

        // getting the calendar instance to get the current time
        Calendar cal = Calendar.getInstance();
        // getting the user object to which the password reset token is assosiated to
        User user = passwordResetToken.getUser();

        // if the user doesnot exists then we are throwing exception.This generally case
        // does not occur but we are specifying here just for the safety
        if (user == null) {
            throw new Exception("User does not exists");
        }

        // if the passwordResetToken token time is less than the current time then the
        // token is expired so we delete the token from the database and throw an exception
        if ((passwordResetToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0) {
            passwordResetTokenRepository.delete(passwordResetToken);
            throw new Exception("Password Reset Token is expired request a new one");
        }
        // deleting the password reset token after verifying it
        passwordResetTokenRepository.delete(passwordResetToken);
        // returning the user assosiated with the token
        return user;
    }


    // taking the user object and the password model which contains the newpassword and validatePasswordResetToken and resetting the user's password.
    @Override
    public String resetPassword(User user, PasswordModel passwordModel) throws Exception {
        // checking if the passwordResetToken and validatePasswordResetToken matches if they match then we save the newpassword to the database else we throw an exception
        if (passwordModel.ValidatePasswordAndRetypedPassword()) {
            user.setPassword(passwordEncoder.encode(passwordModel.getNewpassword()));
            userRepository.save(user);
            return "password reset successful";
        }
        throw new Exception("new Password and retyped new password does not match");
    }

}
