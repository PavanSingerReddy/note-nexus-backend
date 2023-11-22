package com.pavansingerreddy.note.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    @NotBlank(message = "username must not be blank")
    private String username;
    @NotBlank(message = "password should not be blank")
    private String password;
    @NotBlank(message = "retypedPassword should not be blank")
    private String retypedpassword;
    @NotBlank
    @Email
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "Invalid Email Id")
    private String email;

    // @AssertTrue annotation is used to trigger a validation error if the password and retypedPassword does not match
    @AssertTrue(message = "Passwords do not match")
    public boolean ValidatePasswordAndRetypedPassword() {
        return this.password.equals(this.retypedpassword);
    }
}
