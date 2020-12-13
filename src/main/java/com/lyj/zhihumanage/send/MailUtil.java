package com.lyj.zhihumanage.send;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
/**
 * @author ：lyj
 * @email: : iclyj@iclyj.cn
 * @date ：2020/1/17
 */
@Component
@Slf4j
public class MailUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${mail.receive}")
    private String receive;


    /**
     * 发送邮件
     *
     * @param title
     * @param context
     */
    public void sendMailMessage(String title, String context) {
        SimpleMailMessage message = new SimpleMailMessage();
        log.info("title:{}, context{}", title, context);
        message.setFrom(sender); // 邮件发送者
        message.setTo(receive); // 发送邮件的邮箱
        message.setSubject(title);
        message.setText(context);
        try {
            mailSender.send(message);
            log.info("### 邮件发送成功 ###");
        } catch (MailException ex) {
            log.error("邮件发送失败, receive: {}, context: {}", receive, context, ex);
        }
    }

}
