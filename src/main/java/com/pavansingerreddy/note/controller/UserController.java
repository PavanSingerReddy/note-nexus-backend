package com.pavansingerreddy.note.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pavansingerreddy.note.dto.UserDto;
import com.pavansingerreddy.note.entity.User;
import com.pavansingerreddy.note.events.event_publisher.RegistrationCompleteEvent;
import com.pavansingerreddy.note.exception.UserNotFoundException;
import com.pavansingerreddy.note.model.NormalUserModel;
import com.pavansingerreddy.note.model.UserModel;
import com.pavansingerreddy.note.services.UserService;
import com.pavansingerreddy.note.utils.DTOConversionUtil;

import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    private ApplicationEventPublisher publisher;

    @Value("${jwt.token.expiry.seconds}")
    private Long JWTExpireTimeInSeconds;

    @Value("${applicationUrl.user.api}")
    private String UserApiApplicationUrl;

    @GetMapping("/is-authenticated")
    public ResponseEntity<?> checkAuthentication(Principal principal) {
        if (principal != null && principal.getName() != null) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    }

    @GetMapping("/csrf-token")
    public ResponseEntity<String> getCsrf() {
        return ResponseEntity.ok("Initial CSRF Token Request Is Successfull !!!");
    }

    // @CrossOrigin(origins = "*") // allows cors for this method or we can use this
    // for method or for entire controller class
    @PostMapping("/register")
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserModel userModel) throws Exception {
        User user = userService.createUser(userModel);
        publisher.publishEvent(new RegistrationCompleteEvent(user, UserApiApplicationUrl));
        return ResponseEntity.ok(DTOConversionUtil.userToUserDTO(user));
    }

    // api for verifying the registration
    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) throws Exception {
        boolean result = userService.validateVerificationToken(token);
        if (result) {
            return "User Verified Successfully";
        }
        throw new Exception("Bad User Details");
    }

    @GetMapping("/resendVerifyToken")
    public ResponseEntity<UserDto> resendVerificationToken(@RequestParam("email") String email) throws UserNotFoundException{
        User user = userService.getUserDetailsByEmail(email);
        userService.deletePreviousTokenIfExists(user);
        publisher.publishEvent(new RegistrationCompleteEvent(user, UserApiApplicationUrl));
        return ResponseEntity.ok(DTOConversionUtil.userToUserDTO(user));

    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody NormalUserModel normalUserModel, HttpServletResponse response) {
        Map<String, String> jwtToken = userService.loginUser(normalUserModel);

        ResponseCookie cookie = ResponseCookie.from("JWT", jwtToken.get("jwt"))
                .httpOnly(true)
                .secure(true)
                .sameSite("strict") // Set SameSite to None
                .path("/")
                .maxAge(JWTExpireTimeInSeconds)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get")
    @RolesAllowed("USER")
    public ResponseEntity<UserDto> getUserDetailsByEmail(Principal principal) throws UserNotFoundException {
        String userEmail = principal.getName();
        User user = userService.getUserDetailsByEmail(userEmail);
        return ResponseEntity.ok(DTOConversionUtil.userToUserDTO(user));
    }

    @PutMapping("/edit")
    @RolesAllowed("USER")
    public ResponseEntity<UserDto> updateUserInformationByEmail(Principal principal,
            @RequestBody NormalUserModel normalUserModel) throws UserNotFoundException {
        String userEmail = principal.getName();
        return ResponseEntity.ok(userService.updateUserInformationByEmail(userEmail, normalUserModel));
    }

    @DeleteMapping("/delete")
    @RolesAllowed("ADMIN") // change this role to USER if you want to access the functionality of deleting
                           // the user
    public ResponseEntity<UserDto> deleteUserByEmail(Principal principal) throws UserNotFoundException {
        String userEmail = principal.getName();
        return ResponseEntity.ok(userService.deleteUserByEmail(userEmail));
    }

}

// NOTE: In Spring Security, the Authentication object stored in the
// SecurityContextHolder represents the currently authenticated user. When you
// call
// SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken),
// you’re telling Spring Security that the user associated with the current
// request is authenticated.

// The authentication.principal in the @PreAuthorize annotation refers to the
// principal of this Authentication object. The principal is typically an
// instance of UserDetails (or a subclass thereof) that contains information
// about the user.

// So, if you have a custom JWT authentication filter and you set the
// Authentication in the SecurityContextHolder like this:

// ===============================================================================================

// UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new
// UsernamePasswordAuthenticationToken(userDetails, null,
// userDetails.getAuthorities());

// SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

// ===============================================================================================

// Then authentication.principal in your @PreAuthorize annotation will refer to
// the userDetails object that you passed to the
// UsernamePasswordAuthenticationToken constructor. This means that if your
// UserDetails implementation has a method or property called username, you can
// access it in your @PreAuthorize annotation with
// authentication.principal.username.

// Remember to ensure that your UserDetails implementation correctly sets the
// username and any other properties you need to access in your security
// expressions. Also, make sure that your JWT authentication filter is correctly
// configured and working as expected before using it with method security.