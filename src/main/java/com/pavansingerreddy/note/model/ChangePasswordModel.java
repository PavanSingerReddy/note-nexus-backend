package com.pavansingerreddy.note.model;

import jakarta.validation.constraints.NotBlank;
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
public class ChangePasswordModel {
    // @NotBlank is a Hibernate validation annotation which checks that the
    // annotated string is not null and the trimmed length is greater than zero
    @NotBlank(message = "Old password must not be blank")
    private String oldpassword;
    @NotBlank(message = "New Password must not be blank")
    private String newpassword;
    @NotBlank(message = "Re-typed new password must not be blank")
    private String retypednewpassword;

    // checks if the new password and retyped new password are same if they are same
    // then it returns true else it returns false
    public boolean ValidatePasswordAndRetypedPassword() {
        return this.newpassword.equals(this.retypednewpassword);
    }
}
