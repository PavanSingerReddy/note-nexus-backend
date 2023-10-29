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

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "roleId"
)
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long roleId;
    private String name;

    // @ManyToMany(mappedBy = "roles",fetch = FetchType.EAGER)
    // private Set<User> users;
    
    public Role(String authority) {
        this.name = authority;
    }
    @Override
    public String getAuthority() {
        return this.name;
    }

}
