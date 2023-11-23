package com.pavansingerreddy.note.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordModel {
    @NotBlank(message = "Email must not be blank")
    private String email;

    private String oldpassword;

    private String newpassword;

    private String retypednewpassword;

    // checks if the new password and retyped new password are same if they are same then it returns true else it returns false
    public boolean ValidatePasswordAndRetypedPassword() {
        return this.newpassword.equals(this.retypednewpassword);
    }
}
