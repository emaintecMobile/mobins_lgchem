package com.emaintec

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.emaintec.external.zxing.IntentIntegrator
import com.emaintec.lib.adapter.Adapter_Pager
import com.emaintec.lib.base.Emaintec
import com.emaintec.mobins.R


class Activity_ViewPager : Activity_Base() {
    private var _indexPage = 0
    private var _listener: OnBackPressedListener? = null
    var orientationCanActivityReStart :Boolean = false
    interface OnBackPressedListener {
        fun onBack()
    }
    fun setOnBackPressedListener(listener: OnBackPressedListener?) {
        _listener = listener
    }
    override fun onBackPressed()
    {
        if(_listener != null)
          _listener!!.onBack()
        else{
            super.onBackPressed()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_view)
        //촬영 qr 바코드
        run{
            findViewById<View>(R.id.imageButton_Scan).visibility = View.VISIBLE
            findViewById<View>(R.id.imageButton_Scan).setOnClickListener {
                 startActivityForResult(IntentIntegrator.createScanIntent(this@Activity_ViewPager), IntentIntegrator.REQUEST_CODE)
            }
        }
        _pagerAdapter = Adapter_Pager(supportFragmentManager)

        val Fragment = intent.getStringExtra("menu")
        val title = intent.getStringExtra("title")

        (findViewById<View>(R.id.textView_Title) as TextView).text = title
        // if ("Fragment_Return".equals(Fragment)) {
        try {
            _pagerAdapter!!.addFragment(Class.forName("com.emaintec.$Fragment").newInstance() as Fragment)

        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        }

        _viewPager = findViewById(R.id.pager)
        _viewPager!!.adapter = _pagerAdapter
        Emaintec.activity = (this)

        //        Betools.setFragment(_pagerAdapter.getItem(0));
        findViewById<View>(R.id.imageButton_Home).setOnClickListener {
                //getSupportFragmentManager().beginTransaction().remove(_pagerAdapter.getItem(0)).commit();
                //getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                finish()
        }
//        (_pagerAdapter!!.getItem(0) as Fragment_Base).updateUI()
    }

    fun addFragment(fragment: Fragment_Base)
    {
        _pagerAdapter!!.addFragment(fragment)
//        _viewPager!!.setPagingEnabled(false)
        _viewPager!!.adapter = _pagerAdapter
    }
    fun removeFragment(view : ViewGroup, i:Int)
    {

        //getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
         _pagerAdapter!!.destroyItem(view,i,_pagerAdapter!!.getItem(i))
        getSupportFragmentManager().beginTransaction().remove(_pagerAdapter!!.getItem(i)).commit();
        _pagerAdapter!!.removeFragment(_pagerAdapter!!.getItem(i));
        _pagerAdapter!!.notifyDataSetChanged()
        _viewPager!!.adapter = _pagerAdapter
    }
    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {

        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun movePage(position: Int) {

        _indexPage = position
        _viewPager!!.currentItem = _indexPage
        (_pagerAdapter!!.getItem(_indexPage) as Fragment_Base).updateUI()

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 아래 부분에 가로/세로 모드 별로 리소스 재정의나 행동들을 해주면 된다.
        Emaintec.activityOrientation = newConfig.orientation
        if (orientationCanActivityReStart){
            if (newConfig.orientation === Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation === Configuration.ORIENTATION_PORTRAIT) {
//                this.recreate()
            }
        }
    }
    companion object {
        private var _viewPager: ViewPager? = null
        private var _pagerAdapter: Adapter_Pager? = null
    }

}