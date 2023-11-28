package com.pavansingerreddy.note.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordModel {
    @NotBlank(message = "new Password must not be blank")
    private String newpassword;

    @NotBlank(message = "Retyped new password must not be blank")
    private String retypednewpassword;

    // checks if the new password and retyped new password are same if they are same
    // then it returns true else it returns false
    public boolean ValidatePasswordAndRetypedPassword() {
        return this.newpassword.equals(this.retypednewpassword);
    }
}
