package com.projet.marathon.email;

import javax.mail.MessagingException;

public class Email {
    public static void envoyerEmail(String email,String sujet,String message){
        // Créez une instance d'EmailSender avec votre nom d'utilisateur et mot de passe pour l'envoi d'e-mails
        EmailSender emailSender = new EmailSender("Tahalammouchi005@gmail.com", "TAHACOSTACA25242321");

// Définissez les détails de l'e-mail de confirmation
        String recipientEmail = email;
        String subject = sujet;
        String messageText = message;

// Envoyez l'e-mail de confirmation
        try {
            emailSender.sendEmail(recipientEmail, subject, messageText);
            System.out.println("E-mail de confirmation envoyé avec succès !");
        } catch (MessagingException e) {
            System.out.println("Erreur lors de l'envoi de l'e-mail de confirmation : " + e.getMessage());
        }

    }
}
