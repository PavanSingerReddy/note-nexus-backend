package com.pavansingerreddy.note.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

// @Service is a Spring annotation that marks this class as a service in the Spring application context.
// This means that Spring will automatically create an instance of this class and manage it.
@Service
// EmailService is a service class which is used for sending Emails
public class EmailService {

    // @Autowired is a Spring annotation for automatic dependency injection.
    // Here, it's injecting an instance of JavaMailSender, which is a Spring
    // interface for sending emails.
    @Autowired
    // If you have more than one bean of the same type and want to wire only one of
    // them, you can use the @Qualifier annotation along with @Autowired to specify
    // which exact bean will be wired.Here we get the bean with name "mailSender1"
    // from the MailConfig.java file
    @Qualifier("mailSender1")
    private JavaMailSender mailSender1;

    @Autowired
    // If you have more than one bean of the same type and want to wire only one of
    // them, you can use the @Qualifier annotation along with @Autowired to specify
    // which exact bean will be wired.Here we get the bean with name "mailSender2"
    // from the MailConfig.java file
    @Qualifier("mailSender2")
    private JavaMailSender mailSender2;

    // getting the username or email address of the mailSender1 object from the
    // application.yml file
    @Value("${mail.config1.username}")
    private String fromEmail1;

    // getting the username or email address of the mailSender2 object from the
    // application.yml file
    @Value("${mail.config2.username}")
    private String fromEmail2;

    // This method is used to send an email. It takes the recipient's email address,
    // email subject, and email body as parameters.
    public boolean sendEmail(String toEmail, String subject, String body, int mailNoToUseForSendingEmail) {

        // Creating a new instance of SimpleMailMessage which is a helper class for
        // creating a JavaMail MimeMessage.
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        // Setting the recipient's email address.
        mailMessage.setTo(toEmail);
        // Setting the body of the email.
        mailMessage.setText(body);
        // Setting the subject of the email.
        mailMessage.setSubject(subject);

        switch (mailNoToUseForSendingEmail) {
            case 1:
                // Sending the email using the first JavaMailSender instance.
                // If the email is sent successfully, the method returns true.
                // If there's an exception (email not sent), then we catch the exception and go
                // to the next JavaMailSender instance and send the mail if every instance fails
                // to send the email then we return false.
                try {
                    mailMessage.setFrom(fromEmail1);
                    mailSender1.send(mailMessage);
                    return true;
                } catch (Exception e) {
                    System.out.println("Failed to send email to " + toEmail + " using mail configuration 1");
                    System.out.println(e);
                }
            case 2:
                try {
                    mailMessage.setFrom(fromEmail2);
                    mailSender2.send(mailMessage);
                    return true;
                } catch (Exception e) {
                    System.out.println("Failed to send email to " + toEmail + " using mail configuration 2");
                    System.out.println(e);
                }
            default:
                return false;
        }
    }

}

// Note : when you add a 3rd new additional mail service provider in the
// application.yml file then update this file and add add another case 3 in the
// switch case statement which reflects the 3rd new mail configuration and also
// update MailConfig file and DummyUserDetailsCreator files to reflect the new
// mail changes