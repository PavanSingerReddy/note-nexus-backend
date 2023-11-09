package com.pavansingerreddy.note.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/notes")
public class NotesController {

    @Autowired
    NoteService noteService;

    @PostMapping("/create")
    @RolesAllowed("USER")
    public ResponseEntity<NoteDto> createNewNote(Principal principal,@RequestBody @Valid NoteModel noteModel) throws UserNotFoundException{
        String userEmail = principal.getName();
       return ResponseEntity.ok(noteService.createNewNote(noteModel,userEmail));
    }


    @GetMapping("/get/{noteId}")
    @RolesAllowed("USER")
    public ResponseEntity<NoteDto> getASpecificNote(Principal principal,@PathVariable("noteId") Long noteId) throws NoteDoesNotExistsException{
        String userEmail = principal.getName();
        return ResponseEntity.ok(noteService.getASpecificNote(userEmail,noteId));
    }


    @GetMapping("/get")
    @RolesAllowed("USER")
    public ResponseEntity<List<NoteDto>> getAllNotes(Principal principal) throws NoteDoesNotExistsException {
        String userEmail = principal.getName();
        return ResponseEntity.ok(noteService.getAllNotes(userEmail));
    }
    
    @PutMapping("/edit/{noteId}")
    @RolesAllowed("USER")
    public ResponseEntity<NoteDto> updateSpecificNote(Principal principal,@PathVariable("noteId") Long noteId,@RequestBody NoteModel noteModel) throws NoteDoesNotExistsException {
        String userEmail = principal.getName();
        return ResponseEntity.ok(noteService.updateSpecificNote(userEmail,noteId,noteModel));
    }

    @DeleteMapping("/delete/{noteId}")
    @RolesAllowed("USER")
    public ResponseEntity<NoteDto> deleteASpecificNote(Principal principal,@PathVariable("noteId") Long noteId) throws NoteDoesNotExistsException {
        String userEmail = principal.getName();
        return ResponseEntity.ok(noteService.deleteASpecificNote(userEmail,noteId));
    }
}
