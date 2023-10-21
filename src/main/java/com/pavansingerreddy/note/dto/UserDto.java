package com.pavansingerreddy.note.dto;

import java.util.List;

import com.pavansingerreddy.note.entity.Note;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String username;
    private String email;
    private List<Note> notes;
}
