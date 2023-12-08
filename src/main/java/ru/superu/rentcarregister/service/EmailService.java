package ru.superu.rentcarregister.service;

import jakarta.mail.MessagingException;
import org.springframework.core.io.InputStreamSource;
import ru.superu.rentcarregister.model.EmailRegister;

public interface EmailService {

    public void sendWithHtml(EmailRegister email) throws MessagingException;
}
