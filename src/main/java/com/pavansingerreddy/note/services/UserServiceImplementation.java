package com.pavansingerreddy.note.services;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
import com.pavansingerreddy.note.exception.InvalidUserDetailsException;
import com.pavansingerreddy.note.exception.PasswordDoesNotMatchException;
import com.pavansingerreddy.note.exception.UserAlreadyExistsException;
import com.pavansingerreddy.note.exception.UserNotFoundException;
import com.pavansingerreddy.note.model.ChangePasswordModel;
import com.pavansingerreddy.note.model.NormalUserModel;
import com.pavansingerreddy.note.model.ResetPasswordModel;
import com.pavansingerreddy.note.model.UserModel;
import com.pavansingerreddy.note.repository.PasswordResetTokenRepository;
import com.pavansingerreddy.note.repository.UserRepository;
import com.pavansingerreddy.note.repository.VerificationTokenRepository;
import com.pavansingerreddy.note.utils.DTOConversionUtil;
import com.pavansingerreddy.note.utils.JWTUtil;

import jakarta.validation.Valid;

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

    public User createUser(UserModel userModel, int mailNoToUse)
            throws UserAlreadyExistsException, PasswordDoesNotMatchException {

        Optional<User> userOptional = userRepository.findByEmail(userModel.getEmail());

        if (userModel.ValidatePasswordAndRetypedPassword()) {

            if (userOptional.isPresent() && userOptional.get().isEnabled()) {
                throw new UserAlreadyExistsException("User already exists in the database");
            }

            else if (userOptional.isPresent() && !userOptional.get().isEnabled()) {
                userRepository.delete(userOptional.get());
            }

            User user = new User();

            BeanUtils.copyProperties(userModel, user);

            Role role = new Role();

            role.setName("ROLE_USER");

            Set<Role> roles = new HashSet<>();

            roles.add(role);

            user.setRoles(roles);

            user.setPassword(passwordEncoder.encode(user.getPassword()));

            user.updateUserCreatedAtTimeAndNewUserCanBeCreatedAtTime();

            user.setMailNoToUseForSendingEmail(mailNoToUse);

            userRepository.save(user);

            return user;
        }

        throw new PasswordDoesNotMatchException("Passwords do not match");

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

        if ((verificationToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0) {

            user.setVerificationToken(null);

            verificationTokenRepository.delete(verificationToken);

            return false;
        }

        user.setEnabled(true);

        userRepository.save(user);

        user.setVerificationToken(null);

        verificationTokenRepository.delete(verificationToken);

        return true;

    }

    @Override

    public void deletePreviousTokenIfExists(User user) {

        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByUser(user);

        if (verificationToken.isPresent()) {

            user.setVerificationToken(null);
            verificationTokenRepository.delete(verificationToken.get());
        }

    }

    @Override

    public void deletePreviousPasswordResetTokenIfExists(User user) {

        Optional<PasswordResetToken> passwordToken = passwordResetTokenRepository.findByUser(user);

        if (passwordToken.isPresent()) {
            passwordResetTokenRepository.delete(passwordToken.get());
        }
    }

    @Override
    public void savePasswordResetToken(String token, User user) {

        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token, TokenExpireTimeInSeconds);

        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override

    public User validatePasswordResetToken(String token) throws InvalidUserDetailsException {

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (passwordResetToken == null) {
            throw new InvalidUserDetailsException("Not a valid password reset Token");
        }

        Calendar cal = Calendar.getInstance();

        User user = passwordResetToken.getUser();

        if (user == null) {
            throw new InvalidUserDetailsException("User does not exists");
        }

        if ((passwordResetToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0) {
            passwordResetTokenRepository.delete(passwordResetToken);
            throw new InvalidUserDetailsException("Password Reset Token is expired request a new one");
        }

        return user;
    }

    @Override
    public String resetPassword(User user, ResetPasswordModel resetPasswordModel) throws InvalidUserDetailsException {

        if (resetPasswordModel.ValidatePasswordAndRetypedPassword()) {

            user.setPassword(passwordEncoder.encode(resetPasswordModel.getNewpassword()));

            userRepository.save(user);
            return "password reset successful";
        }

        throw new InvalidUserDetailsException("new Password and retyped new password does not match");
    }

    @Override
    public String changePassword(User user, @Valid ChangePasswordModel changePasswordModel)
            throws InvalidUserDetailsException {

        if (!passwordEncoder.matches(changePasswordModel.getOldpassword(), user.getPassword())) {
            throw new InvalidUserDetailsException("old password does not match please verify and try again");
        }

        if (!changePasswordModel.ValidatePasswordAndRetypedPassword()) {
            throw new InvalidUserDetailsException("new Password and retyped new password does not match");
        }

        user.setPassword(passwordEncoder.encode(changePasswordModel.getNewpassword()));
        userRepository.save(user);

        return "Password changed successfully";

    }

    @Override

    public void deletePasswordResetToken(String token) throws InvalidUserDetailsException {

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (passwordResetToken == null) {
            throw new InvalidUserDetailsException("Not a valid password reset Token");
        }

        passwordResetTokenRepository.delete(passwordResetToken);
    }

    @Override

    public Integer getLatestEmailToUse() throws InvalidUserDetailsException {

        Optional<User> userOptional = userRepository.findTheUserWhoContainsTheAppropriateEmailToSendSignUpUrl();

        if (userOptional.isPresent()) {

            User user = userOptional.get();

            Date currentDate = Date.from(Instant.now());

            if (user.getNewUserCanBeCreatedAtTime().compareTo(currentDate) > 0) {

                long diffInMilli = Math.abs(user.getNewUserCanBeCreatedAtTime().getTime() - currentDate.getTime());

                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMilli);
                long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diffInMilli) -
                        TimeUnit.MINUTES.toSeconds(diffInMinutes);

                throw new InvalidUserDetailsException("Email service is busy right now try after " + diffInMinutes
                        + " minutes and " + diffInSeconds + " seconds");
            }

            return user.getMailNoToUseForSendingEmail();
        }
        return 1;
    }

}
