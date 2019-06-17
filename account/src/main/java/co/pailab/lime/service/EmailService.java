package co.pailab.lime.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;

import co.pailab.lime.helper.AmazonSES;
import org.springframework.stereotype.Service;

@Service("emailService")
public class EmailService {

    private JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendEmail(SimpleMailMessage email) {
        mailSender.send(email);
    }

    public void sendSesEmailWithCc (String from, String fromName, String to, String subject, String body, String cc) throws Exception {
        AmazonSES.sendWithCc(from, fromName, to, subject, body, cc);
    }
    public void sendSesEmail(String from, String fromName, String to, String subject, String body) throws Exception {
        AmazonSES.send(from, fromName, to, subject, body);
    }
}