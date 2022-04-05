package com.emaintec.common

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.view.*
import android.widget.MediaController
import android.widget.SeekBar
import com.emaintec.Define
import com.emaintec.common.model.fileModel
import com.emaintec.lib.ctrl.PaintView
import com.emaintec.lib.util.setOneClickListener
import com.emaintec.mobins.R
import com.emaintec.mobins.databinding.CommonImageViewerBinding
import java.io.File

class imageViewer(context: Context,themeResId:Int = 0) : Dialog(context,themeResId) {
    val binding by lazy { CommonImageViewerBinding.inflate(layoutInflater) }
    lateinit var paintView :com.emaintec.lib.ctrl.PaintView
    companion object {
        const val RETURN_VALUE_NOTHING = 0
        const val RETURN_VALUE_SAVE = 1
        const val RETURN_VALUE_DELETE = 2
    }
    private var _mediaController: MediaController? = null

    private val _updateHandler: Handler by lazy {
        Handler()
    }
    private fun runVideoTimer() {
        _updateHandler.postDelayed(::showVideoTimer, 100)
    }
    private fun stopVideoTimer() {
        _updateHandler.removeCallbacksAndMessages(null)
    }
    private fun showVideoTimer() {
        _mediaController!!.show()
        runVideoTimer()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopVideoTimer()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init(file: fileModel, onResultListener: ((Int, fileModel) -> Unit)) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        paintView = findViewById(R.id.paintView)
        setCancelable(false)
        setTitle("사진 촬영")
//        getWindow()!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        val mediaController = MediaController(context)
        mediaController.findFocus()
        mediaController.isEnabled = true
        binding.videoView.setMediaController(mediaController)
        (mediaController.parent as ViewGroup).removeView(mediaController)
        binding.videoViewWrapper.addView(mediaController)
        _mediaController = mediaController


        binding.zoom.setScrollEnabled(false)

        binding.checkBoxPaint.setOnCheckedChangeListener { buttonView, isChecked ->
            paintView.isDrawingEnabled = isChecked
            binding.zoom.setHasClickableChildren(isChecked)
            binding.zoom.isFocusable = !isChecked
            binding.zoom.setZoomEnabled(!isChecked)
            binding.zoom.setScrollEnabled(!isChecked)
            binding.zoom.isEnabled =  !isChecked
            binding.zoom.setFlingEnabled( !isChecked)
        }
        paintView.setOnDrawingListener(object : PaintView.OnDrawingListener {
            override fun drawingStarted() {
                binding.buttonClear.isEnabled = true
            }
        })
        binding.buttonDelete.setOneClickListener {
            binding.videoView.stopPlayback()
            stopVideoTimer()

            onResultListener(RETURN_VALUE_DELETE, file)
            this@imageViewer.dismiss()
        }
        binding.buttonClear.setOneClickListener {
            paintView.clear()
            binding.buttonClear.isEnabled = false
        }
        binding.buttonSave.setOneClickListener {
            if (binding.buttonClear.isEnabled) {
                file.DATA_RAW = paintView.imageFileData
                onResultListener(RETURN_VALUE_SAVE, file)
            } else {
                //onResultListener(RETURN_VALUE_NOTHING, file)
            }
            this@imageViewer.dismiss()
        }
        binding.customLayoutTitle.imageButton.setOneClickListener {
            onResultListener(RETURN_VALUE_NOTHING, file)
            this@imageViewer.dismiss()
        }
        binding.buttonClose.setOneClickListener {
            onResultListener(RETURN_VALUE_NOTHING, file)
            this@imageViewer.dismiss()
        }


        setOnKeyListener { _, keyCode, event ->
            if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.action == KeyEvent.ACTION_UP)) {
                onResultListener(RETURN_VALUE_NOTHING, file)
                this@imageViewer.dismiss()
            }
            false
        }
        initButtonPaint()
    }
    fun initButtonPaint(){
        binding.sliderPenWeight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(v: SeekBar?, progress: Int, p2: Boolean) {
                paintView.setStrokeWidth(progress.toFloat())
            }
            override fun onStartTrackingTouch(v: SeekBar?) {

            }
            override fun onStopTrackingTouch(v: SeekBar?) {

            }
        })
        binding.buttonBlack.setOneClickListener {
            paintView.setStrokColor(  Color.BLACK)
        }
        binding.buttonBlue.setOneClickListener {
            paintView.setStrokColor(  Color.BLUE)
        }
        binding.buttonRed.setOneClickListener {
            paintView.setStrokColor(  Color.RED)
        }
        binding.buttonYellow.setOneClickListener {
            paintView.setStrokColor(  Color.YELLOW)
        }
        binding.buttonWhite.setOneClickListener {
            paintView.setStrokColor(  Color.WHITE)
        }
        binding.buttonUndo.setOneClickListener {
            paintView.onUndo()
        }
        binding.buttonRedo.setOneClickListener {
            paintView.onRedo()
        }
    }
    fun initPhoto(file: fileModel, onResultListener: ((Int, fileModel) -> Unit),isEnable : Boolean = true): imageViewer {

        init(file, onResultListener)

        setOnShowListener {
            paintView.imageViewWidth = binding.zoom.width //window!!.attributes.width//window!!.decorView.width
            paintView.imageViewHight = binding.zoom.height //window!!.attributes.width//window!!.decorView.width
            paintView.imageFileData = file.DATA_RAW
        }
        paintView.visibility = View.VISIBLE
        binding.buttonClear.isEnabled = false

        paintView.isDrawingEnabled = false
        binding.zoom.setHasClickableChildren(false)
        binding.zoom.isFocusable = true
        binding.zoom.setZoomEnabled(true)
        binding.zoom.setScrollEnabled(true)
        binding.zoom.isEnabled =  true
        binding.zoom.setFlingEnabled( true)

        if(!isEnable)
        {
            binding.frameControl.visibility = View.GONE
            binding.layoutButtonGroup.visibility = View.GONE
        }
        return this
    }

    fun initVideo(file: fileModel, makeVideoFilePath: ((Int) -> String), onResultListener: ((Int, fileModel) -> Unit)): imageViewer {

        init(file, onResultListener)
        binding.zoom.visibility = View.GONE
        binding.layoutVideo.visibility = View.VISIBLE
        binding.videoView.visibility = View.VISIBLE
        binding.buttonClear.isEnabled = false

        runVideoTimer()

        val fileVideo: File
        if (file.FILE_NO == null) {
            fileVideo = File(makeVideoFilePath(file.FILE_ID))
        } else {
            fileVideo = File(makeVideoFilePath(Integer.parseInt(file.FILE_NO!!)))
        }

        if (fileVideo.exists()) {
            binding.videoView.setVideoURI(Uri.parse(fileVideo.absolutePath))
        } else {
            binding.videoView.setVideoURI(Uri.parse(Define.URL_DEFAULT + File.separator + "upload" + File.separator + file.FILE_NO + ".mp4"))
        }

        binding.videoView.setOnPreparedListener {
            it.setScreenOnWhilePlaying(true)
            binding.videoView.seekTo(0)
            binding.videoView.start()
            /*videoView.postDelayed({
                videoView.pause()
            }, 100)*/
        }

        return this
    }
}
