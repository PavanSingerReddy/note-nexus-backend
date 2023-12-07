package com.pavansingerreddy.note.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Entity annotation tells Hibernate to make a table out of this class.
@Entity
// Getter annotation from Lombok generates getters for all fields.
@Getter
// Setter annotation from Lombok generates setters for all fields.
@Setter
// NoArgsConstructor annotation from Lombok generates a no-args constructor.
@NoArgsConstructor
// AllArgsConstructor annotation from Lombok generates a constructor with one
// parameter for each field in your class. Fields are initialized in the order
// they are declared.
@AllArgsConstructor
@JsonIdentityInfo(
                // This annotation is used to handle serialization and deserialization of
                // related entities. It helps in handling infinite recursion problems by
                // referring to the object id during serialization.
                generator = ObjectIdGenerators.PropertyGenerator.class,
                // This specifies the property of the entity that will be used as the ID.
                property = "noteId")
public class Note {
        // This annotation specifies the primary key of an entity.
        @Id
        // This annotation provides for the specification of generation strategies for
        // the values of primary keys.
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        // This field holds the note ID.
        private long noteId;
        // This annotation is used to specify the mapped column for a persistent
        // property or field.
        @Column(columnDefinition = "TEXT")
        // This field holds the title of the note.
        private String title;
        // This annotation specifies that a persistent property or field should be
        // persisted as a large object to a database-supported large object type.
        @Lob
        // This annotation is used to specify the mapped column for a persistent
        // property or field.
        @Column(columnDefinition = "LONGTEXT")
        // This field holds the content of the note.
        private String content;
        // This field holds the date when the note was created.
        private Date createdAt;
        // This field holds the date when the note was last updated.
        private Date updatedAt;
        // This annotation defines a many-to-one relationship between the Note and User
        // entities. FetchType.EAGER means that the related entity will be fetched
        // immediately. optional = false means that a non-null relationship must always
        // exist.
        @ManyToOne(fetch = FetchType.EAGER, optional = false)
        @JoinColumn(
                        // This specifies the name of the column for joining an entity association or
                        // element collection.
                        name = "user_id",
                        // This specifies the name of the column in the referenced table that will be
                        // used to join.
                        referencedColumnName = "userId",
                        // This specifies that the column cannot have null values.
                        nullable = false)
        // This field holds the user associated with the note.
        private User user;

}

// Serialization in JSON involves converting Java objects into JSON
// representations. JSON (JavaScript Object Notation) is a lightweight
// data-interchange format that is widely used in web applications and APIs.
// When serializing a Java object to JSON, the object's properties are mapped to
// JSON key-value pairs

// Deserialization in JSON involves converting JSON strings back into Java
// objects.

// The @JsonIdentityInfo annotation is used to handle serialization and
// deserialization of related entities. It helps in handling infinite recursion
// problems by referring to the object id during serialization.

// Let’s consider an example where you have two entities, User and Note, and
// they have a bi-directional relationship. A User can have multiple Notes, and
// each Note is associated with a User.

// Without @JsonIdentityInfo, if you try to serialize a User to JSON, it will
// include all the Notes associated with that User. But each Note also includes
// the User it’s associated with, which in turn includes all the Notes, and so
// on. This leads to an infinite loop, also known as infinite recursion.

// To solve this problem, you can use the @JsonIdentityInfo annotation. This
// tells Jackson to serialize the related entity by its ID, instead of
// serializing the entire entity. This breaks the infinite loop.

// In my above code, @JsonIdentityInfo(generator =
// ObjectIdGenerators.PropertyGenerator.class, property = "noteId") is telling
// Jackson to use the noteId property when the recursion happens means while
// parsing from java object to json by the jackson(It is a popular library used
// for serializing Java objects to JSON and vice versa.It is included in spring
// by default) parser then if the noteId occurs for the first time then it
// parses the entire object and when the recursion happens and the 2nd time also
// we get noteId while parsing we only parse the noteId and not the entire Note
// object
