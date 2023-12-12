package com.pavansingerreddy.note.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration

public class MailConfig {

    @Autowired
    private Environment env;

    @Bean

    @Qualifier("mailSender1")

    public JavaMailSender getJavaMailSender1() {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(env.getProperty("mail.config1.host"));
        mailSender.setPort(Integer.parseInt(env.getProperty("mail.config1.port")));

        mailSender.setUsername(env.getProperty("mail.config1.username"));
        mailSender.setPassword(env.getProperty("mail.config1.password"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }

    @Bean
    @Qualifier("mailSender2")
    public JavaMailSender getJavaMailSender2() {
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

}
