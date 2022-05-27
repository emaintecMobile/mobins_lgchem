package com.emaintec

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.makeRestartActivityTask
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.emaintec.lib.base.Emaintec
import com.emaintec.lib.device.Device
import com.emaintec.lib.network.*
import com.emaintec.mobins.R
import com.emaintec.mobins.Activity_Main
import com.emaintec.mobins.BuildConfig
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object Functions {
    var mapLang = HashMap<String, String>()

    /**
     * Snackbar 이용 시 OnClickListener 를 등록하지 않으면 클릭해서 닫을 수 없어서 사용한다.
     */
    var onClickListener: View.OnClickListener = View.OnClickListener { }
    /*
        소리 지원
     */
    lateinit var mTTS: TextToSpeech

    init {
        if (Define.SOUND) {
            Emaintec.application?.let {
                mTTS = TextToSpeech(
                    Emaintec.application!!.applicationContext,
                    TextToSpeech.OnInitListener { status ->
                        if (status != TextToSpeech.ERROR) {
                            //if there is no error then set language
                            mTTS.language = Locale.KOREAN
                        }
                    })
            }
        }

    }

    /*** 메세지 예제
    %s님.......
    var Msg : String = "${Data.instance._loginDeptNm} 님 로그인을 환영합니다."
    Functions.mapLang["MSG_LOGIN_S"]?.run { String.format(this,Data.instance._loginDeptNm) }?.let {
    Msg = it
    }
    Log.d("test",Msg)
     */
    object FilePath{
        fun getPath(fileName:String?=null):File {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                if(fileName.isNullOrBlank())
                {
                    Log.d("FilePath1",Environment.getExternalStorageDirectory().absolutePath)
                    return Environment.getExternalStorageDirectory()
                }
                Log.d("FilePath2",Environment.getExternalStoragePublicDirectory(fileName).absolutePath)
                return Environment.getExternalStoragePublicDirectory(fileName)
            }
            Log.d("FilePath3",Emaintec.application?.getExternalFilesDir(fileName)!!.absolutePath)
            return Emaintec.application?.getExternalFilesDir(fileName)!!
        }
        fun getCacheDir(context: Context): File? {
            var cacheDir: File? = null
//            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
//                cacheDir = File(Environment.getExternalStorageDirectory(), "cachefolder")
//                if (!cacheDir.isDirectory) {
//                    cacheDir.mkdirs()
//                }
//            }
//            if (!cacheDir!!.isDirectory) {
            cacheDir = context.cacheDir
//            }
            return cacheDir
        }
    }

    object DateUtil {
        fun getStartEndHour(strStartDate: String?, strEndDate: String): String {
            var hour = 0.0

            if (strStartDate.isNullOrBlank() || strEndDate.isNullOrBlank()) {
                return "0"
            }
            try {
                val dt = SimpleDateFormat("yyyy-MM-dd HH:mm")
                val sdate = dt.parse(strStartDate)
                val edate = dt.parse(strEndDate)
                val diff = edate.getTime() - sdate.getTime()
                hour = (diff / (1000 * 60 * 60)).toDouble()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return hour.toString()
        }

        fun DatePickerFormat(datePicker: DatePicker, format: String?): String {
            val day = datePicker.dayOfMonth
            val month = datePicker.month
            val year = datePicker.year

            val calendar = Calendar.getInstance()
            calendar.set(year, month, day)
            val date = calendar.time

            return SimpleDateFormat(if (format.isNullOrEmpty()) "yyyy-MM-dd" else format).format(
                date
            )
        }

        fun getDate(format: String? = null): String {
            return SimpleDateFormat(if (format.isNullOrEmpty()) "yyyy-MM-dd" else format).format(
                Date()
            )
        }

        fun getDate(format: String?, addDay: Int): String {
            val cal = Calendar.getInstance()
            cal.time = Date()
            cal.add(Calendar.DATE, addDay)
            return SimpleDateFormat(if (format.isNullOrEmpty()) "yyyy-MM-dd" else format).format(cal.time)
        }

        fun getDate(date: Long, format: String?): String {
            return SimpleDateFormat(if (format.isNullOrEmpty()) "yyyy-MM-dd" else format).format(
                date
            )
        }

        fun getWeekDay(): Int {
            val calendar = Calendar.getInstance()
            return calendar.get(Calendar.DAY_OF_WEEK)
        }

        //일요일 1
        //월요일 2
        //화요일 3
        //수요일 4
        //목요일 5
        //금요일 6
        //토요일 7
        fun getSunDayofWeekDate(format: String?): String {
            return getDate(format, -getWeekDay() + 1)
        }

        fun getMonDayofWeekDate(format: String?): String {
            var weekday = getWeekDay()
            if (getWeekDay() == 1) {
                weekday = 8
            }
            return getDate(format, -weekday + 2)
        }

        fun getFriDayofWeekDate(format: String?): String {
            return getDate(format, 6 - getWeekDay())
        }

        fun getSUNDayofWeekDate(format: String?): String {
            var weekday = getWeekDay()
            if (getWeekDay() == 1) {
                weekday = 8
            }
            return getDate(format, 8 - weekday)
        }

        fun transDateFormat(strDate: String, strFormat: String, strResultFormat: String): String {
            if(strDate.isNullOrEmpty()) return ""
            var simpleDateFormat2 = SimpleDateFormat(strFormat)
            var parsedDate: Date? = null
            try {
                parsedDate = simpleDateFormat2.parse(strDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            simpleDateFormat2 = SimpleDateFormat(strResultFormat)
            return simpleDateFormat2.format(parsedDate)
        }
    }

    object Noti {
        var Noti_Channel_ID = "1"
        var Noti_ID = 0

        @TargetApi(Build.VERSION_CODES.O)
        fun createNotiChannel(notificationManager: NotificationManager) {
            val name: CharSequence = "사용자 알람 채널명"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(
                Noti_Channel_ID, name, importance
            )
            mChannel.enableLights(true)
            mChannel.lightColor = Color.BLUE
            mChannel.enableVibration(true)
            notificationManager.createNotificationChannel(mChannel)
        }

        fun showNotification(
            context: Context,
            menu: String,
            title: String?,
            content: String,
            Memo: String
        ) {
            PushUtils.acquireWakeLock(context);
            val re_Noti_ID = Noti_ID
            val stackBuilder = TaskStackBuilder.create(context)


//        if(menu.isNotEmpty()) {
            val notificationIntent = Intent(context, Activity_Main::class.java)
            notificationIntent.putExtra("menu", menu)
            notificationIntent.putExtra("title", title)
            notificationIntent.putExtra("Content", content)
            notificationIntent.putExtra("Memo", Memo)
            notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            stackBuilder.addParentStack(Activity_Main::class.java)
            stackBuilder.addNextIntent(notificationIntent)
//        }
            val pendingIntent = stackBuilder.getPendingIntent(
                Noti_ID, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= 26) {
                notificationManager?.let { createNotiChannel(it) }
            }
            val builder = Notification.Builder(context)
            builder.setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(content)

            if (Build.VERSION.SDK_INT >= 26) {
                builder.setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE);
                builder.setChannelId(Noti_Channel_ID)
            }

            builder.setAutoCancel(true)

            builder.setOngoing(false) // 모두지우기나 스와이프로 삭제 가능...

            if (Memo.length > 0) {
                val Style = Notification.BigTextStyle(builder)
                Style.setBigContentTitle(title)
                Style.bigText(content + "\n" + Memo)
                Style.setSummaryText("더 보기")
                builder.style = Style
            }
            notificationManager?.notify(Noti_ID, builder.build())

            Noti_ID = re_Noti_ID + 1

        }
    }

    fun textToSpeech(str: String) {
        if (Define.SOUND)
            mTTS?.let {
                it.speak(str, TextToSpeech.QUEUE_FLUSH, null)
            }
    }

    fun stopSpeech() {
        if (Define.SOUND)
            if (mTTS.isSpeaking) {
                mTTS.stop()
            }
    }

    fun equals(left: String?, right: String?): Boolean {
        return null == left && null == right || null != left && left == right || null != right && right == left
    }


    fun getEmptyToZero(strEmpty: String?): String {
        return if (strEmpty.isNullOrEmpty()) "0" else strEmpty
    }


    fun ContainsCount(strString: String, delimiter: String): Int {
        var index = -1
        var count = 0
        var strString = strString
        while (true) {
            index = strString.indexOf(delimiter)
            if (index < 0) break
            count++
            strString = strString.substring(index + delimiter.length)
        }

        return count
    }

    fun toast(context: Context, Msg: String) {
        Toast.makeText(context, Msg, Toast.LENGTH_SHORT).show()
    }

    fun toastlong(context: Context, Msg: String) {
        Toast.makeText(context, Msg, Toast.LENGTH_LONG).show()
    }

    fun MessageBox(
        context: Context,
        Msg: String,
        sTitle: String? = "Info",
        langKey: String? = null
    ) {
        if (Msg.isNullOrBlank()) return
        var Msg = Msg
        langKey?.run {
            mapLang[langKey]?.let {
                Msg = it.toString()
            }
        }
        textToSpeech(Msg)
        AlertDialog.Builder(context)
            .setTitle(sTitle ?: "Info")
            .setMessage(Msg)
            .setCancelable(true)
            .setNegativeButton("확인", null)
            .create()
            .show()
    }

    fun MessageBoxOk(
        context: Context,
        Msg: String,
        Title: String?,
        langKey: String? = null,
        listener_OK: DialogInterface.OnClickListener?
    ) {
        if (Msg.isNullOrBlank()) return
        var Msg = Msg
        langKey?.run {
            mapLang[langKey]?.let {
                Msg = it.toString()
            }
        }
        textToSpeech(Msg)
        AlertDialog.Builder(context)
            .setTitle(Title ?: "Info")
            .setMessage(Msg)
            .setCancelable(true)
            .setPositiveButton("확인", listener_OK)
            .create()
            .show()
    }

    fun MessageBox(
        context: Context,
        Msg: String,
        Title: String?,
        listener_OK: DialogInterface.OnClickListener?
    ) {
        if (Msg.isNullOrBlank()) return
        var Msg = Msg
        AlertDialog.Builder(context)
            .setTitle(Title ?: "Info")
            .setMessage(Msg)
            .setCancelable(true)
            .setPositiveButton("확인", listener_OK)
            .setNegativeButton("취소", null)
            .create()
            .show()
    }

    fun Ping(host: String): Boolean {
        val runTime = Runtime.getRuntime()
        //        String host = "192.168.0.13";
        val cmd = "ping -c 1 -W 10 $host" //-c 1은 반복 횟수를 1번만 날린다는 뜻
        lateinit var proc: Process
        try {
            proc = runTime.exec(cmd)

        } catch (ie: IOException) {
            Log.d("LOG_runtime.exec()", ie.message.toString())
        }

        try {
            proc.waitFor()
        } catch (ie: InterruptedException) {
            Log.d("LOG_proc.waitFor", ie.message.toString())
        }

        //여기서 반환되는 ping 테스트의 결과 값은 0, 1, 2 중 하나이다.
        // 0 : 성공, 1 : fail, 2 : error이다.
        val result = proc.exitValue()
        if (result == 0) {
            Log.d("LOG_ping test 결과", "네트워크 연결 상태 양호")
        } else {
            Log.d("LOG_ping test 결과", "연결되어 있지 않습니다.")
        }
        return if (result == 0) true else false
    }

    fun isConnected(host: String, port: Int): Boolean {
        val soc = Socket()
        val adr = InetSocketAddress(host, port)
        var result = false
        try {
            soc.connect(adr, 1000)
            if (soc.isConnected == true) {
                result = true
            }
        } catch (e: IOException) {
            result = false
        }

        return result
    }

    fun highlight(message: String, vararg views: View) {
        ValueAnimator.ofObject(ArgbEvaluator(), Color.argb(150, 255, 0, 0), Color.TRANSPARENT).let {
            it.duration = 250
            it.repeatCount = 2
            it.addUpdateListener { animator ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    views.forEach { view ->
                        view.foreground = ColorDrawable(animator.animatedValue as Int)
                    }
                } else {
                    views.forEach { view ->
                        view.background = ColorDrawable(animator.animatedValue as Int)
                    }
                }
            }
            it.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
//                    Functions.MessageBox(context!!, message)
                    Toast.makeText(Emaintec.activity!!, message, Toast.LENGTH_SHORT).show()
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
            it.start()
        }
    }
    fun highlightGrid(message: String, vararg views: RecyclerView.ViewHolder) {
        ValueAnimator.ofObject(ArgbEvaluator(), Color.argb(150, 255, 0, 0), Color.TRANSPARENT).let {
            it.duration = 250
            it.repeatCount = 2
            it.addUpdateListener { animator ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    views.forEach { view ->
                        view.itemView.foreground = ColorDrawable(animator.animatedValue as Int)
                    }
                } else {
                    views.forEach { view ->
                        view.itemView.background = ColorDrawable(animator.animatedValue as Int)
                    }
                }
            }
            it.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    MessageBox(Emaintec.activity!!, message)
//                    Toast.makeText(Emaintec.activity!!, message, Toast.LENGTH_SHORT).show()
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
            it.start()
        }
    }

    fun addTextLabel(line: LinearLayout, label: String, contents: String,type:Int=0) {

        var text = TextView(Emaintec.activity)
        val p = LinearLayout.LayoutParams(
            Device.convertDpToPixel(80f, Emaintec.activity!!).toInt(),
            LinearLayout.LayoutParams.MATCH_PARENT,
            0f
        )
//        p.setMargins(30, 30, 30, 30);
        text.setLayoutParams(p);
        text.setBackgroundResource(R.drawable.bg_label_item)
        text.setPadding(10, 5, 10, 5)
//        text.setTextColor(Color.BLACK)
        text.setTextColor(ContextCompat.getColor(Emaintec.activity!!, R.color.colorListHeaderTxt))
        text.setTypeface(null, Typeface.BOLD);
        text.gravity = Gravity.CENTER
        text.text = label
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
        text.maxLines = 1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            text.setAutoSizeTextTypeUniformWithConfiguration(6, 14, 1, TypedValue.COMPLEX_UNIT_SP)
        }
        var text2 = TextView(Emaintec.activity)
        val p1 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            3f
        )
