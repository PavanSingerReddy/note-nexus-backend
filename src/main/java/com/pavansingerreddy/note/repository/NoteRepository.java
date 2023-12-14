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

// @Repository is a Spring annotation that marks this interface as a Repository.  Spring will automatically create an implementation of this interface. The @Repository annotation is a way to tell Spring Data JPA to handle all the database operations for a certain type of data (like Note), so you can focus on writing the rest of your application
@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    // This method declaration is for finding a Note by its ID. It returns an
    // Optional that contains the Note if found, or is empty if not found.
    Optional<Note> findById(Long noteId);

    // This method declaration is for finding a page of Note entities associated
    // with a specific user ID. The Pageable parameter specifies the details of the
    // page request, such as the page number and size.
    Page<Note> findByUser_UserId(Long userId, Pageable pageable);

    @Query("SELECT n FROM Note n WHERE n.user.userId = :userId AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :term, '%')) OR LOWER(n.content) LIKE LOWER(CONCAT('%', :term, '%')))")
    // This method declaration is for searching for Note entities associated with a
    // specific user ID and a search term. The @Query annotation specifies the JPQL
    // query to be executed. The @Param annotations are used to bind the method
    // parameters to the query parameters. List<Note> search(@Param("userId") Long
    // userId, @Param("term") String term);
    List<Note> search(@Param("userId") Long userId, @Param("term") String term);
}

// NOTE: here is a break down of the above query : @Query("SELECT n FROM Note n
// WHERE n.user.userId = :userId AND (LOWER(n.title) LIKE LOWER(CONCAT('%',
// :term, '%')) OR LOWER(n.content) LIKE LOWER(CONCAT('%', :term, '%')))")

// explanation of the query :

// SELECT n FROM Note n: This part of the query is selecting all the Note
// entities and referring to them as n in the rest of the query.

// WHERE n.user.userId = :userId: This is a condition that filters the notes
// based on their associated user. It only selects the notes where the userId
// field of the user field of the Note entity matches the userId parameter
// provided in the method call.

// LOWER(n.title) LIKE LOWER(CONCAT('%', :term, '%')): This condition checks if
// the title of the note contains the search :term. The LOWER function is used
// to make the search case-insensitive. The CONCAT function is used to add ‘%’
// before and after the search term, which allows it to match any part of the
// title.

// LOWER(n.content) LIKE LOWER(CONCAT('%', :term, '%')): This condition is
// similar to the previous one, but it checks the content of the note instead of
// the title.

// AND: This is a logical operator that requires both conditions on its sides to
// be true. So a Note entity will only be selected if both the userId matches
// and the title or content contains the search term.

// So, in summary, this query selects all notes from a specific user where the
// title or content of the note contains a certain search term. The search is
// case-insensitive.