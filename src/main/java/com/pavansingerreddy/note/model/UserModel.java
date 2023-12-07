package com.pavansingerreddy.note.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// @Data is a Lombok annotation to create the getters, setters, equals, hash, and toString methods
@Data
// @NoArgsConstructor is a Lombok annotation for generating a constructor with
// no parameters
@NoArgsConstructor
// @AllArgsConstructor is a Lombok annotation for generating a constructor with
// all parameters
@AllArgsConstructor

// This model is used while creating a new user using /register api endpoint
public class UserModel {
    // @NotBlank is a Hibernate validation annotation which checks that the
    // annotated string is not null and the trimmed length is greater than zero
    @NotBlank(message = "username must not be blank")
    private String username;
    @NotBlank(message = "password should not be blank")
    private String password;
    @NotBlank(message = "retypedPassword should not be blank")
    private String retypedpassword;
    @NotBlank
    // @Email is a Hibernate validation annotation which validates that the
    // annotated string is a well-formed email address
    @Email
    // @Pattern is a Hibernate validation annotation which validates that the
    // annotated string matches the regular expression.Here we are validating the
    // regular expression that the email uses
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "Invalid Email Id")
    private String email;

    // @AssertTrue is a Hibernate validation annotation which checks that the
    // annotated method (or computed property) returns trueHere @AssertTrue
    // annotation is used to trigger a validation error if the password and
    // retypedPassword does not match
    @AssertTrue(message = "Passwords do not match")
    // This method checks that the password and retypedPassword fields have the same
    // value
    public boolean ValidatePasswordAndRetypedPassword() {
        return this.password.equals(this.retypedpassword);
    }
}
