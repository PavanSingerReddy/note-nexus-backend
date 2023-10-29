package com.pavansingerreddy.note.entity;



import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "noteId"
)
public class Note {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long noteId;
    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;
  

    @ManyToOne(fetch = FetchType.EAGER,optional = false)
    @JoinColumn(
        name = "user_id",
        referencedColumnName = "userId",
        nullable = false
        )
    private User user;

}
