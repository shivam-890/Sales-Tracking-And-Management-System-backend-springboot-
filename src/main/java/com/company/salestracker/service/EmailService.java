package com.company.salestracker.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void send(String to, String subject, String body) throws MessagingException;


}
