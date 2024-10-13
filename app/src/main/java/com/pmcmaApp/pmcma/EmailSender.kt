package com.pmcmaApp.pmcma

import android.os.AsyncTask
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailSender(
    private val recipientEmail: String,
    private val subject: String,
    private val messageBody: String
) : AsyncTask<Void, Void, Void>() {

    override fun doInBackground(vararg params: Void?): Void? {
        val properties = System.getProperties()
        properties["mail.smtp.host"] = "smtp.example.com" // Replace with your SMTP server
        properties["mail.smtp.port"] = "587" // or the port you are using
        properties["mail.smtp.auth"] = "true"
        properties["mail.smtp.starttls.enable"] = "true" // Use TLS

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication("pmcmassociation@gmail.com", "aijd eftq itme ojaw") // Replace with your email and password
            }
        })

        try {
            val message = MimeMessage(session)
            message.setFrom(InternetAddress("pmcmassociation@gmail.com")) // Replace with your email
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
            message.subject = subject
            message.setText(messageBody)

            Transport.send(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
