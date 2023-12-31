package com.pavansingerreddy.note.events.event_publisher;

import org.springframework.context.ApplicationEvent;

import com.pavansingerreddy.note.entity.Users;

import lombok.Getter;
import lombok.Setter;

// Getter annotation from Lombok generates getters for all fields.
@Getter
// Setter annotation from Lombok generates setters for all fields.
@Setter
// passwordResetEvent which is triggered when we want to reset the password of
// the user.This event extends Spring’s ApplicationEvent class. This means it’s
// a type of application event that can be published and listened to within your
// Spring application.
public class PasswordResetEvent extends ApplicationEvent {

    private Users user;
    private String applicationUrl;
    private int mailNoToUseForSendingEmail;

    // constructor which is used to initialize the user and application url when the
    // even get's created and mailNoToUseForSendingEmail contains the mail number
    // from our mail providers to use for sending the email for resetting the password
    public PasswordResetEvent(Users user, String applicationUrl, int mailNoToUseForSendingEmail) {
        // In my custom PasswordResetEvent, I am passing the User object as the source.
        // This means that when handling the event, you could retrieve the User object
        // by calling getSource() on the event. However, since I am also storing the
        // User in a separate field, I may not need to use getSource() in this case.
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
        this.mailNoToUseForSendingEmail = mailNoToUseForSendingEmail;
    }

}
