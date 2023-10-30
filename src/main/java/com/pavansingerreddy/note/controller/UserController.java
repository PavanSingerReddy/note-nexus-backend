package com.pavansingerreddy.note.controller;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pavansingerreddy.note.dto.UserDto;
import com.pavansingerreddy.note.exception.UserNotFoundException;
import com.pavansingerreddy.note.model.NormalUserModel;
import com.pavansingerreddy.note.model.UserModel;
import com.pavansingerreddy.note.services.UserService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    // @CrossOrigin(origins = "*")  // allows cors for this method or we can use this for method or for entire controller class
    @PostMapping("/register")
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserModel userModel ){
        return ResponseEntity.ok(userService.createUser(userModel));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> loginUser(@RequestBody NormalUserModel normalUserModel ){
        return ResponseEntity.ok(userService.loginUser(normalUserModel));
    }


    @GetMapping("/{userEmail}")
    @RolesAllowed("USER")
    @PreAuthorize("#userEmail == authentication.principal") // "authentication.principal" contains the email id of the user who has logged in see below note for more information on this and "#userEmail" field contains the value of the path variable "String userEmail"
    public ResponseEntity<UserDto> getUserDetailsByEmail(@PathVariable("userEmail") String userEmail) throws UserNotFoundException{
        return ResponseEntity.ok(userService.getUserDetailsByEmail(userEmail));
    }

    @PutMapping("/{userEmail}")
    @RolesAllowed("USER")
    @PreAuthorize("#userEmail == authentication.principal") 
    public ResponseEntity<UserDto> updateUserInformationByEmail(@PathVariable("userEmail") String userEmail, @RequestBody NormalUserModel normalUserModel) throws UserNotFoundException {
        return ResponseEntity.ok(userService.updateUserInformationByEmail(userEmail,normalUserModel));
    }
    
    @DeleteMapping("/{userEmail}")
    @RolesAllowed("ADMIN")      // change this role to USER if you want to access the functionality of deleting the user
    @PreAuthorize("#userEmail == authentication.principal")
    public ResponseEntity<UserDto> deleteUserByEmail(@PathVariable("userEmail") String userEmail) throws UserNotFoundException{
        return ResponseEntity.ok(userService.deleteUserByEmail(userEmail));
    }

}



// NOTE: In Spring Security, the Authentication object stored in the SecurityContextHolder represents the currently authenticated user. When you call SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken), you’re telling Spring Security that the user associated with the current request is authenticated.

// The authentication.principal in the @PreAuthorize annotation refers to the principal of this Authentication object. The principal is typically an instance of UserDetails (or a subclass thereof) that contains information about the user.

// So, if you have a custom JWT authentication filter and you set the Authentication in the SecurityContextHolder like this:

// ===============================================================================================

// UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

// SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

// ===============================================================================================

// Then authentication.principal in your @PreAuthorize annotation will refer to the userDetails object that you passed to the UsernamePasswordAuthenticationToken constructor. This means that if your UserDetails implementation has a method or property called username, you can access it in your @PreAuthorize annotation with authentication.principal.username.

// Remember to ensure that your UserDetails implementation correctly sets the username and any other properties you need to access in your security expressions. Also, make sure that your JWT authentication filter is correctly configured and working as expected before using it with method security.