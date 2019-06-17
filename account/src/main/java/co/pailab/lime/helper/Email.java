package co.pailab.lime.helper;

import co.pailab.lime.model.User;
import co.pailab.lime.model.UserGroupChange;
import co.pailab.lime.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

@Component
public class Email {
    private static String from = "noreply-lime@pailab.co";
    private static String fromName = "noreply-lime";
    private static String body;
    private static String subject;
    private static String csEmail = "contact@pailab.co";

    @Value("${spring.mail.username}")
    private String fromGmail;

    @Value("${spring.mail.password}")
    private String passwordGmail;

    @Value("${spring.mail.usernameSystem}")
    private String fromSystemMail;

    @Value("${spring.mail.passwordSystem}")
    private String passwordSystemGmail;

    public void activationEmail(User user) throws UnsupportedEncodingException, MessagingException {
        subject = "Activate Your Vitae Account";
        body = "<strong>" + user.getActivationToken() + "</strong> is activation code for your Vitae account.";

        sendGmail(user.getEmail(), subject, body, false, false);
    }

    public void confirmationPwChangeEmail(User user) throws MessagingException, UnsupportedEncodingException {
        subject = "Vitae Account Password Reset";
        body = "<strong>" + user.getPwConfirmationToken() + "</strong> is password reset code for your Vitae account.";
        sendGmail(user.getEmail(), subject, body, false, true);
    }

    public void sendGmailJobApplicationNotiToRecruiter(User recruiter, User applicant, String jobName, String applicantCV) throws Exception {
        subject = "New Job Application";
        String firstName = recruiter.getFirstName() != null ? recruiter.getFirstName() : "";
        String lastName = recruiter.getLastName() != null ? recruiter.getLastName() : "";

        body =  "<p>Dear " + firstName + " " + lastName + ",</p>"
                + "<p> <strong>" + applicant.getEmail() + "</strong> has applied to your Job Offer of " + jobName + "</p>"
                + "<p> Candidate Info: </p>"
                + applicantCV
                + "<p>If you have any further questions, please contact us via email : <strong>" + csEmail + "</strong></p>"
                + "<p>Best regards</p>";

        sendGmail(recruiter.getEmail(), subject, body, true, false);
    }

    public void sendGmailGroupChangeRequestConfirmation(UserGroupChange userGroupChange, User user) throws Exception {
        subject = "Upgrade Account - Request Received";

        String userFullName = getNotEmptyString(userGroupChange.getFullName(), user.getFullName());
        if (userFullName.equals("N/A")) userFullName = "User";

        String title = getNotEmptyString(userGroupChange.getTitle(), "");
        String phone = getNotEmptyString(userGroupChange.getPhone(), user.getPhone());
        String dateOfBirth = getNotEmptyString(userGroupChange.getDateOfBirth(), user.getDateOfBirth());
        String careerName = getNotEmptyString(userGroupChange.getCareerName(), "");
        String jobName = getNotEmptyString(userGroupChange.getJobName(), "");
        String selfDescription = getNotEmptyString(userGroupChange.getExperience(), userGroupChange.getSelfDescription());

        String bodySentToUser =  "<p>Dear " + userFullName + ",</p>"
                + "<p>Your upgrade request has been received with the below information : </p>"
                + "<p> _ Current account level : <strong>User</strong></p>"
                + "<p> _ Requested upgrade level : <strong>Expert<strong></p>"
                + "<p> _ Title : <strong>" + title + "</strong></p>"
                + "<p> _ Phone : <strong>" + phone + "</strong></p>"
                + "<p> _ Date of birth : <strong>" + dateOfBirth + "</strong></p>"
                + "<p> _ Career Name : <strong>" + careerName + "</strong></p>"
                + "<p> _ Job Name : <strong>" + jobName + "</strong></p>"
                + "<p> _ Self Description : <strong>" + selfDescription + "</strong></p>"
                + "<p>Your request will be processed in order it was received. Generally, upgrade requests are processed within 48 hours</p>"
                + "<p>If you have any further questions, please contact us via email : <strong>" + csEmail + "</strong></p>"
                + "<p>Best regards</p>";

        sendGmail(user.getEmail(), subject, bodySentToUser, true, false);
    }

