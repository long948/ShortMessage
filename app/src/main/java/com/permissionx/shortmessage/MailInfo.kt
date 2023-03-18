package com.permissionx.shortmessage


import java.util.Properties

class MailInfo {

    var mailServerHost: String? = null// 发送邮件的服务器的IP
    var mailServerPort: String? = null// 发送邮件的服务器的端口
    var fromAddress: String? = null// 邮件发送者的地址
    var toAddress: String? = null   // 邮件接收者的地址
    var userName: String? = null// 登陆邮件发送服务器的用户名
    var password: String? = null// 登陆邮件发送服务器的密码
    var isValidate = true// 是否需要身份验证
    var subject: String? = null// 邮件主题
    var content: String? = null// 邮件的文本内容
    var attachFileNames: Array<String>? = null// 邮件附件的文件名

    /**
     * 获得邮件会话属性
     */
    val properties: Properties
        get() {
            val p = Properties()
            p["mail.smtp.host"] = this.mailServerHost!!
            p["mail.smtp.port"] = this.mailServerPort!!
            p["mail.smtp.auth"] = if (isValidate) "true" else "false"
            return p
        }
}
