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

@Entity
@Setter
@Getter
@NoArgsConstructor
public class VerificationToken {


    @Transient
    private Long VerificationTokenExpireTimeInSeconds;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private Date expirationTime;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_USER_VERIFICATION_TOKEN"))
    private User user;

    public VerificationToken(User user,String token,Long verificationTokenExpireTimeInSeconds){
        super();
        this.token = token;
        this.user = user;
        this.VerificationTokenExpireTimeInSeconds=verificationTokenExpireTimeInSeconds;
        System.out.println("=============The verificationTokenExpiryTime is : "+this.VerificationTokenExpireTimeInSeconds+"===================");
        this.expirationTime = calculateExpirationDate(this.VerificationTokenExpireTimeInSeconds);
    }

    private Date calculateExpirationDate(Long expiryTimeInSeconds) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("IST"));
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.SECOND, expiryTimeInSeconds.intValue());
        return new Date(cal.getTime().getTime());
    }
}
