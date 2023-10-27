package com.pavansingerreddy.note.entity;


import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
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
