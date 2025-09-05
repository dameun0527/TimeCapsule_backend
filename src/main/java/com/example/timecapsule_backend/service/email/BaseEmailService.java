package com.example.timecapsule_backend.service.email;

import com.example.timecapsule_backend.controller.email.dto.EmailPayload;
import com.example.timecapsule_backend.domain.capsule.ThemeType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class BaseEmailService {

    protected final JavaMailSender mailSender;
    protected final SpringTemplateEngine templateEngine;

    public void send(EmailPayload payload) {
        try {
            if (payload.theme() != null) {
                String html = renderTimeCapsuleHtml(
                        payload.capsuleTitle() == null ? payload.subject() : payload.capsuleTitle(),
                        payload.html() != null ? payload.html() : payload.text(),
                        payload.to(),
                        payload.theme()
                );
                sendHtmlEmail(payload.to(), payload.subject(), html);
            } else if (payload.html() != null) {
                sendHtmlEmail(payload.to(), payload.subject(), payload.html());
            } else {
                sendSimpleEmail(payload.to(), payload.subject(), payload.text());
            }
            log.debug("메일 전송 성공: to={}, subject={}", payload.to(), payload.subject());
        } catch (Exception e) {
            log.error("메일 전송 실패: to={}, subject={}, err={}", payload.to(), payload.subject(), e.getMessage(), e);
            throw (e instanceof MailSendException) ? (MailSendException) e : new MailSendException("메일 전송 실패: " + e.getMessage(), e);
        }
    }

    private void sendSimpleEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@timecapsule.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    private void sendHtmlEmail(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("noreply@timecapsule.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new MailSendException("Html 메일 전송 실패", e);
        }
    }

    // =================== Template ===================
    protected String renderTimeCapsuleHtml(String capsuleTitle, String content, String to, ThemeType theme) {
        String template = resolveTemplate(theme);
        Context context = new Context();
        context.setVariable("capsuleTitle", capsuleTitle);
        context.setVariable("content", content);
        context.setVariable("recipientEmail", to);
        return templateEngine.process(template, context);
    }

    protected String resolveTemplate(ThemeType theme) {
        if (theme == null) return "email/timecapsule-basic";
        return switch (theme) {
            case CHRISTMAS_TREE -> "email/christmas-tree";
            case BIRTHDAY_CAKE -> "email/birthday-cake";
            case EXAM_CALENDAR -> "email/exam-calendar";
            case ANNIVERSARY_PHOTO_FRAME -> "email/anniversary-photo-frame";
            case TIME_CAPSULE_VAULT -> "email/time-capsule-vault";
            default -> "email/timecapsule-basic";
        };
    }

    protected static long ms(long t0) {
        return Duration.ofNanos(System.nanoTime() - t0).toMillis();
    }
}