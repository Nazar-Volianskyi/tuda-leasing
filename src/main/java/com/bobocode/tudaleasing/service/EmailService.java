package com.bobocode.tudaleasing.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendFeedbackEmail(String name, String email, String topic, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        // ВНИМАНИЕ: Замени на свой реальный email, куда ты хочешь получать письма
        mailMessage.setTo("igorexa200574@gmail.com");

        mailMessage.setReplyTo(email);
        mailMessage.setSubject("New mes: " + topic);
        mailMessage.setText("Senders name: " + name + "\n" +
                "Contact email: " + email + "\n\n" +
                "Mass:\n" + message);

        mailSender.send(mailMessage);
    }
}