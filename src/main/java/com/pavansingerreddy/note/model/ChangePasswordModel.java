package com.pavansingerreddy.note.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor

@AllArgsConstructor
public class ChangePasswordModel {

    @NotBlank(message = "Old password must not be blank")
    private String oldpassword;
    @NotBlank(message = "New Password must not be blank")
    private String newpassword;
    @NotBlank(message = "Re-typed new password must not be blank")
    private String retypednewpassword;

    public boolean ValidatePasswordAndRetypedPassword() {
        return this.newpassword.equals(this.retypednewpassword);
    }
}
