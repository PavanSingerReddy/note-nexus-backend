package com.pavansingerreddy.note.utils;

import java.util.ArrayList;
import java.util.List;

import com.pavansingerreddy.note.dto.NoteDto;
import com.pavansingerreddy.note.dto.PagableNoteDto;
import com.pavansingerreddy.note.dto.UserDto;
import com.pavansingerreddy.note.entity.Note;
import com.pavansingerreddy.note.entity.Users;
import com.pavansingerreddy.note.model.NoteModel;

// This is a utility class which is used for converting the original entity to it's DTO
public class DTOConversionUtil {

    // This method converts a User object to a UserDto object.
    public static UserDto userToUserDTO(Users user) {
        // Create a new UserDto object.
        UserDto userDto = new UserDto();
        // Set the username in the UserDto. If the username in the User is null, set it
        // to an empty string.
        userDto.setUsername(user.getUsername() != null ? user.getUsername() : "");
        // Set the email in the UserDto. If the email in the User is null, set it to an
        // empty string.
        userDto.setEmail(user.getEmail() != null ? user.getEmail() : "");
        // Create a new list to hold the note IDs.
        List<Long> noteIds = new ArrayList<>();
        // If the notes in the User are not null,
        if (user.getNotes() != null) {
            // get a stream of the notes,
            user.getNotes().stream()
                    // map each note to its note ID,
                    .map(Note::getNoteId)
                    // and add each note ID to the list.
                    .forEach(noteIds::add);
        }
        // Set the note IDs in the UserDto.
        userDto.setNoteIds(noteIds);
        // Return the UserDto.
        return userDto;
    }

    // This method converts a Note object to a NoteDto object.
    public static NoteDto noteToNoteDTO(Note note) {

        // Create a new NoteDto object.
        NoteDto noteDto = new NoteDto();
        // Set the note ID in the NoteDto. If the note ID in the Note is 0, set it to 0.
        noteDto.setNoteId(note.getNoteId() != 0L ? note.getNoteId() : 0L);
        // Set the title in the NoteDto. If the title in the Note is null, set it to an
        // empty string.
        noteDto.setTitle(note.getTitle() != null ? note.getTitle() : "");
        // Set the content in the NoteDto. If the content in the Note is null, set it to
        // an empty string.
        noteDto.setContent(note.getContent() != null ? note.getContent() : "");
        // If the created at time in the Note is not null
        if (note.getCreatedAt() != null) {
            // set the created at time in the NoteDto.
            noteDto.setCreatedAt(note.getCreatedAt());
        }
        // If the updated at time in the Note is not null,
        if (note.getUpdatedAt() != null) {
            // set the updated at time in the NoteDto.
            noteDto.setUpdatedAt(note.getUpdatedAt());
        }
        // Set the user ID in the NoteDto. If the user ID in the Note is 0, set it to 0.
        noteDto.setUserId(note.getUser().getUserId() != 0L ? note.getUser().getUserId() : 0L);
        // Return the NoteDto.
        return noteDto;
    }

    // This method converts a Note object to a PagableNoteDto object.
    public static PagableNoteDto noteToPagableNoteDto(Note note, long totalPages) {

        // Create a new PagableNoteDto object.
        PagableNoteDto pagableNoteDto = new PagableNoteDto();
        // Set the note ID in the PagableNoteDto. If the note ID in the Note is 0, set
        // it to 0.
        pagableNoteDto.setNoteId(note.getNoteId() != 0L ? note.getNoteId() : 0L);
        // Set the title in the PagableNoteDto. If the title in the Note is null, set it
        // to an empty string.
        pagableNoteDto.setTitle(note.getTitle() != null ? note.getTitle() : "");
        // Set the content in the PagableNoteDto. If the content in the Note is null,
        // set it to an empty string.
        pagableNoteDto.setContent(note.getContent() != null ? note.getContent() : "");
        // If the created at time in the Note is not null,
        if (note.getCreatedAt() != null) {
            // set the created at time in the PagableNoteDto.
            pagableNoteDto.setCreatedAt(note.getCreatedAt());
        }

        // If the updated at time in the Note is not null,
        if (note.getUpdatedAt() != null) {
            // set the updated at time in the PagableNoteDto.
            pagableNoteDto.setUpdatedAt(note.getUpdatedAt());
        }
        // Set the user ID in the PagableNoteDto. If the user ID in the Note is 0, set
        // it to 0.
        pagableNoteDto.setUserId(note.getUser().getUserId() != 0L ? note.getUser().getUserId() : 0L);
        // Set the total pages in the PagableNoteDto.
        pagableNoteDto.setTotalPages(totalPages);
        // Return the PagableNoteDto.
        return pagableNoteDto;
    }

    // This method converts a NoteModel object to a Note object.
    public static Note noteModelToNote(NoteModel noteModel, Note note) {

        // Set the title in the Note. If the title in the NoteModel is null, keep the
        // original title in the Note.
        note.setTitle(noteModel.getTitle() != null ? noteModel.getTitle() : note.getTitle());
        // Set the content in the Note. If the content in the NoteModel is null, keep
        // the original content in the Note.
        note.setContent(noteModel.getContent() != null ? noteModel.getContent() : note.getContent());
        // Return the Note.
        return note;
    }

}
