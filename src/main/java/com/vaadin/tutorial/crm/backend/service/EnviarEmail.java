package com.vaadin.tutorial.crm.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EnviarEmail{

    private static final Logger LOGGER = LoggerFactory.getLogger(EnviarEmail.class);

    private JavaMailSender sender;

    public EnviarEmail(JavaMailSender sender)
    {
        this.sender=sender;
    }



    public boolean sendEmailTool(String textMessage, String email,String subject) {
        boolean send = false;
        MimeMessage message = this.sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setTo(email);
            helper.setText(textMessage, true);
            helper.setSubject(subject);
            sender.send(message);
            send = true;
            LOGGER.info("Mail enviado!");
        } catch (MessagingException e) {
            LOGGER.error("Hubo un error al enviar el mail: {}", e);
        }
        return send;
    }
}