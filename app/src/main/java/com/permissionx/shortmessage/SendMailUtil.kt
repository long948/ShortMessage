package com.permissionx.shortmessage


import java.io.File

class SendMailUtil {


    //qq
    private val HOST = "smtp.qq.com"
    private val PORT = "587"
//    private val PORT = "465"
    private val FROM_ADD = "948358878@qq.com" //发送方邮箱
    private val FROM_PSW = "wpvqorrxbuczbchd"//发送方邮箱授权码

    //    //163
    //    private static final String HOST = "smtp.163.com";
    //    private static final String PORT = "465"; //或者465  994
    //    private static final String FROM_ADD = "teprinciple@163.com";
    //    private static final String FROM_PSW = "teprinciple163";
    private val TO_ADD = "2584770373@qq.com"

//    fun send(file: File, toAdd: String) {
//        val mailInfo = creatMail(toAdd,"","","")
//        val sms = MailSender()
//        Thread(Runnable { sms.sendFileMail(mailInfo, file) }).start()
//    }

    fun send(toAdd: String,str :String,FROM_ADD:String,FROM_PSW:String) {
        val mailInfo = creatMail(toAdd,str,FROM_ADD,FROM_PSW)
        val sms = MailSender()
        Thread(Runnable { sms.sendTextMail(mailInfo) }).start()
    }

    private fun creatMail(toAdd: String,str :String,FROM_ADD:String,FROM_PSW:String): MailInfo {
        val mailInfo = MailInfo()
        mailInfo.mailServerHost = HOST
        mailInfo.mailServerPort = PORT
        mailInfo.isValidate = true
        mailInfo.userName = FROM_ADD // 你的邮箱地址
        mailInfo.password = FROM_PSW// 您的邮箱密码
        mailInfo.fromAddress = FROM_ADD // 发送的邮箱
        mailInfo.toAddress = toAdd // 发到哪个邮件去
        mailInfo.subject = "Hello" // 邮件主题
        mailInfo.content = str // 邮件文本
        return mailInfo
    }
}