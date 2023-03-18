package com.permissionx.shortmessage


import android.util.Log

import java.io.File
import java.util.Date
import java.util.Properties

import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.Address
import javax.mail.Authenticator
import javax.mail.BodyPart
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Multipart
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.internet.MimeUtility

/**
 * 发送器2023-3-18
 */
class MailSender {
    /**
     * 以文本格式发送邮件
     * @param mailInfo 待发送的邮件的信息
     */
    fun sendTextMail(mailInfo: MailInfo): Boolean {

        // 判断是否需要身份认证
        var authenticator: MyAuthenticator? = null
        val pro = mailInfo.properties
        if (mailInfo.isValidate) {
            // 如果需要身份认证，则创建一个密码验证器
            authenticator = MyAuthenticator(mailInfo.userName!!, mailInfo.password!!)
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        val sendMailSession = Session.getDefaultInstance(pro, authenticator)

        //      Session sendMailSession = Session.getInstance(pro, new Authenticator() {
        //          @Override
        //          protected PasswordAuthentication getPasswordAuthentication() {
        //              return new PasswordAuthentication(mailInfo.getUserName(),mailInfo.getPassword());
        //          }
        //      });

        try {
            // 根据session创建一个邮件消息
            val mailMessage = MimeMessage(sendMailSession)
            // 创建邮件发送者地址
            val from = InternetAddress(mailInfo.fromAddress)
            // 设置邮件消息的发送者
            mailMessage.setFrom(from)
            // 创建邮件的接收者地址，并设置到邮件消息中
            val to = InternetAddress(mailInfo.toAddress)
            mailMessage.setRecipient(Message.RecipientType.TO, to)
            // 设置邮件消息的主题
            mailMessage.subject = mailInfo.subject
            // 设置邮件消息发送的时间
            mailMessage.sentDate = Date()

            // 设置邮件消息的主要内容
            val mailContent = mailInfo.content
            mailMessage.setText(mailContent)
            // 发送邮件
            Transport.send(mailMessage)
            return true
        } catch (ex: MessagingException) {
            ex.printStackTrace()
        }

        return false
    }


    /**
     * 发送带附件的邮件
     * @param info
     * @return
     */
    fun sendFileMail(info: MailInfo, file: File): Boolean {
        val attachmentMail = createAttachmentMail(info, file)
        try {
            Transport.send(attachmentMail!!)
            return true
        } catch (e: MessagingException) {
            e.printStackTrace()
            return false
        }

    }

    /**
     * 创建带有附件的邮件
     * @return
     */
    private fun createAttachmentMail(info: MailInfo, file: File): Message? {
        //创建邮件
        var message: MimeMessage? = null
        val pro = info.properties
        try {

            val sendMailSession = Session.getInstance(pro, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(info.userName, info.password)
                }
            })

            message = MimeMessage(sendMailSession)
            // 设置邮件的基本信息
            //创建邮件发送者地址
            val from = InternetAddress(info.fromAddress)
            //设置邮件消息的发送者
            message.setFrom(from)
            //创建邮件的接受者地址，并设置到邮件消息中
            val to = InternetAddress(info.toAddress)
            //设置邮件消息的接受者, Message.RecipientType.TO属性表示接收者的类型为TO
            message.setRecipient(Message.RecipientType.TO, to)
            //邮件标题
            message.subject = info.subject

            // 创建邮件正文，为了避免邮件正文中文乱码问题，需要使用CharSet=UTF-8指明字符编码
            val text = MimeBodyPart()
            text.setContent(info.content, "text/html;charset=UTF-8")

            // 创建容器描述数据关系
            val mp = MimeMultipart()
            mp.addBodyPart(text)
            // 创建邮件附件
            val attach = MimeBodyPart()

            val ds = FileDataSource(file)
            val dh = DataHandler(ds)
            attach.dataHandler = dh
            attach.fileName = MimeUtility.encodeText(dh.name)
            mp.addBodyPart(attach)
            mp.setSubType("mixed")
            message.setContent(mp)
            message.saveChanges()

        } catch (e: Exception) {
            Log.e("TAG", "创建带附件的邮件失败")
            e.printStackTrace()
        }

        // 返回生成的邮件
        return message
    }

    companion object {

        /**
         * 以HTML格式发送邮件
         * @param mailInfo 待发送的邮件信息
         */
        fun sendHtmlMail(mailInfo: MailInfo): Boolean {
            // 判断是否需要身份认证
            var authenticator: MyAuthenticator? = null
            val pro = mailInfo.properties
            // 如果需要身份认证，则创建一个密码验证器
            if (mailInfo.isValidate) {
                authenticator = MyAuthenticator(mailInfo.userName!!, mailInfo.password!!)
            }
            // 根据邮件会话属性和密码验证器构造一个发送邮件的session
            val sendMailSession = Session.getDefaultInstance(pro, authenticator)
            try {
                // 根据session创建一个邮件消息
                val mailMessage = MimeMessage(sendMailSession)
                // 创建邮件发送者地址
                val from = InternetAddress(mailInfo.fromAddress)
                // 设置邮件消息的发送者
                mailMessage.setFrom(from)
                // 创建邮件的接收者地址，并设置到邮件消息中
                val to = InternetAddress(mailInfo.toAddress)
                // Message.RecipientType.TO属性表示接收者的类型为TO
                mailMessage.setRecipient(Message.RecipientType.TO, to)
                // 设置邮件消息的主题
                mailMessage.subject = mailInfo.subject
                // 设置邮件消息发送的时间
                mailMessage.sentDate = Date()
                // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
                val mainPart = MimeMultipart()
                // 创建一个包含HTML内容的MimeBodyPart
                val html = MimeBodyPart()
                // 设置HTML内容
                html.setContent(mailInfo.content, "text/html; charset=utf-8")
                mainPart.addBodyPart(html)
                // 将MiniMultipart对象设置为邮件内容
                mailMessage.setContent(mainPart)
                // 发送邮件
                Transport.send(mailMessage)
                return true
            } catch (ex: MessagingException) {
                ex.printStackTrace()
            }

            return false
        }
    }
}