    public void sendGmailGroupChangeApproval(User user, String oldGroup, String newGroup) throws Exception {
        subject = "Upgrade Account - Request Approved";
        body =  "<p>Dear " + user.getFirstName() + ",</p>"
                + "<p>Your upgrade request has been approved with the below information : </p>"
                + "<p> _ Previous account level : <strong>" + oldGroup + "</strong></p>"
                + "<p> _ Current account level : <strong>" + newGroup +"</strong></p>"
                + "<p>If you have any further questions, please contact us via email : <strong>" + csEmail + "</strong></p>"
                + "<p>Best regards</p>";


        sendGmail(user.getEmail(), subject, body, false, true);
    }

    public void sendGmailJobApplicationNotiToApplicant(User applicant, String jobName, String companyName) throws Exception {
        subject = "New Job Application";
        String firstName = applicant.getFirstName() != null ? applicant.getFirstName() : "";
        String lastName = applicant.getLastName() != null ? applicant.getLastName() : "";

        body = "<p>Dear " + firstName + " " + lastName + ",</p>"
                + "<p>You have successfully applied to job " + jobName + " at " + companyName + "</p>"
                + "<p>If you have any further questions, please contact us via email : " + csEmail + "</p>"
                + "<p>Best regards</p>";

        sendGmail(applicant.getEmail(), subject, body, false, false);
    }

    public void sendGmailJobOfferNotiToRecruiter(User recruiter, String nameEn) throws Exception {
        subject = "New Job Offer";
        body = "<p>Dear " + returnIfNull(recruiter.getFullName(), "Recruiter")+ ",</p>"
                +"<p>You have successfully created a new Job Offer of " + returnIfNull(nameEn, "") + "</p>"
                +"<p>If you have any further questions, please contact us via email : " + csEmail + "</p>"
                +"<p>Best regards</p>";
        sendGmail(recruiter.getEmail(), subject, body, false, false);
    }

    public static void sesActivationEmail(User user, EmailService emailService) throws Exception {
        subject = "Activate Your Vitae Account";
        body = "<strong>" + user.getActivationToken() + "</strong> is activation code for your Vitae account.";
        emailService.sendSesEmail(from, fromName, user.getEmail(), subject, body);
    }

    public static void sesConfirmationPwChangeEmail(User user, EmailService emailService) throws Exception {
        subject = "Vitae Account Password Reset";
        body = user.getPwConfirmationToken() + " is password reset code for your Vitae account.";
        emailService.sendSesEmail(from, fromName, user.getEmail(), subject, body);
    }

    public static void sendJobApplicationNotiToRecruiter(User recruiter, User applicant, String jobName, EmailService emailService) throws Exception {
        subject = "New Job Application";
        String firstName = recruiter.getFirstName() != null ? recruiter.getFirstName() : "";
        String lastName = recruiter.getLastName() != null ? recruiter.getLastName() : "";
        body =  "<p>Dear " + firstName + " " + lastName + ",</p>"
                + "<p> <strong>" + applicant.getEmail() + "</strong> has applied to your Job Offer of " + jobName
                + "<p>If you have any further questions, please contact us via email : <strong>" + csEmail + "</strong></p>"
                + "<p>Best regards</p>";
        emailService.sendSesEmail(from, fromName, recruiter.getEmail(), subject, body);
    }

