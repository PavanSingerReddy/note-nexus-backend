package com.pavansingerreddy.note.model;

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
// This model is used while resetting the password or while resending the verification token to verify user
public class PasswordModel {
    // @NotBlank is a Hibernate validation annotation which checks that the
    // annotated string is not null and the trimmed length is greater than zero
    @NotBlank(message = "Email must not be blank")
    // @Email is a Hibernate validation annotation which validates that the
    // annotated string is a well-formed email address
    @Email
    // @Pattern is a Hibernate validation annotation which validates that the
    // annotated string matches the regular expression.Here we are validating the
    // regular expression that the email uses
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "Invalid Email Id")
    private String email;

    private String oldpassword;

    private String newpassword;

    private String retypednewpassword;

    // checks if the new password and retyped new password are same if they are same
    // then it returns true else it returns false
    public boolean ValidatePasswordAndRetypedPassword() {
        return this.newpassword.equals(this.retypednewpassword);
    }
}
