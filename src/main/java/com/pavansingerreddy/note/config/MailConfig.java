package com.pavansingerreddy.note.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

// Configuration annotation indicates that the class can be used by the Spring IoC container as a source of bean definitions.
@Configuration
// MailConfig is the declaration of my configuration class named MailConfig.
public class MailConfig {

    // This injects an instance of Environment which is responsible for providing
    // environment-specific properties.This can be used to access the properties of
    // application.yml file
    @Autowired
    private Environment env;

    // Bean declares a bean of type JavaMailSender with the qualifier “mailSender1”.
    // This method will provide the actual bean when requested.
    @Bean
    // The @Qualifier annotation in Spring is used to resolve ambiguity when
    // multiple beans of the same type are present in a Spring application context

    // If you have more than one bean of the same type and want to wire only one of
    // them, you can use the @Qualifier annotation along with @Autowired to specify
    // which exact bean will be wired.But here we are defining it with qualifier so
    // that the @Autowired annotation can identify it

    @Qualifier("mailSender1")
    // This declares a bean of type JavaMailSender with the qualifier “mailSender1”.
    // This method will provide the actual bean when requested.
    JavaMailSender getJavaMailSender1() {
        // This creates a new instance of JavaMailSenderImpl, which is an implementation
        // of the JavaMailSender interface.
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // These lines set the host and port for the mail server from the properties
        // defined in my environment or applications.yml file.
        mailSender.setHost(env.getProperty("mail.config1.host"));
        mailSender.setPort(Integer.parseInt(env.getProperty("mail.config1.port")));

        // These lines set the username and password for the mail server from the
        // properties defined in my environment or application.yml file.
        mailSender.setUsername(env.getProperty("mail.config1.username"));
        mailSender.setPassword(env.getProperty("mail.config1.password"));

        // These lines get the JavaMailProperties from the mailSender and set some
        // properties. It sets the mail transport protocol to SMTP, enables SMTP
        // authentication, and enables the STARTTLS command to switch the connection to
        // a TLS-protected connection before issuing any login commands.
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // This returns the configured mailSender bean.
        return mailSender;
    }

    // The getJavaMailSender2 method does the same thing as getJavaMailSender1, but
    // it uses different properties (i.e., “mail.config2.host”, “mail.config2.port”,
    // etc.).
    @Bean
    @Qualifier("mailSender2")
    JavaMailSender getJavaMailSender2() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(env.getProperty("mail.config2.host"));
        mailSender.setPort(Integer.parseInt(env.getProperty("mail.config2.port")));

        mailSender.setUsername(env.getProperty("mail.config2.username"));
        mailSender.setPassword(env.getProperty("mail.config2.password"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }

    // add more beans for additional mail configurations
}

// Note : when you add a 3rd new additional mail service provider in the
// application.yml file then update this file and add another JavaMailSender
// bean with @Bean annotation and @Qualifier("mailSender3") qualifier which
// reflects the new 3rd email with it's configuration and also update
// EmailService file and DummyUserDetailsCreator files to reflect the new mail
// changes