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
import com.pavansingerreddy.note.entity.Users;
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

// The @RestController annotation is a convenience annotation in Spring that is used to create RESTful web services. It was introduced in Spring 4.0 to simplify the creation of RESTful web services.

// This annotation is a combination of @Controller and @ResponseBody annotations. The @Controller annotation is used to mark a class as a web request handler, and the @ResponseBody annotation is used to indicate that the return value from a method should be used as the response body for the request.

// By using @RestController, you eliminate the need to annotate every request handling method of the controller class with the @ResponseBody annotation. This means that every method in the controller will automatically serialize return objects into HttpResponse.

@RestController
// RequestMapping annotation is used in spring mvc it can be used both at the
// class level and the method level.When @RequestMapping("/api/user") is used at
// the class level, it means that all request handling methods in this class
// will be relative to the /api/user path. For example, if there is a method in
// the class annotated with @RequestMapping("/get"), the full path to access
// this method would be /api/user/get
@RequestMapping("/api/user")

// This class is a user controller class which handles all the request related
// to the user
public class UserController {

    // making the UserService as final so it can be assigned a value only once so
    // any other accidental assigning of other objects does not happen to it.
    private final UserService userService;

    // making the constructor for UserController class and here the spring injects
    // our UserService object or bean in the constructor by using dependency
    // injection in our parameter automatically so that we can use that object later
    // . Instead of all these we can also use autowired annotation
    UserController(UserService userService) {
        this.userService = userService;
    }

    // autowiring our ApplicationEventPublisher so that the bean or instance of
    // ApplicationEventPublisher will be injected here by the spring IOC container
    @Autowired
    // ApplicationEventPublisher is used for publishing the event's
    private ApplicationEventPublisher publisher;

    // Value annotation is used to inject the value of the property
    // "jwt.token.expiry.seconds" from our application.yml into the field
    // "JWTExpireTimeInSeconds".
    @Value("${jwt.token.expiry.seconds}")
    private Long JWTExpireTimeInSeconds;

    // Value annotation is used to inject the value of the property
    // "frontend.applicationUrl.user.api" from our application.yml into the field
    // "UserApiApplicationUrl".
    @Value("${frontend.applicationUrl.user.api}")
    private String UserApiApplicationUrl;

    // GetMapping annotation is used to map the Http GET request to this
    // controller.This controller listens for the "/api/user/is-authenticated" http
    // get request.The GetMapping annotation act's as a shortcut for
    // @RequestMapping(method = RequestMethod.GET) annotation
    @GetMapping("/is-authenticated")
    // This method takes a Principal object as a parameter which we get from our
    // security context. The Principal object represents the currently authenticated
    // user. Here it returns ResponseEntity<?>, ResponseEntity<?> is used so that it
    // can return a response body of any type. The ? is a wildcard that stands for
    // “any type”. This is useful because depending on the situation, the method
    // might need to return different types of bodies. In this case, it’s returning
    // a Boolean value (true or false), but it could be set up to return other types
    // if needed.
    public ResponseEntity<?> checkAuthentication(Principal principal) {
        // This is a condition that checks if the Principal object is not null and if
        // the name of the Principal is not null. If both conditions are true, it means
        // the user is authenticated.
        if (principal != null && principal.getName() != null) {
            // If the user is authenticated, the method returns a ResponseEntity with a
            // status of 200 OK and a body of true
            return ResponseEntity.ok(true);
        }
        // If the user is not authenticated (i.e., the Principal object or its name is
        // null), the method returns a ResponseEntity with a status of 401 Unauthorized
        // and a body of false
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    }

    // This method is used to obtain the csrf token for protecting our website from
    // csrf attacks
    @GetMapping("/csrf-token")
    public ResponseEntity<String> getCsrf() {
        return ResponseEntity.ok("Initial CSRF Token Request Is Successful !!!");
    }

    // @CrossOrigin(origins = "*") // allows cors for this method or we can use this
    // for method or for entire controller class

