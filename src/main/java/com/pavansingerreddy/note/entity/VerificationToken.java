package com.pavansingerreddy.note.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Entity annotation tells Hibernate to make a table out of this class.
@Entity
// Setter annotation from Lombok generates setters for all fields.
@Setter
// Getter annotation from Lombok generates getters for all fields.
@Getter
// NoArgsConstructor annotation from Lombok generates a no-args constructor.
@NoArgsConstructor
// This VerificationToken class is used to store the verification token's which
// is used to verify the user
public class VerificationToken {

    // Transient annotation is used to indicate that a field is not to be persisted
    // in the database.
    @Transient
    // VerificationTokenExpireTimeInSeconds field holds the expiration time in
    // seconds for the verification token.
    private Long VerificationTokenExpireTimeInSeconds;

    // Id annotation specifies the primary key of an entity.
    @Id
    // GeneratedValue annotation provides for the specification of generation
    // strategies for the values of primary keys.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // id field holds the ID of the verification token.
    private Long id;

    // token field holds the token.
    private String token;

    // expirationTime field holds the expiration time of the token.
    private Date expirationTime;

    // OneToOne annotation defines a one-to-one relationship between the
    // VerificationToken and User entities. FetchType.EAGER means that the related
    // entity will be fetched immediately.
    @OneToOne(fetch = FetchType.EAGER)
    // JoinColumn annotation is used to specify a column for joining an entity
    // association or element collection. Here, it's specifying that the "user_id"
    // column in the VerificationToken table is joined with the User table. The
    // foreign key is named "FK_USER_VERIFICATION_TOKEN".
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_USER_VERIFICATION_TOKEN"))
    // user field holds the user associated with the verification token.
    private User user;

    // This constructor takes a user, a token, and an expiration time in seconds as
    // parameters.and calculates the expiration time and assigns it to the
    // VerificationTokenExpireTimeInSeconds attribute of this Table
    public VerificationToken(User user, String token, Long verificationTokenExpireTimeInSeconds) {
        // Set the token.
        this.token = token;
        // Set the user.
        this.user = user;
        // Set the expiration time in seconds.
        this.VerificationTokenExpireTimeInSeconds = verificationTokenExpireTimeInSeconds;
        // Calculate the expiration date and set it.
        this.expirationTime = calculateExpirationDate(this.VerificationTokenExpireTimeInSeconds);
    }

    // This method calculates the expiration date based on the expiry time in
    // seconds.
    private Date calculateExpirationDate(Long expiryTimeInSeconds) {
        // Get a Calendar instance for the Indian Standard Time timezone.
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("IST"));
        // Set the Calendar's time to the current time.
        cal.setTimeInMillis(new Date().getTime());
        // Add the expiry time in seconds to the Calendar's time.
        cal.add(Calendar.SECOND, expiryTimeInSeconds.intValue());
        // Return a new Date object with the Calendar's time.
        return new Date(cal.getTime().getTime());
    }
}
