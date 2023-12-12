package com.pavansingerreddy.note.entity;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity

@Getter

@Setter

@NoArgsConstructor

@AllArgsConstructor

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userId")

@ToString

public class User implements UserDetails {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private long userId;

    private String username;

    private String password;

    @Column(unique = true)

    private String email;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)

    @JoinTable(name = "User_Role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))

    private Set<Role> roles;

    private Date userCreatedAtTime;

    @Column(name = "new_user_can_be_created_at_time")
    private Date newUserCanBeCreatedAtTime;

    private int mailNoToUseForSendingEmail;

    private boolean enabled = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)

    private VerificationToken verificationToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)

    private List<Note> notes;

    @Override

    public Collection<? extends GrantedAuthority> getAuthorities() {

        return this.roles;
    }

    @Override

    public boolean isAccountNonExpired() {

        return true;
    }

    @Override

    public boolean isAccountNonLocked() {

        return true;
    }

    @Override

    public boolean isCredentialsNonExpired() {

        return true;
    }

    @Override

    public boolean isEnabled() {

        return this.enabled;
    }

    public void updateUserCreatedAtTimeAndNewUserCanBeCreatedAtTime() {

        this.userCreatedAtTime = Date.from(Instant.now());

        int randomMinutes = ThreadLocalRandom.current().nextInt(5, 11);

        int randomSeconds = ThreadLocalRandom.current().nextInt(0, 60);

        Duration minutesToAdd = Duration.ofMinutes(randomMinutes);

        this.newUserCanBeCreatedAtTime = Date.from(Instant.now().plus(minutesToAdd).plusSeconds(randomSeconds));
    }

}
