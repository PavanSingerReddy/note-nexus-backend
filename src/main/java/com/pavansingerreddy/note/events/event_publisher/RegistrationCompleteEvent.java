package com.pavansingerreddy.note.events.event_publisher;

import org.springframework.context.ApplicationEvent;

import com.pavansingerreddy.note.entity.Users;

import lombok.Getter;
import lombok.Setter;

// Getter annotation from Lombok generates getters for all fields.
@Getter
// Setter annotation from Lombok generates setters for all fields.
@Setter
// This is an event which we will send after the user successfully creates a
// new user by sending the request to /register endpoint.after creating the new
// user successfully we trigger or publish this event
public class RegistrationCompleteEvent extends ApplicationEvent {

    private Users user;
    private String applicationUrl;
    private int mailNoToUseForSendingEmail;

    // application url contains the root path or url of the frontend application and
    // User object contains the details of the registered new user and
    // mailNoToUseForSendingEmail contains the mail number from our
    // mail providers to use for sending the email for verification
    public RegistrationCompleteEvent(Users user, String applicationUrl, int mailNoToUseForSendingEmail) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
        this.mailNoToUseForSendingEmail = mailNoToUseForSendingEmail;
    }

}
