package com.pavansingerreddy.note.events.event_listener;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.pavansingerreddy.note.entity.User;
import com.pavansingerreddy.note.events.event_publisher.PasswordResetEvent;
import com.pavansingerreddy.note.services.EmailService;
import com.pavansingerreddy.note.services.UserService;

@Component

public class PasswordResetEventListener implements ApplicationListener<PasswordResetEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Async

    @Override
    public void onApplicationEvent(PasswordResetEvent event) {

        User user = event.getUser();

        String token = UUID.randomUUID().toString();

        userService.savePasswordResetToken(token, user);

        String url = event.getApplicationUrl() + "/verifyResetPassword?token=" + token;

        String messageBody = "click The link to reset your account password : " + url;

        String messageSubject = "Password Reset Email";

        emailService.sendEmail(user.getEmail(), messageSubject, messageBody, event.getMailNoToUseForSendingEmail());

    }

}
