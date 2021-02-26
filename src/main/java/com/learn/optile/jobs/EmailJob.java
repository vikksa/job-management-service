package com.learn.optile.jobs;

import com.learn.optile.entities.Job;
import com.learn.optile.exceptions.JobServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.ObjectUtils;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.*;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Job to send emails using provided data and Application Email Configuration
 */

public class EmailJob extends ExecutableJob {

    private static final Logger logger = LoggerFactory.getLogger(EmailJob.class);

    public EmailJob(ApplicationContext applicationContext, Job job) {
        super(applicationContext, job);
    }

    @Override
    public void taskDefinition() {
        logger.info("Email Job Started");
        Environment environment = getApplicationContext().getEnvironment();
        String smtpHostName = environment.getProperty("smtp.hostname");
        Integer smtpPort = environment.getProperty("smtp.port",Integer.class);
        String smtpUsername = environment.getProperty("smtp.authentication.username");
        String smtpPassword = environment.getProperty("smtp.authentication.password");
        String smtpFromAddress = environment.getProperty("smtp.from");
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(smtpHostName);
        javaMailSender.setPort(smtpPort);
        javaMailSender.setUsername(smtpUsername);
        javaMailSender.setPassword(smtpPassword);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        if (ObjectUtils.isEmpty(getJob().getRawJobData())) {
            throw new JobServiceException("EMAIL data can't be empty");
        }

        if (ObjectUtils.isEmpty(getJob().getRawJobData().get("emailBody"))) {
            throw new JobServiceException("EMAIL body can't be empty");
        }
        if (ObjectUtils.isEmpty(getJob().getRawJobData().get("to"))) {
            throw new JobServiceException("EMAIL to Addresses can't be empty");
        }

        try {
            mimeMessage.setFrom(smtpFromAddress);
            mimeMessage.setRecipients(Message.RecipientType.TO, toInternetAddress(getJob().getRawJobData().get("to").toString()));

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(getJob().getRawJobData().get("emailBody").toString(), "text/plain");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            mimeMessage.setContent(multipart);
            mimeMessage.setSubject(getJob().getRawJobData().getOrDefault("emailSubject", "").toString());
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            logger.error("Error while sending email", e);
            throw new JobServiceException("Error while sending email", e);
        }
        logger.info("Email Job completed");
    }

    private InternetAddress[] toInternetAddress(String emails) {
        if (ObjectUtils.isEmpty(emails)) {
            return new InternetAddress[]{};
        }
        return Arrays.stream(emails.split(","))
                .map(address -> {
                    try {
                        InternetAddress internetAddress = new InternetAddress(address);
                        internetAddress.validate();
                        return internetAddress;
                    } catch (AddressException e) {
                        throw new JobServiceException(String.format("Invalid email address: '%s'", address), e);
                    }
                }).collect(Collectors.toList()).toArray(new InternetAddress[]{});
    }

    @Override
    public void rollback() {

    }
}