    public static void sendGroupChangeRequestConfirmation(UserGroupChange userGroupChange, User user, EmailService emailService) throws Exception {
        subject = "Upgrade Account - Request Received";

        String userFullName = getNotEmptyString(userGroupChange.getFullName(), user.getFullName());
        if (userFullName.equals("N/A")) userFullName = "User";

        String title = getNotEmptyString(userGroupChange.getTitle(), "");
        String phone = getNotEmptyString(userGroupChange.getPhone(), user.getPhone());
        String dateOfBirth = getNotEmptyString(userGroupChange.getDateOfBirth(), user.getDateOfBirth());
        String careerName = getNotEmptyString(userGroupChange.getCareerName(), "");
        String jobName = getNotEmptyString(userGroupChange.getJobName(), "");
        String selfDescription = getNotEmptyString(userGroupChange.getExperience(), userGroupChange.getSelfDescription());

        String bodySentToUser =  "<p>Dear " + userFullName + ",</p>"
                + "<p>Your upgrade request has been received with the below information : </p>"
                + "<p> _ Current account level : <strong>User</strong></p>"
                + "<p> _ Requested upgrade level : <strong>Expert<strong></p>"
                + "<p> _ Title : <strong>" + title + "</strong></p>"
                + "<p> _ Phone : <strong>" + phone + "</strong></p>"
                + "<p> _ Date of birth : <strong>" + dateOfBirth + "</strong></p>"
                + "<p> _ Career Name : <strong>" + careerName + "</strong></p>"
                + "<p> _ Job Name : <strong>" + jobName + "</strong></p>"
                + "<p> _ Self Description : <strong>" + selfDescription + "</strong></p>"
                + "<p>Your request will be processed in order it was received. Generally, upgrade requests are processed within 48 hours</p>"
                + "<p>If you have any further questions, please contact us via email : <strong>" + csEmail + "</strong></p>"
                + "<p>Best regards</p>";

//        //Optional Email Sent to Admin
//        String bodySentToAdmin = "<p> New group upgrade request created:</p>"
//                + "<p> _ Current account level : User</p>"
//                + "<p> _ Requested upgrade level : Expert</p>"
//                + "<p> _ Email : " + user.getEmail() + "</p>"
//                + "<p> _ Full Name : " + userFullName + "</p>"
//                + "<p> _ Title : " + title + "</p>"
//                + "<p> _ Phone : " + phone + "</p>"
//                + "<p> _ Date of birth : " + dateOfBirth + "</p>"
//                + "<p> _ Career Name : " + careerName + "</p>"
//                + "<p> _ Career Id : " + userGroupChange.getCareerId() + "</p>"
//                + "<p> _ Job Name : " + jobName + "</p>"
//                + "<p> _ Job Id : " + userGroupChange.getJobId() + "</p>"
//                + "<p> _ Self Description : " + selfDescription + "</p>"
//                + "<p>Your request will be processed in order it was received. Generally, upgrade requests are processed within 48 hours</p>"
//                + "<p>If you have any further questions, please contact us via email : " + csEmail + "</p>"
//                + "<p>Best regards</p>";

        emailService.sendSesEmailWithCc(from, fromName, user.getEmail(), subject, bodySentToUser, csEmail);
//        emailService.sendSesEmail(from, fromName, csEmail, subject, bodySentToAdmin);
    }

    private static String getNotEmptyString(String fistString, String secondString) {
        if (fistString != null && !fistString.equals("")) return fistString;
        if (secondString != null && !secondString.equals("")) return secondString;
        return "N/A";
    }

    public static void sendGroupChangeApproval(User user, String oldGroup, String newGroup, EmailService emailService) throws Exception {
        subject = "Upgrade Account - Request Approved";
        body =  "<p>Dear " + user.getFirstName() + ",</p>"
                + "<p>Your upgrade request has been approved with the below information : </p>"
                + "<p> _ Previous account level : <strong>" + oldGroup + "</strong></p>"
                + "<p> _ Current account level : <strong>" + newGroup +"</strong></p>"
                + "<p>If you have any further questions, please contact us via email : <strong>" + csEmail + "</strong></p>"
                + "<p>Best regards</p>";

        emailService.sendSesEmailWithCc(from, fromName, user.getEmail(), subject, body, csEmail);
    }

    public static void sendJobApplicationNotiToApplicant(User applicant, String jobName, String companyName, EmailService emailService) throws Exception {
        subject = "New Job Application";
        String firstName = applicant.getFirstName() != null ? applicant.getFirstName() : "";
        String lastName = applicant.getLastName() != null ? applicant.getLastName() : "";
        body = "<p>Dear " + firstName + " " + lastName + ",</p>"
                + "<p>You have successfully applied to job " + jobName + " at " + companyName + "</p>"
                + "<p>If you have any further questions, please contact us via email : " + csEmail + "</p>"
                + "<p>Best regards</p>";

        emailService.sendSesEmailWithCc(from, fromName, applicant.getEmail(), subject, body, csEmail);
//        emailService.sendSesEmail(from, fromName, applicant.getEmail(), subject, body);
    }

