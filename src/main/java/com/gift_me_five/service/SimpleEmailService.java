package com.gift_me_five.service;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
 
@Service
public class SimpleEmailService {
     
    @Autowired
    private JavaMailSender sender;
 
    public void email(String receiver, String subject, String message) {
        try {
            sendEmail(receiver, subject, message);
        }catch(Exception ex) {
        	throw new RuntimeException(ex.getMessage());
        }
    }
 
    private void sendEmail(String receiver, String subject, String message) throws Exception{
        MimeMessage mail = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail);
         
        helper.setTo(receiver);
        helper.setText(message);
        helper.setSubject(subject);
         
        sender.send(mail);
    }
    
    public String emailDummy(String receiver, String subject, String message) {
    	
    	String emailText = "SendTo: /" + receiver + "/\n" +
    	          "Subject: " + subject + "\n" + 
    		      "Message: \n" + message + "\n\n";
    	System.out.println(emailText);
    	return emailText;
    }
}