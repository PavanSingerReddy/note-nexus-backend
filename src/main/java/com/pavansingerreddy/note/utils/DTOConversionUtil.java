package com.pavansingerreddy.note.utils;

import java.util.ArrayList;
import java.util.List;

import com.pavansingerreddy.note.dto.NoteDto;
import com.pavansingerreddy.note.dto.PagableNoteDto;
import com.pavansingerreddy.note.dto.UserDto;
import com.pavansingerreddy.note.entity.Note;
import com.pavansingerreddy.note.entity.User;
import com.pavansingerreddy.note.model.NoteModel;

public class DTOConversionUtil {
    
    public static UserDto userToUserDTO(User user) {
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername() != null ? user.getUsername() : "");
        userDto.setEmail(user.getEmail()!= null?user.getEmail() : "");
        List<Long> noteIds = new ArrayList<>();
        if(user.getNotes() != null){
            user.getNotes().stream()
                .map(Note::getNoteId)
                .forEach(noteIds::add);
        }
        userDto.setNoteIds(noteIds);
        return userDto;
    }

    public static NoteDto noteToNoteDTO(Note note){

        NoteDto noteDto = new NoteDto();
        noteDto.setNoteId(note.getNoteId() !=0L ? note.getNoteId(): 0L);
        noteDto.setTitle(note.getTitle() != null ? note.getTitle():"");
        noteDto.setContent(note.getContent() != null ? note.getContent() : "");
        if (note.getCreatedAt() != null) {
            noteDto.setCreatedAt(note.getCreatedAt());
        }        

        if(note.getUpdatedAt() != null){
            noteDto.setUpdatedAt(note.getUpdatedAt());
        }
        noteDto.setUserId(note.getUser().getUserId() != 0L ? note.getUser().getUserId():0L);
        return noteDto;
    }

    public static PagableNoteDto noteToPagableNoteDto(Note note,long totalPages){

        PagableNoteDto pagableNoteDto = new PagableNoteDto();
        pagableNoteDto.setNoteId(note.getNoteId() !=0L ? note.getNoteId(): 0L);
        pagableNoteDto.setTitle(note.getTitle() != null ? note.getTitle():"");
        pagableNoteDto.setContent(note.getContent() != null ? note.getContent() : "");
        if (note.getCreatedAt() != null) {
            pagableNoteDto.setCreatedAt(note.getCreatedAt());
        }        

        if(note.getUpdatedAt() != null){
            pagableNoteDto.setUpdatedAt(note.getUpdatedAt());
        }
        pagableNoteDto.setUserId(note.getUser().getUserId() != 0L ? note.getUser().getUserId():0L);
        pagableNoteDto.setTotalPages(totalPages);
        return pagableNoteDto;
    }

    public static Note noteModelToNote(NoteModel noteModel,Note note){

        note.setTitle(noteModel.getTitle()!=null? noteModel.getTitle():note.getTitle());
        note.setContent(noteModel.getContent()!=null ? noteModel.getContent() : note.getContent());
        return note;
    }

}
