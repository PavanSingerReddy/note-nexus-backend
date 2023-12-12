package com.pavansingerreddy.note.events.event_publisher;

import org.springframework.context.ApplicationEvent;

import com.pavansingerreddy.note.entity.User;

import lombok.Getter;
import lombok.Setter;

@Getter

@Setter

public class RegistrationCompleteEvent extends ApplicationEvent {

    private User user;
    private String applicationUrl;
    private int mailNoToUseForSendingEmail;

    public RegistrationCompleteEvent(User user, String applicationUrl, int mailNoToUseForSendingEmail) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
        this.mailNoToUseForSendingEmail = mailNoToUseForSendingEmail;
    }

}