//        p.setMargins(30, 30, 30, 30);
        text2.setLayoutParams(p1);
//        text2.setBackgroundColor(color)
        when (type) {
            0 ->  text2.setBackgroundResource(R.drawable.bg_label_contents)
            1 ->  text2.setBackgroundResource(R.drawable.bg_label_contents1)
            2 ->  text2.setBackgroundResource(R.drawable.bg_label_contents2)
        }
        text2.setTextColor(ContextCompat.getColor(Emaintec.activity!!, R.color.colorListDetailTxt))
//        text2.setTextColor(Color.BLACK)
        text2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
        text2.setPadding(10, 5, 10, 5)
        text2.text = contents

        if (label.isNotEmpty() && contents.isNotEmpty()) {
            line.addView(text)
        }
        if (contents.isNotEmpty())
            line.addView(text2)
    }

    fun addTextLabel2(line: LinearLayout, label: String, contents: String,type:Int=0) {

        var text = TextView(Emaintec.activity)
        val p = LinearLayout.LayoutParams(
            Device.convertDpToPixel(80f, Emaintec.activity!!).toInt(),
            LinearLayout.LayoutParams.MATCH_PARENT
        )
//        p.setMargins(30, 30, 30, 30);
        text.setLayoutParams(p);
        text.setBackgroundResource(R.drawable.bg_label_item)
        text.setPadding(10, 5, 10, 5)
        text.setTextColor(ContextCompat.getColor(Emaintec.activity!!, R.color.colorListHeaderTxt))

        text.setTypeface(null, Typeface.BOLD);
        text.gravity = Gravity.CENTER
        text.text = label
        text.maxLines = 1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            text.setAutoSizeTextTypeUniformWithConfiguration(6, 14, 1, TypedValue.COMPLEX_UNIT_SP)
        }

        var text2 = TextView(Emaintec.activity)
        val p1 = LinearLayout.LayoutParams(
            Device.convertDpToPixel(80f, Emaintec.activity!!).toInt(),
            LinearLayout.LayoutParams.MATCH_PARENT,
            1F
        )
        val p2 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            1F
        )

