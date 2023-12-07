package com.pavansingerreddy.note.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
// This is a model which is used for storing the data of incoming notes of the
// user
public class NoteModel {
    // @NotBlank is a Hibernate validation annotation which checks that the
    // annotated string is not null and the trimmed length is greater than zero
    @NotBlank(message = "Note Title must not be empty")
    private String title;
    // @NotNull: This annotation is used to specify that a field must not be null.
    // However, it doesn’t check whether a string is empty or contains only
    // whitespace.
    @NotNull(message = "Note content must not be null")
    private String content;
}
