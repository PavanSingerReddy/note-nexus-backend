package com.pavansingerreddy.note.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pavansingerreddy.note.dto.NoteDto;
import com.pavansingerreddy.note.exception.NoteDoesNotExistsException;
import com.pavansingerreddy.note.exception.UserNotFoundException;
import com.pavansingerreddy.note.model.NoteModel;
import com.pavansingerreddy.note.services.NoteService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class NotesController {

    @Autowired
    NoteService noteService;

    @PostMapping("/{userEmail}/notes")
    @RolesAllowed("USER")
    @PreAuthorize("#userEmail == authentication.principal")
    public ResponseEntity<NoteDto> createNewNote(@PathVariable("userEmail") String userEmail,@RequestBody @Valid NoteModel noteModel) throws UserNotFoundException{
       return ResponseEntity.ok(noteService.createNewNote(noteModel,userEmail));
    }


    @GetMapping("/{userEmail}/notes/{noteId}")
    @RolesAllowed("USER")
    @PreAuthorize("#userEmail == authentication.principal")
    public ResponseEntity<NoteDto> getASpecificNote(@PathVariable("userEmail")String userEmail,@PathVariable("noteId") Long noteId) throws NoteDoesNotExistsException{
        return ResponseEntity.ok(noteService.getASpecificNote(userEmail,noteId));
    }


    @GetMapping("/{userEmail}/notes")
    @RolesAllowed("USER")
    @PreAuthorize("#userEmail == authentication.principal")
    public ResponseEntity<List<NoteDto>> getAllNotes(@PathVariable("userEmail") String userEmail) throws NoteDoesNotExistsException {
        return ResponseEntity.ok(noteService.getAllNotes(userEmail));
    }
    
    @PutMapping("/{userEmail}/notes/{noteId}")
    @RolesAllowed("USER")
    @PreAuthorize("#userEmail == authentication.principal")
    public ResponseEntity<NoteDto> updateSpecificNote(@PathVariable("userEmail") String userEmail,@PathVariable("noteId") Long noteId,@RequestBody NoteModel noteModel) throws NoteDoesNotExistsException {
        return ResponseEntity.ok(noteService.updateSpecificNote(userEmail,noteId,noteModel));
    }

    @DeleteMapping("/{userEmail}/notes/{noteId}")
    @RolesAllowed("USER")
    @PreAuthorize("#userEmail == authentication.principal")
    public ResponseEntity<NoteDto> deleteASpecificNote(@PathVariable("userEmail") String userEmail,@PathVariable("noteId") Long noteId) throws NoteDoesNotExistsException {
        return ResponseEntity.ok(noteService.deleteASpecificNote(userEmail,noteId));
    }
}
