/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.nwtis.mpavlovi2.util;

import java.util.Date;
import org.foi.nwtis.mpavlovi2.konfiguracije.Konfiguracija;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Milan
 */
public class SimpleMailSender {
    
    private String from;
    private String to;
    private String subject;
    private String content;
    private String server;
    private Konfiguracija config;

    public SimpleMailSender() {
    
    }
    
    public SimpleMailSender(Konfiguracija config) {
        this.config = config;
        init();
    }
    
    private void init() {
        from = config.dajPostavku("posiljatelj");
        to = config.dajPostavku("primatelj");
        subject = config.dajPostavku("predmet");
        server = config.dajPostavku("mailServer");
    }
    
    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setConfig(Konfiguracija config) {
        this.config = config;
    }
    
    public void sendMail() {
        try {    
            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", server);
            
            Session session = Session.getInstance(properties, null);

            MimeMessage message = new MimeMessage(session);

            Address fromAddress = new InternetAddress(from);
            message.setFrom(fromAddress);

            Address[] toAddresses = InternetAddress.parse(to);
            message.setRecipients(Message.RecipientType.TO, toAddresses);

            message.setSubject(subject);
            message.setSentDate(new Date());
            message.setText(content, "UTF-8", "plain");

            Transport.send(message);

        } catch (AddressException | SendFailedException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    
}
