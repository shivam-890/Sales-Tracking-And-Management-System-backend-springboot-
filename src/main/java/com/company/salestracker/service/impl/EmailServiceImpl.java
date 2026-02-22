package com.company.salestracker.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.company.salestracker.exception.BadRequestException;
import com.company.salestracker.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService{
	

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void send(String to, String subject, String body)  {
       
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(message, true);
			 helper.setTo(to);
		        helper.setSubject(subject);
		        helper.setText(body, true); 
		        mailSender.send(message);
		        
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new BadRequestException("Email not sent");
		}

       
    }
    

}
