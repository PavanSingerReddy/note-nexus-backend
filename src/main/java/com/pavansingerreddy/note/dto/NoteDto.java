package com.pavansingerreddy.note.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// AllArgsConstructor annotation from Lombok generates a constructor with one parameter for each field in your class. Fields are initialized in the order they are declared.
@AllArgsConstructor
// NoArgsConstructor annotation from Lombok generates a no-args constructor.
@NoArgsConstructor
// @Data is a Lombok annotation to create the getters, setters, equals, hash,
// and toString methods
@Data
public class NoteDto {
    private long noteId;
    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private Long userId;
}
