package com.pavansingerreddy.note.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor

@NoArgsConstructor

@Data
public class NoteDto {
    private long noteId;
    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private Long userId;
}
