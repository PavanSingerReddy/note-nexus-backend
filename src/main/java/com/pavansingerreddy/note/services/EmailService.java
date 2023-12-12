package com.pavansingerreddy.note.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service

public class EmailService {

    @Autowired

    @Qualifier("mailSender1")
    private JavaMailSender mailSender1;

    @Autowired

    @Qualifier("mailSender2")
    private JavaMailSender mailSender2;

    @Value("${mail.config1.username}")
    private String fromEmail1;

    @Value("${mail.config2.username}")
    private String fromEmail2;

    public boolean sendEmail(String toEmail, String subject, String body, int mailNoToUseForSendingEmail) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(toEmail);

        mailMessage.setText(body);

        mailMessage.setSubject(subject);

        switch (mailNoToUseForSendingEmail) {
            case 1:

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