    public static void sendJobOfferNotiToRecruiter(User recruiter, String nameEn, EmailService emailService) throws Exception {
        subject = "New Job Offer";
        String firstName = recruiter.getFirstName() != null ? recruiter.getFirstName() : "";
        String lastName = recruiter.getLastName() != null ? recruiter.getLastName() : "";
        body = "<p>Dear " + firstName + " " + lastName + ",</p>"
                +"<p>You have successfully created a new Job Offer of " + nameEn + "</p>";
        emailService.sendSesEmail(from, fromName, recruiter.getEmail(), subject, body);
    }

//    private static void sendGmail(String email, String subject, String body) throws MessagingException, UnsupportedEncodingException {
//        try {
//            JavaMailSenderImpl sender = new JavaMailSenderImpl ();
//            sender.setHost("smtp.gmail.com");
//            sender.setPort(587);
//            sender.setUsername("vitae.pailab@gmail.com");
//            sender.setPassword("pailab2019@");
//
//            Properties props = sender.getJavaMailProperties();
//            props.put("mail.transport.protocol", "smtp");
//            props.put("mail.smtp.auth", "true");
//            props.put("mail.smtp.starttls.enable", "true");
//            props.put("mail.debug", "true");
//            props.put("mail.smtp.allow8bitmime", "true");
//            props.put("mail.smtps.allow8bitmime", "true");
//
//            MimeMessage message = sender.createMimeMessage();
//            message.setFrom(new InternetAddress("vitae.pailab@gmail.com", "Vitae no-reply"));
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            helper.setTo(email);
//            helper.setText(body, true);
//            helper.setSubject(subject);
//
//            sender.send(message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void sendGmailWithCc(String email, String subject, String body) throws MessagingException, UnsupportedEncodingException {
//        try {
//            JavaMailSenderImpl sender = new JavaMailSenderImpl ();
//            sender.setHost("smtp.gmail.com");
//            sender.setPort(587);
//            sender.setUsername("vitae.pailab@gmail.com");
//            sender.setPassword("pailab2019@");
//
//            Properties props = sender.getJavaMailProperties();
//            props.put("mail.transport.protocol", "smtp");
//            props.put("mail.smtp.auth", "true");
//            props.put("mail.smtp.starttls.enable", "true");
//            props.put("mail.debug", "true");
//            props.put("mail.smtp.allow8bitmime", "true");
//            props.put("mail.smtps.allow8bitmime", "true");
//
//            MimeMessage message = sender.createMimeMessage();
//            message.setFrom(new InternetAddress("vitae.pailab@gmail.com", "Vitae no-reply"));
//
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            helper.setTo(email);
//            helper.addCc(csEmail);
//            helper.setText(body, true);
//            helper.setSubject(subject);
//
//            sender.send(message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
    private void sendGmail(String email, String subject, String body, Boolean ccRequired, Boolean systemMail) throws UnsupportedEncodingException, MessagingException {
        try {
            JavaMailSenderImpl sender = new JavaMailSenderImpl ();
            sender.setHost("smtp.gmail.com");
            sender.setPort(587);
            sender.setDefaultEncoding("UTF-8");

            if(systemMail){
                sender.setUsername(fromSystemMail);
                sender.setPassword(passwordSystemGmail);
            }else {
                sender.setUsername(fromGmail);
                sender.setPassword(passwordGmail);
            }

            Properties props = sender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.debug", "true");
            props.put("mail.smtp.allow8bitmime", "true");
            props.put("mail.smtps.allow8bitmime", "true");

            MimeMessage message = sender.createMimeMessage();
            if(systemMail) {
                message.setFrom(new InternetAddress(fromSystemMail, "Vitae no-reply"));
            }else {
                message.setFrom(new InternetAddress(fromGmail, "Vitae service"));
            }

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            if(ccRequired)
                helper.addCc(csEmail);
            helper.setText(body, true);
            helper.setSubject(subject);
            System.out.println("Sending ...");
            sender.send(message);
            System.out.println("Sent to " + email);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private String returnIfNull(String input, String returnIfNull) {
        return input != null ? input : returnIfNull;
    }

}