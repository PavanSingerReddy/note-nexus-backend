package com.pavansingerreddy.note.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pavansingerreddy.note.dto.NoteDto;
import com.pavansingerreddy.note.dto.PagableNoteDto;
import com.pavansingerreddy.note.entity.Note;
import com.pavansingerreddy.note.entity.User;
import com.pavansingerreddy.note.exception.NoteDoesNotExistsException;
import com.pavansingerreddy.note.exception.UserNotFoundException;
import com.pavansingerreddy.note.model.NoteModel;
import com.pavansingerreddy.note.repository.NoteRepository;
import com.pavansingerreddy.note.repository.UserRepository;
import com.pavansingerreddy.note.utils.DTOConversionUtil;

@Service
@Transactional
public class NoteServiceImplementation implements NoteService {

    @Autowired
    NoteRepository noteRepository;

    @Autowired
    UserRepository userRepository;

    @Override

    public NoteDto createNewNote(NoteModel noteModel, String userEmail) throws UserNotFoundException {

        Note note = new Note();

        BeanUtils.copyProperties(noteModel, note);

        note.setCreatedAt(Date.from(Instant.now()));

        note.setUpdatedAt(Date.from(Instant.now()));

        Optional<User> user = userRepository.findByEmail(userEmail);

        if (user.isPresent()) {
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

        if (optionalNote.isPresent() && optionalUser.isPresent()) {

            Note note = optionalNote.get();
            User user = optionalUser.get();

            if (note.getUser().getUserId() == user.getUserId()) {

                return DTOConversionUtil.noteToNoteDTO(note);
            }
        }

        throw new NoteDoesNotExistsException("Note does not exists for the user and NoteId you have provided");

    }

    @Override

    public List<NoteDto> getAllNotes(String userEmail) throws NoteDoesNotExistsException {

        Optional<User> optionalUser = userRepository.findByEmail(userEmail);

        if (optionalUser.isPresent()) {

            User user = optionalUser.get();

            List<Note> notes = user.getNotes();

            if (notes != null) {

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

    public NoteDto updateSpecificNote(String userEmail, Long noteId, NoteModel noteModel)
            throws NoteDoesNotExistsException {

        Optional<Note> optionalNote = noteRepository.findById(noteId);

        Optional<User> optionalUser = userRepository.findByEmail(userEmail);

        if (optionalNote.isPresent() && optionalUser.isPresent()) {
            Note note = optionalNote.get();
            User user = optionalUser.get();

            if (note.getUser().getUserId() == user.getUserId()) {

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

        if (optionalNote.isPresent() && optionalUser.isPresent()) {
            Note note = optionalNote.get();
            User user = optionalUser.get();

            if (note.getUser().getUserId() == user.getUserId()) {

                noteRepository.delete(note);
                return DTOConversionUtil.noteToNoteDTO(note);
            }
        }

        throw new NoteDoesNotExistsException("Note does not exists for the user and NoteId you have provided");

    }

    @Override

    public List<NoteDto> searchNotes(String userEmail, String searchTerm) throws NoteDoesNotExistsException {

        Optional<User> optionalUser = userRepository.findByEmail(userEmail);

        if (optionalUser.isPresent()) {

            User user = optionalUser.get();

            Long userId = user.getUserId();

            List<Note> notes = noteRepository.search(userId, searchTerm);

            if (notes != null) {

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

    public List<PagableNoteDto> getPagedNotes(String userEmail, int page, int size) throws NoteDoesNotExistsException {

        Optional<User> optionalUser = userRepository.findByEmail(userEmail);

        if (optionalUser.isPresent()) {

            User user = optionalUser.get();

            Long userId = user.getUserId();

            Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());

            Page<Note> pagedNotes = noteRepository.findByUser_UserId(userId, pageable);

            List<Note> notes = pagedNotes.getContent();

            if (notes != null) {

                long totalPages = pagedNotes.getTotalPages();

                List<PagableNoteDto> pagableNoteDtos = new ArrayList<>();

                notes.stream()
                        .map(note -> DTOConversionUtil.noteToPagableNoteDto(note, totalPages))
                        .forEach(pagableNoteDtos::add);
                return pagableNoteDtos;
            }
        }

        throw new NoteDoesNotExistsException("Note does not exists for the user and NoteId you have provided");

    }

}
