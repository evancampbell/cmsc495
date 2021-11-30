package main.service;

import main.properties.SMTPProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service("emailService")
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SMTPProperties properties;

    public void sendMail(String to, String time, String url, String filename) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
                mimeMessage.setFrom(new InternetAddress(properties.getAddress()));
                mimeMessage.setSubject("New screenshot!");

                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

                helper.setText(
                        "<html><body><p>Hello! This screenshot of " + url + " was taken at " +
                                time + ".</p><br/><img src='cid:screenshot'></body></html>",
                        true);

                FileSystemResource res = new FileSystemResource(new File(filename));
                helper.addInline("screenshot", res);
            }
        };

        try {
            mailSender.send(preparator);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }
}
