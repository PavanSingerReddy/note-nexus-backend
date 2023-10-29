package com.pavansingerreddy.note.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pavansingerreddy.note.dto.NoteDto;
import com.pavansingerreddy.note.entity.Note;
import com.pavansingerreddy.note.entity.User;
import com.pavansingerreddy.note.exception.NoteDoesNotExistsException;
import com.pavansingerreddy.note.exception.UserNotFoundException;
import com.pavansingerreddy.note.model.NoteModel;
import com.pavansingerreddy.note.repository.NoteRepository;
import com.pavansingerreddy.note.repository.UserRepository;
import com.pavansingerreddy.note.utils.DTOConversionUtil;

@Service
public class NoteServiceImplementation implements NoteService {

    @Autowired
    NoteRepository noteRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public NoteDto createNewNote(NoteModel noteModel,String userEmail) throws UserNotFoundException {

        Note note = new Note();
        BeanUtils.copyProperties(noteModel, note);
        note.setCreatedAt(Date.from(Instant.now()));
        note.setUpdatedAt(Date.from(Instant.now()));

        Optional<User> user= userRepository.findByEmail(userEmail);
        
        if(user.isPresent()){
            note.setUser(user.get());
            noteRepository.save(note);
            return DTOConversionUtil.noteToNoteDTO(note);
        }

        throw new UserNotFoundException("The user does not exists to create a note for that user");

    }

    @Override
    public NoteDto getASpecificNote(String userEmail, Long noteId) throws NoteDoesNotExistsException {
        Optional<Note> optionalNote = noteRepository.findById(noteId);
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);

        if(optionalNote.isPresent() && optionalUser.isPresent()){

            Note note = optionalNote.get();
            User user = optionalUser.get();
            if(note.getUser().getUserId() == user.getUserId()){
               return DTOConversionUtil.noteToNoteDTO(note);
            }
        }

        throw new NoteDoesNotExistsException("Note does not exists for the user and NoteId you have provided");

    }

    @Override
    public List<NoteDto> getAllNotes(String userEmail) throws NoteDoesNotExistsException {

        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            List<Note> notes = user.getNotes();
            if(notes !=null){
                List<NoteDto> noteDtos = new ArrayList<>();
                notes.stream()
                    .map(DTOConversionUtil::noteToNoteDTO)
                    .forEach(noteDtos::add);
                
                return noteDtos;
            }
        }

        throw new NoteDoesNotExistsException("Note does not exists for the user and NoteId you have provided");
    }

    @Override
    public NoteDto updateSpecificNote(String userEmail, Long noteId, NoteModel noteModel) throws NoteDoesNotExistsException {

        Optional<Note> optionalNote = noteRepository.findById(noteId);
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);

        if(optionalNote.isPresent() && optionalUser.isPresent()){
            Note note = optionalNote.get();
            User user = optionalUser.get();
            if(note.getUser().getUserId() == user.getUserId()){
                note = DTOConversionUtil.noteModelToNote(noteModel, note);
                note.setUpdatedAt(Date.from(Instant.now()));
                noteRepository.save(note);
               return DTOConversionUtil.noteToNoteDTO(note);
            }
        }
        throw new NoteDoesNotExistsException("Note does not exists for the user and NoteId you have provided");
    }

    @Override
    public NoteDto deleteASpecificNote(String userEmail, Long noteId) throws NoteDoesNotExistsException {

        Optional<Note> optionalNote = noteRepository.findById(noteId);
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);

         if(optionalNote.isPresent() && optionalUser.isPresent()){
            Note note = optionalNote.get();
            User user = optionalUser.get();
            if(note.getUser().getUserId() == user.getUserId()){
                noteRepository.delete(note);
                return DTOConversionUtil.noteToNoteDTO(note);
            }
         }


         throw new NoteDoesNotExistsException("Note does not exists for the user and NoteId you have provided");

    }

    
}
