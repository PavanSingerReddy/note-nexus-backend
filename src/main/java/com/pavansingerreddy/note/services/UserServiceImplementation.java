package com.pavansingerreddy.note.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pavansingerreddy.note.dto.UserDto;
import com.pavansingerreddy.note.entity.Role;
import com.pavansingerreddy.note.entity.User;
import com.pavansingerreddy.note.exception.UserNotFoundException;
import com.pavansingerreddy.note.model.NormalUserModel;
import com.pavansingerreddy.note.model.UserModel;
import com.pavansingerreddy.note.repository.UserRepository;
import com.pavansingerreddy.note.utils.JWTUtil;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    UserServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Autowired
    PasswordEncoder passwordEncoder;
    @Override
    public UserDto createUser(UserModel userModel) {

        User user = new User();
        BeanUtils.copyProperties(userModel, user);

        Role role = new Role();

        role.setName("ROLE_USER");

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        user.setRoles(roles);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);

        return userDto;
    }

    @Override
    public UserDto getUserDetailsByEmail(String userEmail) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isPresent()) {

            User user = userOptional.get();

            UserDto userDto = new UserDto();

            BeanUtils.copyProperties(user, userDto);

            return userDto;
        } else {
            throw new UserNotFoundException("User Does not exists");
        }

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

            UserDto userDto = new UserDto();

            BeanUtils.copyProperties(user, userDto);

            return userDto;
        } else {
            throw new UserNotFoundException("User Does not exists");
        }

    }

    @Override
    public UserDto deleteUserByEmail(String userEmail) throws UserNotFoundException {

        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        UserDto userDto = new UserDto();

        if(userOptional.isPresent()){
            User user = userOptional.get();
            userRepository.deleteByEmail(userEmail);
            BeanUtils.copyProperties(user, userDto);
            return userDto;

        }
        else{

            throw new UserNotFoundException("User Does not exists");
        }
    }

    @Override
    public Map<String,String> loginUser(NormalUserModel normalUserModel) {
        
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(normalUserModel.getUsername(), normalUserModel.getPassword());

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        String jwt = jwtUtil.generateJwt(authentication);

        Map<String,String> jwtToken = new HashMap<>();

        jwtToken.put("jwt", jwt);

        return jwtToken;
    }

}
