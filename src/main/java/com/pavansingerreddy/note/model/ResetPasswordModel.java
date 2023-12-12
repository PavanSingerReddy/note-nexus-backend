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

    public boolean ValidatePasswordAndRetypedPassword() {
        return this.newpassword.equals(this.retypednewpassword);
    }
}
