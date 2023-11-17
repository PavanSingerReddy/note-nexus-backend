package com.pavansingerreddy.note.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pavansingerreddy.note.entity.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    Optional<Note> findById(Long noteId);

    Page<Note> findByUser_UserId(Long userId, Pageable pageable);

    @Query("SELECT n FROM Note n WHERE n.user.userId = :userId AND (LOWER(CAST(n.title AS string)) LIKE LOWER(CONCAT('%', :term, '%')) OR LOWER(CAST(n.content AS string)) LIKE LOWER(CONCAT('%', :term, '%')))")
    List<Note> search(@Param("userId") Long userId, @Param("term") String term);
    

}