    // PostMapping annotation is used to map the Http POST request to this
    // controller.This controller listens for the "/api/user/register" http
    // post request.The PostMapping annotation act's as a shortcut for
    // @RequestMapping(method = RequestMethod.POST) annotation
    @PostMapping("/register")
    // This method returns a UserDto(Data Transfer Objects) which contains the
    // details of the user which can be used by our frontend client. it uses
    // RequestBody annotation which is used to get json data from our post request
    // to our user model class.we are using @Valid annotation because
    // we want to validate the request's json body input.
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserModel userModel)
            throws UserAlreadyExistsException, PasswordDoesNotMatchException, InvalidUserDetailsException {
        // we have multiple mail senders in our application.yml file so this method
        // getLatestEmailToUse() returns us the appropriate mail sender to use to send
        // email so that we don't get marked as spam by our email service provider like
        // gmail or outlook.
        int mailNoToUse = userService.getLatestEmailToUse();
        // createUser method from the userService is used to check if the password and
        // retyped password match and also used to check if the user is already present
        // and already enable then it throws an exception and if the user is present and
        // not enabled then it deletes the previous user and creates a new user.if the
        // user is not present then it creates a new user with the details provided by
        // the userModel and the mailNoToUse variable
        Users user = userService.createUser(userModel, mailNoToUse);
        // publishing a new RegistrationCompleteEvent which send's the event for sending
        // an email for the registered user with the verification token.We are using
        // event here as sending an email for the user is a separate task from the main
        // task of creating a new user.Here application url contains the root path or
        // url of the frontend application and User object contains the details of the
        // registered new user and the mailNoToUse contains the mail number from our
        // mail providers to use for sending the email for verification
        publisher.publishEvent(new RegistrationCompleteEvent(user, UserApiApplicationUrl, mailNoToUse));
        // after publishing the event we are returning the response of user information
        // using UserDto as we made the RegistrationCompleteEventListener Async we will
        // not wait for the mail to get sent the event runs on a separate thread and the
        // response get's sent after we publish the event
        return ResponseEntity.ok(DTOConversionUtil.userToUserDTO(user));
    }

    // api for verifying the registration of the user.This api is called from the
    // frontend to verify the registered user
    @GetMapping("/verifyRegistration")
    // This method is used to verify the registered user and after verifying the
    // user we are sending the "User Verified Successfully" string as a
    // response.This method uses RequestParam annotation which is used to get the
    // query parameters from the request here we are getting the query parameter
    // named token
    public ResponseEntity<String> verifyRegistration(@RequestParam("token") String token)
            throws InvalidUserDetailsException {
        // validateVerificationToken method checks if the given token is valid or not
        // and the token is expired or not and if the token is valid and not expired
        // then it enables the user associated with the token and returns true if the
        // user is enabled and the token is valid or false if the user is not enabled
        // and the token is invalid.
        boolean result = userService.validateVerificationToken(token);
        // if the result is true then we are sending a Http response of 200(ok) with the
        // "User Verified Successfully" String
        if (result) {
            return ResponseEntity.ok("User Verified Successfully");
        }
        // else we throw an exception with message as Bad User Details
        throw new InvalidUserDetailsException("Bad User Details");
    }

    // api for resending the verification token for enabling the user using the
    // registered user's email
    @PostMapping("/resendVerifyToken")
    // This method returns the User Dto based on the given email address to resend
    // the verification token this method takes the Email address from it's POST
    // request body using the RequestBody annotation and the email id is populated
    // in to the PasswordModel and also it validates the email using the Valid
    // annotation
    public ResponseEntity<UserDto> resendVerificationToken(@RequestBody @Valid PasswordModel passwordModel)
            throws UserNotFoundException, InvalidUserDetailsException {
        // getting the email from the password model
        String email = passwordModel.getEmail();
        // getting the user details by his/her email address.
        Users user = userService.getUserDetailsByEmail(email);
        // checking if the user is enabled or not
        boolean isUserEnabled = user.isEnabled();
        // if the user is already enabled then we are throwing exception as "User is
        // already verified"
        if (isUserEnabled) {
            throw new InvalidUserDetailsException("User is already verified");
        }
        // deletePreviousTokenIfExists checks if there exists any previous verification
        // token if it exists then we are deleting the verification token for that
        // particular user
        userService.deletePreviousTokenIfExists(user);
        // now we are publishing the RegistrationCompleteEvent with the user object,
        // frontend application url and the mailNoToUse which contains the mail number
        // from our mail providers to use for sending the email for verification which
        // triggers the RegistrationCompleteEventListener and
        // RegistrationCompleteEventListener creates a new verification token and
        // associates it with the user and send's the email to the user with the url for
        // verifying the verification token
        publisher.publishEvent(
                new RegistrationCompleteEvent(user, UserApiApplicationUrl, user.getMailNoToUseForSendingEmail()));
        // returning the http 200(ok) response with UserDto by converting the user to
        // the userDto by our custom made class called DTOConversionUtil
        return ResponseEntity.ok(DTOConversionUtil.userToUserDTO(user));

    }

    // post api endpoint for resetting the forgotten password it send's an email
    // with the link to reset the password
    @PostMapping("/resetPassword")
    // The request should contain the email of the user who forgot the password and
    // want to reset the password
    public ResponseEntity<String> resetPassword(@RequestBody @Valid PasswordModel passwordModel)
            throws UserNotFoundException, InvalidUserDetailsException {
        // userService.getUserDetailsByEmail() returns the user object with their email
        // id
        Users user = userService.getUserDetailsByEmail(passwordModel.getEmail());
        // checking if the user is enabled or not if the user is not enabled then we
        // throw the exception as "User is not verified.Verify the user first"
        if (!user.isEnabled()) {
            throw new InvalidUserDetailsException("User is not verified.Verify the user first");
        }

        // if there exists any previous password reset token for this user we delete it
        // because we cannot set a new password reset token if any old token exists as
        // PasswordResetToken entity is mapped with one to one relationship with the
        // user
        userService.deletePreviousPasswordResetTokenIfExists(user);
        // publishing a PasswordResetEvent with user, application url(front end root
        // url which is used for resetting the password) and and
        // getMailNoToUseForSendingEmail() contains the mail number from our mail
        // providers to use for sending the email for resetting the password which
        // triggers the PasswordResetEventListener
        publisher.publishEvent(
                new PasswordResetEvent(user, UserApiApplicationUrl, user.getMailNoToUseForSendingEmail()));
        // returning the string response to the user as "sent url to reset password
        // successfully"
        return ResponseEntity.ok("sent url to reset password successfully");
    }

    // This endpoint is used to check if the password reset token is valid or not
    @GetMapping("/isValidPasswordResetToken")
    // This method is used to check if the password reset token is valid or not.It
    // takes the password reset token as a query parameter the RequestParam
    // annotation is used to extract that password reset token
    public ResponseEntity<Boolean> isValidPasswordResetToken(@RequestParam("token") String token)
            throws InvalidUserDetailsException {
        // checks if the password reset token is present and also checks if the user
        // associated with that password reset token also exists then it checks if the
        // token is expired or not if the token is not expired then we get the user
        // associated with that token
        Users user = userService.validatePasswordResetToken(token);
        // if the user is null then we are returning false as the user is not present
        if (user == null) {
            // returning the response as false as the user associated with the token is not
            // present
            return ResponseEntity.ok(false);
        }
        // if the user is present then return true
        return ResponseEntity.ok(true);
    }

    @PostMapping("/verifyResetPassword")
    // The /verifyResetPassword api endpoint verifies the reset password request
    // which we sent to the user's email with url containing UUID as the token.we
    // are taking token which is sent as a query parameter and the reset password
    // model as a body of the post request which contains newpassword and
    // retypednewpassword as a json with the http POST request.and we are validating
    // that newpassword and retypednewpassword should be present using @Valid
    // annotation
    public ResponseEntity<String> verifyResetPassword(@RequestParam("token") String token,
            @RequestBody @Valid ResetPasswordModel resetPasswordModel) throws InvalidUserDetailsException {
        // The validatePasswordResetToken checks if the token is valid and not expired
        // and returns the user associated with the token
        Users user = userService.validatePasswordResetToken(token);

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
    // The changepassword method takes the ChangePasswordModel which takes old
    // password,new password and retyped new password as its json body and principal
    // object for detecting the user who is logged in.As principal object contains
    // the email of the user who sent the request as we pass the jwt when we are
    // sending request and the jwt contains the user's email.we are using @Valid
    // annotation because we want to validate the request's json body input to not
    // include any blank inputs.
    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordModel changePasswordModel,
            Principal principal) throws UserNotFoundException, InvalidUserDetailsException {

        // get's the email from the principal.getName() as we are fetching the email
        // from the jwt using our jwt Token filter and setting the security context
        // using that email details
        String email = principal.getName();
        // get's the user object from the user's email address
        Users user = userService.getUserDetailsByEmail(email);
        // userService.changePassword() takes the user object and change password model
        // as parameters and it compares the old password given by the change password
        // model and the user's password from the database if they both match then it
        // verifies if the new password and retyped new password from the change
        // password model matches if they match then it saves the new password of the
        // user to the database and returns a string as "Password changed successfully"
        return ResponseEntity.ok(userService.changePassword(user, changePasswordModel));

    }

    // @PostMapping is a Spring annotation that maps HTTP POST requests onto this
    // method. "api/user/login" is the path at which this method will be available.
    @PostMapping("/login")
    // api endpoint used for logging in the user by sending the jwt token to the
    // user @RequestBody is a Spring annotation which is expecting a JSON object in
    // the request body which will be converted to a NormalUserModel object.
    // HttpServletResponse is used to produce the response in this request-response
    // cycle.
    public ResponseEntity<?> loginUser(@RequestBody NormalUserModel normalUserModel, HttpServletResponse response) {
        // Calls a method in userService to authenticate the user and generate a JWT
        // (JSON Web Token). The JWT is returned as a Map where the key is "jwt" and the
        // value is the actual token.
        Map<String, String> jwtToken = userService.loginUser(normalUserModel);

        // Creates a cookie that will be sent in the response. The cookie's name is
        // "JWT", and its value is the JWT obtained from userService.loginUser().

        ResponseCookie cookie = ResponseCookie.from("JWT", jwtToken.get("jwt"))
                // The cookie is marked as HttpOnly for security (it's not accessible via
                // JavaScript).
                .httpOnly(true)
                // Uncomment this line if you want the cookie to be sent only over HTTPS.
                // .secure(true)
                // The cookie will only be sent in a first-party context and not be sent along
                // with requests initiated by third party websites.
                .sameSite("strict")
                // The cookie will be accessible within the entire domain.
                .path("/")
                // The cookie will expire after JWTExpireTimeInSeconds seconds.
                .maxAge(JWTExpireTimeInSeconds)
                // now we build the response cookie
                .build();

        // Adds the cookie to the response headers.
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        // Returns a ResponseEntity with a status of 200 OK. ResponseEntity represents
        // the entire HTTP response. Good thing about it is that you can control
        // anything that goes into it.
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // @GetMapping is a Spring annotation that maps HTTP GET requests onto this
    // method."api/user/get" is the path at which this method will be available.
    @GetMapping("/get")
    // @RolesAllowed is a Java annotation used to specify the security roles
    // permitted to access method(s) in an application. The "USER" role is allowed
    // to access this method.
    @RolesAllowed("USER")
    // This method returns a ResponseEntity containing a UserDto object.
    // It throws a UserNotFoundException if the user is not found.
    // The Principal object represents the currently authenticated user.
    public ResponseEntity<UserDto> getUserDetailsByEmail(Principal principal) throws UserNotFoundException {
        // Get the name (email) of the authenticated user.
        String userEmail = principal.getName();
        // Call a method in userService to get the User object associated with the
        // email.
        Users user = userService.getUserDetailsByEmail(userEmail);
        // Convert the User object to a UserDto object using a utility method in
        // DTOConversionUtil. Return the UserDto object in the response with a status of
        // 200 OK.
        return ResponseEntity.ok(DTOConversionUtil.userToUserDTO(user));
    }

    // @PutMapping is a Spring annotation that maps HTTP PUT requests onto this
    // method. "api/user/edit" is the path at which this method will be available.
    @PutMapping("/edit")
    // @RolesAllowed is a Java annotation used to specify the security roles
    // permitted to access method(s) in an application. The "USER" role is allowed
    // to access this method.
    @RolesAllowed("USER")
    // This method returns a ResponseEntity containing a UserDto object.
    // It throws a UserNotFoundException if the user is not found.
    // The Principal object represents the currently authenticated user.
    // @RequestBody is a Spring annotation which is expecting a JSON object in
    // the request body which will be converted to a NormalUserModel object.
    public ResponseEntity<UserDto> updateUserInformationByEmail(Principal principal,
            @RequestBody NormalUserModel normalUserModel) throws UserNotFoundException {
        // Get the name (email) of the authenticated user.
        String userEmail = principal.getName();
        // Call a method in userService to update the User object associated with the
        // email with the details from the NormalUserModel object. Return the updated
        // UserDto object in the response with a status of 200 OK.
        return ResponseEntity.ok(userService.updateUserInformationByEmail(userEmail, normalUserModel));
    }

    // @RolesAllowed is a Java annotation used to specify the security roles
    // permitted to access method(s) in an application. The "ADMIN" role is allowed
    // to access this method.

    // change this role from "ADMIN" to "USER" if you want to access the
    // functionality of deleting the user
    @RolesAllowed("ADMIN")
    // @DeleteMapping is a Spring annotation that maps HTTP DELETE requests onto
    // this method. "api/user/delete" is the path at which this method will be
    // available.
    @DeleteMapping("/delete")
    // This method returns a ResponseEntity containing a UserDto object.
    // It throws a UserNotFoundException if the user is not found.
    // The Principal object represents the currently authenticated user.
    public ResponseEntity<UserDto> deleteUserByEmail(Principal principal) throws UserNotFoundException {
        // Get the name (email) of the authenticated user.
        String userEmail = principal.getName();
        // Call a method in userService to delete the User object associated with the
        // email. Return the deleted UserDto object in the response with a status of 200
        // OK.
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