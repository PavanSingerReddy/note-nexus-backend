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

// making it as component so that spring can recognise it as the component and create a bean for it
@Component
public class PasswordResetEventListener implements ApplicationListener<PasswordResetEvent> {

    // getting user service so that we can perform the operations related to the user
    @Autowired
    private UserService userService;

    // getting the email service object so that we can send email to the user for resetting the password
    @Autowired
    private EmailService emailService;

    // making the onApplicationEvent function Async so that it doesn't interfere with the main thread and it operates on the different thread from the main api thread on which the request and response is happening so that application feels snappy
    @Async
    // overriding the onApplicationEvent which get's executed when an event get's triggered or published
    @Override
    public void onApplicationEvent(PasswordResetEvent event) {
        // creating the password reset token for the user


        // getting the user object of the user who triggered this event
        User user = event.getUser();
        // generating a random uuid which is stored in my database and also sent to the user's email in a query parameter url so that user can verify and reset the password
        String token = UUID.randomUUID().toString();
        // saves the random UUID token in the database with the user details like user id attached to that token
        userService.savePasswordResetToken(token,user);

        // url to which the user have to call to reset the password.this url contains the application url(url on which user endpoints exists) with the /verifyResetPassword endpoint for verifying our token and the token itself as a query parameter
        String url = event.getApplicationUrl()+"/verifyResetPassword?token="+token;

        // sending the url to the email to reset the password

        // Message body which is the body of the email which we send to the user
        String messageBody = "click The link to reset your account password : "+url;
        // Message subject is the title of the email which we send
        String messageSubject = "Password Reset Email";
        //using the emailService.sendEmail() custom method to send the email to the user by using the user's email with the message subject and message body
        emailService.sendEmail(user.getEmail(), messageSubject, messageBody);

    }
    
}
