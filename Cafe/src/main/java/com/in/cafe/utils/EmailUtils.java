package com.in.cafe.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
public class EmailUtils {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleMessage(String to, String subject, String text, List<String> list){

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(("studyspring62@gmail.com"));
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        if(list!=null && list.size()>0){
            message.setCc(getCcArray(list));

        }

        javaMailSender.send(message);

    }

    private String[] getCcArray(List<String> ccList){
        String[] cc= new String[ccList.size()];
        for(int i=0; i< ccList.size(); i++){
            cc[i] = ccList.get(i);
        }
        return cc;
    }

    public void forgotMail(String to, String sub, String pass) throws MessagingException{
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("springstudy62@gmail.com");
        helper.setTo(to);
        helper.setSubject((sub));
        String htmlMsg = "";
        message.setContent(htmlMsg, "text/html");
        javaMailSender.send(message);
    }
}
