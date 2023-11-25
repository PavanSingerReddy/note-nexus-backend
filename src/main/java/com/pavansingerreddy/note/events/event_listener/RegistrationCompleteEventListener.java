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
    // adding transactional annotation so that all the transactions in this event happen in a single hibernate session and not on different hibernate session which may cause error
    // In the context of Hibernate, a transaction is associated with a Session. The @Transactional annotation ensures that all the operations in the method occur within the same Hibernate Session. This can help avoid issues with detached entities, which can occur when an entity is fetched in one Session and then used in another.
    // So, when we annotate the onApplicationEvent method with @Transactional, we are ensuring that the fetching of the User and the saving of the VerificationToken both happen within the same Hibernate Session. This can help avoid the detached entity passed to persist error.
    @Transactional
    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // creating the verification token for the user
        try {
            // here we are fetching the user again because the hiberate session is discontinued in this event as we are using async annotation which creates a seperate thread so there will be new hiberate session so even though we have the previous user object the hiberanate session is discontinued and we get the error as "detached entity passed to persist" this is the reason we are fetching the user details again in this new hibernate session
            User user = userService.getUserDetailsByEmail(event.getUser().getEmail());
            String token = UUID.randomUUID().toString();
            userService.saveVerificationTokenForUser(token, user);
    
            String url = event.getApplicationUrl() + "/verifyRegistration?token=" + token;
    
            // send mail to the users
            String messageBody = "click the link to verify your account : " + url;
            String messageSubject = "Account verification email";
            emailService.sendEmail(user.getEmail(), messageSubject, messageBody);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
    }

}
