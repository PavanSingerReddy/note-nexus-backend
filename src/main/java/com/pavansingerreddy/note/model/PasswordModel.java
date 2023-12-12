package com.pavansingerreddy.note.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor

@AllArgsConstructor

public class PasswordModel {

    @NotBlank(message = "Email must not be blank")

    @Email

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "Invalid Email Id")
    private String email;

    private String oldpassword;

    private String newpassword;

    private String retypednewpassword;

    public boolean ValidatePasswordAndRetypedPassword() {
        return this.newpassword.equals(this.retypednewpassword);
    }
}
