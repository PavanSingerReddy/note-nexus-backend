package com.pavansingerreddy.note.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// AllArgsConstructor annotation from Lombok generates a constructor with one parameter for each field in your class. Fields are initialized in the order they are declared.
@Data
// NoArgsConstructor annotation from Lombok generates a no-args constructor.
@NoArgsConstructor
// @Data is a Lombok annotation to create the getters, setters, equals, hash,
// and toString methods
@AllArgsConstructor
public class UserDto {
    private String username;
    private String email;
    private List<Long> noteIds;
}
