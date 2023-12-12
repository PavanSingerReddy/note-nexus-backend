package com.pavansingerreddy.note.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor

@AllArgsConstructor

public class NoteModel {

    @NotBlank(message = "Note Title must not be empty")
    private String title;

    @NotNull(message = "Note content must not be null")
    private String content;
}
