package com.pavansingerreddy.note.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor

@AllArgsConstructor
public class NormalUserModel {

    private String username;
    private String password;
    private String email;

}
