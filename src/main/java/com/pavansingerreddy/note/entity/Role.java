package com.pavansingerreddy.note.entity;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Entity annotation tells Hibernate to make a table out of this class.
@Entity
// NoArgsConstructor annotation from Lombok generates a no-args constructor.
@NoArgsConstructor
// AllArgsConstructor annotation from Lombok generates a constructor with one
// parameter for each field in your class. Fields are initialized in the order
// they are declared.
@AllArgsConstructor
// Getter annotation from Lombok generates getters for all fields.
@Getter
// Setter annotation from Lombok generates setters for all fields.
@Setter
@JsonIdentityInfo(
        // This annotation is used to handle serialization and deserialization of
        // related entities. It helps in handling infinite recursion problems by
        // referring to the object id during serialization.
        generator = ObjectIdGenerators.PropertyGenerator.class,
        // This specifies the property of the entity that will be used as the ID.
        property = "roleId")

// This class implements the GrantedAuthority interface, which means it can be
// used to represent an authority granted to The User.
public class Role implements GrantedAuthority {

    // Id annotation specifies the primary key of an entity.
    @Id
    // GeneratedValue annotation provides for the specification of generation
    // strategies for the values of primary keys.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // roleId field holds the role ID.
    private long roleId;
    // name field holds the name of the role.
    private String name;

    // This constructor takes an authority (a role name) as a parameter.
    public Role(String authority) {
        // Set the name of the role to the authority.
        this.name = authority;
    }

    // This annotation indicates that this method overrides a method of the
    // superclass or interface.
    @Override
    // This method returns the authority (the role name).
    public String getAuthority() {
        // Return the name of the role.
        return this.name;
    }

}
