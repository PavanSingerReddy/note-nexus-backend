package com.pavansingerreddy.note.events.event_listener;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pavansingerreddy.note.entity.User;
import com.pavansingerreddy.note.events.event_publisher.RegistrationCompleteEvent;
import com.pavansingerreddy.note.exception.UserNotFoundException;
import com.pavansingerreddy.note.services.EmailService;
import com.pavansingerreddy.note.services.UserService;

@Component

public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Async

    @Transactional
    @Override

    public void onApplicationEvent(RegistrationCompleteEvent event) {

        try {

            User user = userService.getUserDetailsByEmail(event.getUser().getEmail());

            String token = UUID.randomUUID().toString();

            userService.saveVerificationTokenForUser(token, user);

            String url = event.getApplicationUrl() + "/verifyRegistration?token=" + token;

            String messageBody = "click the link to verify your account : " + url;

            String messageSubject = "Account verification email";

            emailService.sendEmail(user.getEmail(), messageSubject, messageBody, event.getMailNoToUseForSendingEmail());
        } catch (UserNotFoundException e) {

            e.printStackTrace();
        }
    }

}
