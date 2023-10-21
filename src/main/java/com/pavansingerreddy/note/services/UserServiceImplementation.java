package com.pavansingerreddy.note.services;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pavansingerreddy.note.dto.UserDto;
import com.pavansingerreddy.note.entity.User;
import com.pavansingerreddy.note.exception.UserNotFoundException;
import com.pavansingerreddy.note.model.UpdateUserModel;
import com.pavansingerreddy.note.model.UserModel;
import com.pavansingerreddy.note.repository.UserRepository;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    UserServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserModel userModel) {

        User user = new User();
        BeanUtils.copyProperties(userModel, user);

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
    public UserDto updateUserInformationByEmail(String userEmail, UpdateUserModel updateUserModel)
            throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isPresent()) {

            User user = userOptional.get();

            System.out.println("the user before is : " + user);

            if (updateUserModel.getEmail() != null && !updateUserModel.getEmail().isEmpty()) {
                user.setEmail(updateUserModel.getEmail());
            }

            if (updateUserModel.getUsername() != null && !updateUserModel.getUsername().isEmpty()) {
                user.setUsername(updateUserModel.getUsername());
            }

            if (updateUserModel.getPassword() != null && !updateUserModel.getPassword().isEmpty()) {
                user.setPassword(updateUserModel.getPassword());
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

}
