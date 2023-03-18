package com.permissionx.shortmessage

import android.Manifest
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Toast
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.SimpleAdapter

import android.widget.ListView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.HashMap

import android.os.Message
import java.io.File.separator
import android.os.Environment.getExternalStorageDirectory
import android.text.TextUtils
import java.io.*


/**
 * @author SmallLetters@sina.com
 */

class MainActivity : AppCompatActivity() {
    private var SendMailbox:String=""
    private var ReceivingMailbox:String=""
    private var code:String=""
    private var time:Long=1
    private var timer :Timer?=null
    private var task: TimerTask? = null
    private var mListView: ListView? = null
    private var sa: SimpleAdapter? = null
    private var data: MutableList<Map<String, Any>>? = null
    private var str=""
    var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            // 要做的事情
           when(msg.what){
                1-> sendFileMail()

           }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    fun sendFileMail() {
        readSMS()
//        val file =
//            File(getExternalStorageDirectory().toString() + separator + "test.txt")
//        var os: OutputStream? = null
//        try {
//            os = FileOutputStream(file)
//            val str = "hello world"
//            val data = str.toByteArray()
//            os.write(data)
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        } finally {
//            try {
//                os?.close()
//            } catch (e: IOException) {
//            }
//
//        }
        SendMailUtil().send( ReceivingMailbox,str,SendMailbox,code)
    }

    private fun initView() {
        //得到ListView
//        mListView = findViewById(R.id.listView)
//        data = ArrayList()
        //配置适配置器S
//        sa = SimpleAdapter(
//            this, data, andlistViewroid.R.layout.simple_list_item_2,
//            arrayOf("names", "message"), intArrayOf(android.R.id.text1, android.R.id.text2)
//        )
//        mListView!!.adapter = sa


        readSMS.setOnClickListener {
            if (timer!=null){
                timer?.cancel()
                timer=null
            }
            timer=Timer()
            if (task!=null){
                task?.cancel()
                task=null
            }
            task = object : TimerTask() {
                override fun run() {
                    val message = Message()
                    message.what = 1
                    handler.sendMessage(message)
                }
            }
            if (TextUtils.isEmpty(SendMailboxEdt.text.toString())){
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(ReceivingMailboxEdt.text.toString())){
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(codeEdt.text.toString())){
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(timeEdt.text.toString())){
                return@setOnClickListener
            }
            SendMailbox= SendMailboxEdt.text.toString()?:""
            ReceivingMailbox=ReceivingMailboxEdt.text.toString()?:""
            code=codeEdt.text.toString()?:""
            time=timeEdt.text.toString().toLongOrNull()?:6
            timer?.schedule(task,1000,time*1000)
        }
    }

    /**
     * 检查申请短信权限
     */
    private fun checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //未获取到读取短信权限

            //向系统申请权限
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_SMS), REQ_CODE_CONTACT
            )
        } else {
            query()
        }
    }

    /**
     * 点击读取短信
     *
     */
   private  fun readSMS() {
        checkSMSPermission()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        //判断用户是否，同意 获取短信授权
        if (requestCode == REQ_CODE_CONTACT && grantResults.size > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            //获取到读取短信权限
            query()
        } else {
            Toast.makeText(this, "未获取到短信权限", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun query() {

        //读取所有短信
//        val uri = Uri.parse("content://sms/")//读取所有短信
        val uri = Uri.parse("content://sms/inbox")//读取收件箱短信
        val resolver = contentResolver
        val cursor =
            resolver.query(uri, arrayOf("_id", "address", "body", "date", "type"), null, null, null)
        if (cursor != null && cursor.count > 0) {
            var _id: Int
            var address: String
            var body: String
            var date: String
            var type: Int
            if (cursor.moveToFirst()){
                val map = HashMap<String, Any>()
                _id = cursor.getInt(0)
                address = cursor.getString(1)
                body = cursor.getString(2)
                date = cursor.getString(3)
                type = cursor.getInt(4)
                map["names"] = body
                Log.e("First","_id=$_id address=$address body=$body date=$date type=$type")
                txt.text=body
                str=body
            }

//            while (cursor.moveToNext()) {
//                val map = HashMap<String, Any>()
//                _id = cursor.getInt(0)
//                address = cursor.getString(1)
//                body = cursor.getString(2)
//                date = cursor.getString(3)
//                type = cursor.getInt(4)
//                map["names"] = body
//
//                Log.i("test", "_id=$_id address=$address body=$body date=$date type=$type")
//                data!!.add(map)
//                //通知适配器发生改变
//                sa!!.notifyDataSetChanged()
//            }

        }
    }

    companion object {
        val REQ_CODE_CONTACT = 1
    }
}
