package io.github.zh.tools;

import io.github.zh.model.EmailRequest;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailTool {
    private final JavaMailSender javaMailSender;
    private final String from;

    @Autowired
    public EmailTool(JavaMailSender javaMailSender, @Value("${spring.mail.username}") String from) {
        this.javaMailSender = javaMailSender;
        this.from = from;
    }

    /**
     * 发送邮件到指定的邮箱
     */
    @Tool(name = "sendMailMessage", description = "发送邮件到指定的邮箱，email 为收件人的邮箱，subject 为邮件标题，message 为邮件的具体内容")

    public void sendMailMessage(EmailRequest emailRequest) {

        log.info("发送邮件到指定的邮箱，{} 为收件人的邮箱，{} 为邮件标题，{} 为邮件的具体内容", emailRequest.getEmail(), emailRequest.getSubject(), emailRequest.getMessage());

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        try {
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(emailRequest.getEmail());
            mimeMessageHelper.setSubject(emailRequest.getSubject());
            mimeMessageHelper.setText(emailRequest.getMessage());
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("发送邮件失败", e);
        }

    }
}
