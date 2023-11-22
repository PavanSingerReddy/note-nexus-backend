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
import com.pavansingerreddy.note.entity.Role;
import com.pavansingerreddy.note.entity.User;
import com.pavansingerreddy.note.entity.VerificationToken;
import com.pavansingerreddy.note.exception.UserAlreadyExistsException;
import com.pavansingerreddy.note.exception.UserNotFoundException;
import com.pavansingerreddy.note.model.NormalUserModel;
import com.pavansingerreddy.note.model.UserModel;
import com.pavansingerreddy.note.repository.UserRepository;
import com.pavansingerreddy.note.repository.VerificationTokenRepository;
import com.pavansingerreddy.note.utils.DTOConversionUtil;
import com.pavansingerreddy.note.utils.JWTUtil;

@Service
public class UserServiceImplementation implements UserService {

    @Value("${verification.Token.expiry.seconds}")
    private Long VerificationTokenExpireTimeInSeconds;

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

        VerificationToken verificationToken = new VerificationToken(user, token, VerificationTokenExpireTimeInSeconds);
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


        Optional <VerificationToken> verificationToken = verificationTokenRepository.findByUser(user);

        if(verificationToken.isPresent()){
            verificationTokenRepository.delete(verificationToken.get());
        }

    }

}
