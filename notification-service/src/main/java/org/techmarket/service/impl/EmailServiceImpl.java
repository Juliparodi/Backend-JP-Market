package org.techmarket.service.impl;

import com.netflix.discovery.converters.Auto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.techmarket.service.IEmailService;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String userMail;

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void sendMessage(String to, String subject, String text) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(userMail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText("text");
            emailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
