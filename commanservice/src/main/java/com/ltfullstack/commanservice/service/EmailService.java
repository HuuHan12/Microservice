package com.ltfullstack.commanservice.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;


import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.xml.transform.Templates;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;


    @Autowired
    private Configuration config;

    /**
     * Gửi email đến người nhận với nội dung và tùy chọn định dạng HTML hoặc văn bản thuần,
     * có thể đính kèm tệp nếu cần.
     *
     * @param to         Địa chỉ email người nhận
     * @param subject    Tiêu đề của email
     * @param text       Nội dung email
     * @param isHtml     Nếu true, nội dung sẽ được gửi dưới dạng HTML; nếu false, là văn bản thuần
     * @param attactment Tệp đính kèm gửi kèm email (có thể là null nếu không có file đính kèm)
     */


    public void sendMail(String to, String subject, String text, boolean isHtml, File attactment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, isHtml);


            // add attactment if provided
            if (attactment != null) {
                FileSystemResource fileSystemResource = new FileSystemResource(attactment);
                helper.addAttachment(fileSystemResource.getFilename(), fileSystemResource);
            }

            mailSender.send(message);
            log.info("Send  mail successfully {}", to);


        } catch (MessagingException e) {
            log.error("Failed to send email object" + to, e);

        }
    }

    /**
     * Gửi email đến người nhận với nội dung và tùy chọn định dạng HTML hoặc văn bản thuần,
     * có thể đính kèm tệp nếu cần.
     *
     * @param to         Địa chỉ email người nhận
     * @param subject    Tiêu đề của email
     * @param templateName       Nội dung email
     * @param placeholders     Nếu true, nội dung sẽ được gửi dưới dạng HTML; nếu false, là văn bản thuần
     * @param attactment Tệp đính kèm gửi kèm email (có thể là null nếu không có file đính kèm)
     */
    public void sendMailWithTemplate(String to, String subject, String templateName, Map<String, Object> placeholders, File attactment) {
        try {
            Template t = config.getTemplate(templateName);
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, placeholders);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

             // add attactment if provided
            if (attactment != null) {
                FileSystemResource fileSystemResource = new FileSystemResource(attactment);
                helper.addAttachment(fileSystemResource.getFilename(), fileSystemResource);
            }
            mailSender.send(message);
            log.info("Send  mail successfully {}", to);

        } catch (MessagingException | IOException | TemplateException e) {
            log.error("Failed to send email object" + to, e);
        }
    }
}