//        p.setMargins(30, 30, 30, 30);

        when (type) {
            0 ->  text2.setBackgroundResource(R.drawable.bg_label_contents)
            1 ->  text2.setBackgroundResource(R.drawable.bg_label_contents1)
            2 ->  text2.setBackgroundResource(R.drawable.bg_label_contents2)
        }
//        text2.setTextColor(Color.BLACK)
        text2.setTextColor(ContextCompat.getColor(Emaintec.activity!!, R.color.colorListDetailTxt))
        text2.setPadding(10, 5, 10, 5)
        text2.text = contents

        if (label.isNotEmpty() && contents.isNotEmpty()) {
            text2.setLayoutParams(p1)
            line.addView(text)
        } else {
            text2.setLayoutParams(p2)
            text2.minWidth = Device.convertDpToPixel(100f, Emaintec.activity!!).toInt()
        }
        if (contents.isNotEmpty())
            line.addView(text2)
    }
    fun restartApp(context: Context) {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent!!.getComponent()
        val mainIntent: Intent = makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        System.exit(0)
    }

    object UpdateApp {
         fun Autoupdate(TYPE: String = "XML"): Boolean {
            var bReturn = false
            val gson = Gson()
            val jsonobject = JsonObject()
            val array = JsonArray()
//        val TYPE = "DB" // XML
            jsonobject.addProperty("VERSION", Device.versionName)
            jsonobject.addProperty("DEVICE", Build.MODEL)
            jsonobject.addProperty("TYPE", TYPE)
            array.add(jsonobject)
            gson.toJson(array)

            val jsonData = gson.toJson(array)
            CoroutineScope(Dispatchers.Default).launch {
                val bResult =
                    getNewVersion({ success: Boolean, msg: String ->
                        launch(Dispatchers.Main) {
                            if (success&&msg.isNotBlank()) {
                                MessageBoxOk(
                                    Emaintec.activity!!,
                                    "새로운 버젼이 있습니다. 확인을 클릭하여 업데이트 하세요.",
                                    "업데이트",
                                    "",
                                    DialogInterface.OnClickListener { _, _ ->
                                        CoroutineScope(Dispatchers.Default).launch {
                                            if (TYPE.equals("XML")) {
                                                downFile(action = { success1: Boolean, msg: String ->
                                                    launch(Dispatchers.Main) {
                                                        if (success1) {
                                                            // 파일 다운로드 종료 후 다운받은 파일을 실행시킨다.
                                                            if (Build.VERSION.SDK_INT < 24) {
                                                                val intent =
                                                                    Intent(Intent.ACTION_VIEW)
                                                                intent.setDataAndType(
                                                                    Uri.fromFile(File(msg)),
                                                                    "application/vnd.android.package-archive"
                                                                )
                                                                intent.flags =
                                                                    Intent.FLAG_ACTIVITY_NEW_TASK // without this flag android returned a intent error!
                                                                Emaintec.activity!!.startActivity(
                                                                    intent
                                                                )
                                                            } else {
                                                                val instrallUri =
                                                                    FileProvider.getUriForFile(
                                                                        Emaintec.activity!!,
                                                                        BuildConfig.APPLICATION_ID + ".fileprovider",
                                                                        File(msg)
                                                                    )
                                                                val intent =
                                                                    Intent(Intent.ACTION_VIEW)
                                                                intent.setDataAndType(
                                                                    instrallUri,
                                                                    "application/vnd.android.package-archive"
                                                                )
                                                                intent.flags =
                                                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                                                                Emaintec.activity!!.startActivity(
                                                                    intent
                                                                )
                                                            }
                                                        } else {
                                                            bReturn = false
                                                        }
                                                    }.join()
                                                }, jsondata = msg)
                                            } else if (TYPE.equals("DB")) {
                                                launch(Dispatchers.Main) {
                                                    FileHelper.instance.DownloadFile2(
                                                        Functions.FilePath.getPath().absolutePath + "/Emaintec/install.apk",
                                                        msg
                                                    )
                                                }
                                            }
                                        }

                                    }
                                )

                            } else {
                                bReturn = false
                            }
                        }.join()
                    }, jsonData)
            }
            return bReturn
        }

        @WorkerThread
        suspend fun getNewVersion(
            action: suspend (Boolean, String) -> Unit,
            jsondata: String? = null
        ) {
            NetworkSync(
                TransmitterJson(
                    url = Data.instance.url + "/ct_broker.jsp",
                    action = "ct_biz.DeployApp.IGetUpdatedInfo",
                    jsondata = jsondata
                ), ReceiverJson()
            ).get(onSuccessed = {
                var jsonObject = it.receiver.resultData!!
                if (!jsonObject.getBoolean("success")) {
                    action(false, jsonObject.getString("message").toString())
                    return@get
                }
                val jsonArray = jsonObject.getJSONArray("data")
                for (n in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(0)
                    //                    strVersion = obj.get("VERSION").toString()
                    //                    strPath = obj.get("PATH").toString()
                    action(true, obj.get("PATH").toString())
                }

            }, onFailed = {
                val jsonObject = JSONObject(it.receiver.errorData!!)
                action(false, jsonObject.get("message").toString())
            })
        }

        @WorkerThread
        suspend fun downFile(
            action: suspend (Boolean, String) -> Unit,
            jsondata: String? = null,
            result: String? = null
        ) {
            NetworkSync(
                TransmitterString(jsondata!!),
                ReceiverFile(Functions.FilePath.getPath().absolutePath + "/install.apk")
            ).get(onSuccessed = {
                val jsonObject = JSONObject(it.receiver.resultData!!)
                if ((jsonObject).getBoolean("success")) {
                    action(
                        true,
                        Functions.FilePath.getPath().absolutePath + "/install.apk"
                    )
                } else {
                    action(false, it.receiver.errorData + "")
                }
            }, onFailed = {
                action(false, it.receiver.errorData + "파일다운로드 실패")
            })
        }
    }
    object PushUtils {
        private var mWakeLock: PowerManager.WakeLock? = null
        @SuppressLint("InvalidWakeLockTag")
        fun acquireWakeLock(context: Context) {
            val wakeLock: PowerManager.WakeLock =
                (context.getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                    newWakeLock(PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE, "MyApp::MyWakelockTag").apply {
                        acquire(3000)
                    }
                }
        }

        fun releaseWakeLock() {
            if (mWakeLock != null) {
                mWakeLock!!.release()
                mWakeLock = null
            }
        }
    }
}
