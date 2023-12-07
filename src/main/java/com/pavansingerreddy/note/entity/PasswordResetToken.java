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
import lombok.Data;
import lombok.NoArgsConstructor;

// Entity annotation tells Hibernate to make a table out of this class.
@Entity
// Data annotation from Lombok generates getters, setters, equals, hashCode and
// toString methods.
@Data
// NoArgsConstructor annotation from Lombok generates a no-args constructor.
@NoArgsConstructor
public class PasswordResetToken {
    // Transient annotation is used to indicate that a field is not to be persisted
    // in the database.
    @Transient
    // This field holds the expiration time in seconds for the password reset token.
    private Long PasswordResetTokenExpireTimeInSeconds;

    // Id annotation specifies the primary key of an entity.
    @Id
    // This annotation provides for the specification of generation strategies for
    // the values of primary keys.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // This field holds the ID of the password reset token.
    private Long id;
    // This field holds the token.
    private String token;
    // This field holds the expiration time of the token.
    private Date expirationTime;
    // This annotation defines a one-to-one relationship between the
    // PasswordResetToken and User entities. FetchType.EAGER means that the related
    // entity will be fetched immediately.
    @OneToOne(fetch = FetchType.EAGER)
    // This annotation is used to specify a column for joining an entity association
    // or element collection. Here, it's specifying that the "user_id" column in the
    // PasswordResetToken table is joined with the User table. The foreign key is
    // named "FK_USER_PASSWORD_RESET_TOKEN".Here we did not gave
    // referencedColumnName so it takes the primary key of the User entity which is
    // defined with the Id annotation as it's foreign key
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_USER_PASSWORD_RESET_TOKEN"))
    // This field holds the user associated with the password reset token.
    private User user;

    // constructor for password reset token which takes User object and a UUID token
    // string and a password expiry time in seconds
    public PasswordResetToken(User user, String token, Long PasswordResetTokenExpireTimeInSeconds) {
        // Set the token.
        this.token = token;
        // Set the user.
        this.user = user;
        // Set the expiration time in seconds.
        this.PasswordResetTokenExpireTimeInSeconds = PasswordResetTokenExpireTimeInSeconds;
        // Calculate the expiration date and set it.
        this.expirationTime = calculateExpirationDate(this.PasswordResetTokenExpireTimeInSeconds);
    }

    // calculating the expiration time and date of the token

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
