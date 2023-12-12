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
import com.pavansingerreddy.note.exception.InvalidUserDetailsException;
import com.pavansingerreddy.note.exception.PasswordDoesNotMatchException;
import com.pavansingerreddy.note.exception.UserAlreadyExistsException;
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
        return ResponseEntity.ok("Initial CSRF Token Request Is Successful !!!");
    }

    @PostMapping("/register")

    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserModel userModel)
            throws UserAlreadyExistsException, PasswordDoesNotMatchException, InvalidUserDetailsException {

        int mailNoToUse = userService.getLatestEmailToUse();

        User user = userService.createUser(userModel, mailNoToUse);

        publisher.publishEvent(new RegistrationCompleteEvent(user, UserApiApplicationUrl, mailNoToUse));

        return ResponseEntity.ok(DTOConversionUtil.userToUserDTO(user));
    }

    @GetMapping("/verifyRegistration")

    public ResponseEntity<String> verifyRegistration(@RequestParam("token") String token)
            throws InvalidUserDetailsException {

        boolean result = userService.validateVerificationToken(token);

        if (result) {
            return ResponseEntity.ok("User Verified Successfully");
        }

        throw new InvalidUserDetailsException("Bad User Details");
    }

    @PostMapping("/resendVerifyToken")

    public ResponseEntity<UserDto> resendVerificationToken(@RequestBody @Valid PasswordModel passwordModel)
            throws UserNotFoundException, InvalidUserDetailsException {

        String email = passwordModel.getEmail();

        User user = userService.getUserDetailsByEmail(email);

        boolean isUserEnabled = user.isEnabled();

        if (isUserEnabled) {
            throw new InvalidUserDetailsException("User is already verified");
        }

        userService.deletePreviousTokenIfExists(user);

        publisher.publishEvent(
                new RegistrationCompleteEvent(user, UserApiApplicationUrl, user.getMailNoToUseForSendingEmail()));

        return ResponseEntity.ok(DTOConversionUtil.userToUserDTO(user));

    }

    @PostMapping("/resetPassword")

    public ResponseEntity<String> resetPassword(@RequestBody @Valid PasswordModel passwordModel)
            throws UserNotFoundException, InvalidUserDetailsException {

        User user = userService.getUserDetailsByEmail(passwordModel.getEmail());

        if (!user.isEnabled()) {
            throw new InvalidUserDetailsException("User is not verified.Verify the user first");
        }

        userService.deletePreviousPasswordResetTokenIfExists(user);

        publisher.publishEvent(
                new PasswordResetEvent(user, UserApiApplicationUrl, user.getMailNoToUseForSendingEmail()));

        return ResponseEntity.ok("sent url to reset password successfully");
    }

    @GetMapping("/isValidPasswordResetToken")

    public ResponseEntity<Boolean> isValidPasswordResetToken(@RequestParam("token") String token)
            throws InvalidUserDetailsException {

        User user = userService.validatePasswordResetToken(token);

        if (user == null) {

            return ResponseEntity.ok(false);
        }

        return ResponseEntity.ok(true);
    }

    @PostMapping("/verifyResetPassword")

    public ResponseEntity<String> verifyResetPassword(@RequestParam("token") String token,
            @RequestBody @Valid ResetPasswordModel resetPasswordModel) throws InvalidUserDetailsException {

        User user = userService.validatePasswordResetToken(token);

        userService.deletePasswordResetToken(token);

        return ResponseEntity.ok(userService.resetPassword(user, resetPasswordModel));

    }

    @PostMapping("/changePassword")

    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordModel changePasswordModel,
            Principal principal) throws UserNotFoundException, InvalidUserDetailsException {

        String email = principal.getName();

        User user = userService.getUserDetailsByEmail(email);

        return ResponseEntity.ok(userService.changePassword(user, changePasswordModel));

    }

    @PostMapping("/login")

    public ResponseEntity<?> loginUser(@RequestBody NormalUserModel normalUserModel, HttpServletResponse response) {

        Map<String, String> jwtToken = userService.loginUser(normalUserModel);

        ResponseCookie cookie = ResponseCookie.from("JWT", jwtToken.get("jwt"))

                .httpOnly(true)

                .sameSite("strict")

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

    @RolesAllowed("ADMIN")

    @DeleteMapping("/delete")

    public ResponseEntity<UserDto> deleteUserByEmail(Principal principal) throws UserNotFoundException {

        String userEmail = principal.getName();

        return ResponseEntity.ok(userService.deleteUserByEmail(userEmail));
    }

}
