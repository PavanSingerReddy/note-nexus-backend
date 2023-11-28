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
import com.pavansingerreddy.note.events.event_publisher.PasswordResetEvent;
import com.pavansingerreddy.note.events.event_publisher.RegistrationCompleteEvent;
import com.pavansingerreddy.note.exception.UserNotFoundException;
import com.pavansingerreddy.note.model.ChangePasswordModel;
import com.pavansingerreddy.note.model.NormalUserModel;
import com.pavansingerreddy.note.model.PasswordModel;
import com.pavansingerreddy.note.model.ResetPasswordModel;
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

    @Value("${frontend.applicationUrl.user.api}")
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
    public ResponseEntity<String> verifyRegistration(@RequestParam("token") String token) throws Exception {
        boolean result = userService.validateVerificationToken(token);
        if (result) {
            return ResponseEntity.ok("User Verified Successfully");
        }
        throw new Exception("Bad User Details");
    }

    @PostMapping("/resendVerifyToken")
    public ResponseEntity<UserDto> resendVerificationToken(@RequestBody @Valid PasswordModel passwordModel)
            throws UserNotFoundException, Exception {
        String email = passwordModel.getEmail();
        User user = userService.getUserDetailsByEmail(email);
        boolean isUserEnabled = user.isEnabled();
        if (isUserEnabled) {
            throw new Exception("User is already verified");
        }
        userService.deletePreviousTokenIfExists(user);
        publisher.publishEvent(new RegistrationCompleteEvent(user, UserApiApplicationUrl));
        return ResponseEntity.ok(DTOConversionUtil.userToUserDTO(user));

    }

    // post api endpoint for resetting the forgotten password it send's an email
    // with the link to reset the password
    @PostMapping("/resetPassword")
    // The request should contain the email of the user who forgot the password and
    // want to reset the password
    public ResponseEntity<String> resetPassword(@RequestBody @Valid PasswordModel passwordModel)
            throws UserNotFoundException, Exception {
        // userService.getUserDetailsByEmail() returns the user object with their email
        // id
        User user = userService.getUserDetailsByEmail(passwordModel.getEmail());
        // checking if the user is enabled or not if the user is not enabled then we
        // throw the exception as "User is not verified.Verify the user first"
        if (!user.isEnabled()) {
            throw new Exception("User is not verified.Verify the user first");
        }

        // if there exists any previous password reset token for this user we delete it
        // because we cannot set a new password reset token if any old token exists as
        // PasswordResetToken entity is mapped with one to one relationship with the
        // user
        userService.deletePreviousPasswordResetTokenIfExists(user);
        // publishing a PasswordResetEvent with user and application url(url on which
        // user endpoints exists) which triggers the PasswordResetEventListener
        publisher.publishEvent(new PasswordResetEvent(user, UserApiApplicationUrl));
        // returning the string response to the user as "sent url to reset password
        // successfully"
        return ResponseEntity.ok("sent url to reset password successfully");
    }

    @GetMapping("/isValidPasswordResetToken")
    public ResponseEntity<Boolean> isValidPasswordResetToken(@RequestParam("token") String token) throws Exception {
        User user = userService.validatePasswordResetToken(token);
        if (user == null) {
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(true);
    }

    @PostMapping("/verifyResetPassword")
    // The /verifyResetPassword api endpoint verifies the reset password request
    // which we sent to the user's email with url containing UUID as the token.we
    // are taking token which is sent as a query parameter and the reset password
    // model as
    // a body of the post request which contains newpassword and retypednewpassword
    // as a json with the request
    public ResponseEntity<String> verifyResetPassword(@RequestParam("token") String token,
            @RequestBody @Valid ResetPasswordModel resetPasswordModel) throws Exception {
        // The validatePasswordResetToken checks if the token is valid and not expired
        // and returns the user assosiated with the token
        User user = userService.validatePasswordResetToken(token);

        // deleting the password reset token after verifying it
        userService.deletePasswordResetToken(token);

        // it takes the user object and the reset password model and checks if the new
        // password and retyped new password matches if they match then it saves the
        // new password in the database else it throws an exception
        return ResponseEntity.ok(userService.resetPassword(user, resetPasswordModel));

    }

    // if the user is already logged in and want to change the password then this
    // endpoint is called
    @PostMapping("/changePassword")
    // The changepassword takes the ChangePasswordModel which takes old password,new
    // password and retyped new password as its json body and principal object for
    // detecting the user who is logged in.As principal object contains the email of
    // the user who sent the request as we pass the jwt when we are sending request
    // and the jwt contains the user's email
    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordModel changePasswordModel,
            Principal principal) throws UserNotFoundException, Exception {

        // get's the email from the principal.getName() as we are fetching the email
        // from the jwt using our jwt Token filter and setting the security context
        // using that email details
        String email = principal.getName();
        // get's the user object from the user's email address1
        User user = userService.getUserDetailsByEmail(email);
        // userService.changePassword() takes the user object and change password model
        // as parameters and it compares the old password given by the change password
        // model and the user's password from the database if they both match then it
        // verifies if the new password and retyped new password from the change
        // password model matches if they match then it saves the new password of the
        // user to the database and returns a string as "Password changed successfully"
        return ResponseEntity.ok(userService.changePassword(user, changePasswordModel));

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

    // change this role to USER if you want to access the functionality of deleting
    // the user
    @RolesAllowed("ADMIN")
    @DeleteMapping("/delete")
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