package com.gmail.artsiombaranau.thetutor.services;

import com.gmail.artsiombaranau.thetutor.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendMail(User userTo) throws MailException, MessagingException {
        Context context = new Context();
        context.setVariable("user", userTo);

        String template = templateEngine.process("email", context);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        helper.setTo(userTo.getEmail());
        helper.setSubject("Welcome, " + userTo.getFirstName() + "!");
        helper.setText(template, true);

        javaMailSender.send(mimeMessage);
    }
}
