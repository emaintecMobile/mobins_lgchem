package com.emaintec.mobins


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment.STYLE_NORMAL
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.emaintec.Activity_Base
import com.emaintec.Data
import com.emaintec.Define
import com.emaintec.datasync.dataSync
import com.emaintec.lib.base.Emaintec
import com.emaintec.lib.db.JsonHelper
import com.emaintec.lib.device.Device
import com.emaintec.lib.util.setOneClickListener
import com.emaintec.lib.version.Html
import com.emaintec.main.main_Fragment
import com.emaintec.mobins.databinding.ActivityMainBinding
import com.emaintec.mobins.databinding.ActivityMainMenuBinding
import com.google.gson.Gson
import org.json.JSONObject
import java.util.*

class Activity_Main : Activity_Base() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    lateinit var bindingMenu : ActivityMainMenuBinding
    var foregroundServiceIntent: Intent? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val Fragment = intent.getStringExtra("menu")

        setContentView(binding.root ) // setContentView(R.layout.activity_main)

        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        Emaintec.application = this.application;
        Emaintec.activity = this
        viewPager.adapter = CustomAdapter(supportFragmentManager)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                addBottomDots(position, viewPager.adapter!!.count)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })



//        //	toolbar.setTitle(Data.getInstance().getLoginUserNm());
        if(!Define.OFFLINE) {
            if(null == UndeadService.serviceIntent) {
                foregroundServiceIntent = Intent(this, UndeadService::class.java)
                startService(foregroundServiceIntent)
                //Toast.makeText(getApplicationContext(), "start service", Toast.LENGTH_LONG).show()
            } else {
                foregroundServiceIntent = UndeadService.serviceIntent
                //Toast.makeText(getApplicationContext(), "already", Toast.LENGTH_LONG).show()
            }
        }
        Handler().postDelayed({ initButtons() }, 800) // ???????????? ????????? ?????? ?????? ?????? (db??? write??? ?????? ??????)

    }
    override fun onUserInteraction() {
        super.onUserInteraction()
    }
    private var backKeyPressedTime: Long = 0
    override fun onBackPressed() {

// ?????? ???????????? ????????? ????????? ???????????? ???????????? ?????? ??????
        // super.onBackPressed();

        // ??????????????? ???????????? ????????? ????????? ????????? 2?????? ?????? ??????????????? ?????? ???
        // ??????????????? ???????????? ????????? ????????? ????????? 2?????? ???????????? Toast Show
        // 2000 milliseconds = 2 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
             Toast.makeText(this, "\'??????\' ????????? ?????? ??? ???????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
            return
        }
        // ??????????????? ???????????? ????????? ????????? ????????? 2?????? ?????? ??????????????? ?????? ???
        // ??????????????? ???????????? ????????? ????????? ????????? 2?????? ????????? ???????????? ??????
        // ?????? ????????? Toast ??????
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            startActivity(Intent(this.baseContext, Activity_Login::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        val viewPager = findViewById<ViewPager>(R.id.viewPager)
//        val adapter = viewPager.adapter as CustomAdapter?
//        (adapter!!.getItem(0) as main_Fragment).updateUI()
    }



    private fun initButtons() {
//        val appBarLayout = findViewById<View>(R.id.app_bar) as AppBarLayout
//        appBarLayout.setExpanded(true)

        val screenSize = Point()
        windowManager.defaultDisplay.getSize(screenSize)
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        (viewPager.adapter as CustomAdapter).removeAll()
        (viewPager.adapter as CustomAdapter).addItem(main_Fragment())

        binding.imageButtonSetting.setOneClickListener {
                startActivity(Intent().setClassName(this@Activity_Main.baseContext, "com.emaintec.mobins" + "." + "Activity_Setup"))
                overridePendingTransition(0, 0)
            }


        //???????????? ?????????
        binding.imageButtonMenu.setOneClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this@Activity_Main)

            // ????????????
            alertDialogBuilder.setTitle(
                this@Activity_Main.applicationInfo.loadLabel(this@Activity_Main.packageManager)
                    .toString()
            )

            alertDialogBuilder
                .setMessage("???????????? ???????????????????")
                .setCancelable(true)
                .setPositiveButton("????????????") { dialog, which ->
                    startActivity(Intent(this.baseContext, Activity_Login::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                }
                .setNegativeButton("??????", null)

            // ??????????????? ??????
            val alertDialog = alertDialogBuilder.create()

            // ??????????????? ????????????
            alertDialog.show()
        }
    }
    fun Button_Click(menu: Menu) {
        try {
            val intent = Intent(
                this@Activity_Main.baseContext,
                Class.forName("com.emaintec" + "." + menu.MENU_CLASS)
            )
            intent.putExtra("menu", menu.MENU_SUB_CLASS)
            intent.putExtra("title", menu.MENU_NAME)
            startActivityForResult(intent, Integer.parseInt(menu.MENU_SEQ))
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }
    private fun addBottomDots(currentPage: Int, maxPage: Int) {
        val linearLayout = findViewById<LinearLayout>(R.id.layout_Dots)
        linearLayout.removeAllViews()

        for (i in 0 until maxPage) {
            val textView = TextView(this.applicationContext)
            textView.text = Html.fromHtml("&#8226;")
            textView.textSize = 35f
            textView.setTextColor(Color.argb(if (i == currentPage) 255 else 100, 255, 255, 255))
            textView.setShadowLayer(4.0f, 0.0f, 0.0f, Color.argb(255, 0, 0, 0))
            linearLayout.addView(textView)
        }
    }


    private inner class CustomAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val _arrayList = ArrayList<Fragment>()
        override fun getItem(position: Int): Fragment {
            return _arrayList[position]
        }
        override fun getCount(): Int {
            return _arrayList.size
        }
        fun addItem(fragment: Fragment) {
            _arrayList.add(fragment)
            notifyDataSetChanged()
        }

        fun removeAll() {
            _arrayList.clear()
            notifyDataSetChanged()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}

class Menu {
    var MENU_ID = ""
    var MENU_SEQ = ""
    var MENU_NAME = ""
    var MENU_SUB_CLASS = ""
    var MENU_CLASS: String? = null
    var MENU_STATUS = ""
}