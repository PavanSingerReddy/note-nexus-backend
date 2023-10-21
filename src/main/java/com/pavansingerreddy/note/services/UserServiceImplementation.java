package com.pavansingerreddy.note.services;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.pavansingerreddy.note.dto.UserDto;
import com.pavansingerreddy.note.entity.User;
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
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userModel, user);

        userRepository.save(user);

        BeanUtils.copyProperties(user, userDto);

        return userDto;
    }
    
}
