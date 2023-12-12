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

@Entity

@Data

@NoArgsConstructor
public class PasswordResetToken {

    @Transient

    private Long PasswordResetTokenExpireTimeInSeconds;

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private String token;

    private Date expirationTime;

    @OneToOne(fetch = FetchType.EAGER)

    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_USER_PASSWORD_RESET_TOKEN"))

    private User user;

    public PasswordResetToken(User user, String token, Long PasswordResetTokenExpireTimeInSeconds) {

        this.token = token;

        this.user = user;

        this.PasswordResetTokenExpireTimeInSeconds = PasswordResetTokenExpireTimeInSeconds;

        this.expirationTime = calculateExpirationDate(this.PasswordResetTokenExpireTimeInSeconds);
    }

    private Date calculateExpirationDate(Long expiryTimeInSeconds) {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("IST"));

        cal.setTimeInMillis(new Date().getTime());

        cal.add(Calendar.SECOND, expiryTimeInSeconds.intValue());

        return new Date(cal.getTime().getTime());
    }
}
