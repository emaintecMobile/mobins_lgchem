package com.emaintec.lib.ctrl

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import java.io.ByteArrayOutputStream


class PaintView : View {

    private val _paint = Paint()
    private var _path = Path()    // 자취를 저장할 객체
    private var _onDrawingListener: OnDrawingListener? = null
    var isDrawingEnabled = true
    private var _width = 0
    private var _height = 0
    var imageViewWidth : Int ? = null
    var imageViewHight : Int ? = null
    private var penColor : Int = Color.BLACK
    private var DEFAULT_STROKE_WIDTH = 10.0f
    private val paths = ArrayList<Path>()
    private val undonePaths = ArrayList<Path>()
    var pathColor :HashMap<Path,Paint> = HashMap<Path,Paint>()
    val bitmap: Bitmap?
        get() {
//            if (_path.isEmpty) {
//                return null
//            }




            var bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            background?.draw(canvas)
//            canvas.drawPath(_path, _paint)
            for (p in paths) {
                canvas.drawPath(p,  pathColor!![p] as Paint)
            }
            if (_width > 0) {
                bitmap = Bitmap.createScaledBitmap(bitmap, _width, _height, true)
            }
            return bitmap
        }

    var imageFileData: ByteArray?
        @Deprecated("use {@link #getBitmap()} instead")
        get() {
            val bitmap = bitmap ?: return null
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            return byteArrayOutputStream.toByteArray()
        }
        set(fileData) {
            if (null == fileData) {
                return
            }
            _path.reset()
            val bitmap = BitmapFactory.decodeByteArray(fileData, 0, fileData.size)
            val pixels = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                resizeView(bitmap)
                val drawable = BitmapDrawable(Resources.getSystem(), bitmap)
                drawable.gravity = Gravity.FILL
                background = drawable
            } else {
                setBackgroundDrawable(BitmapDrawable(Resources.getSystem(), bitmap) as Drawable)
            }
            _width = bitmap.width
            _height = bitmap.height
        }
/*

 * 비트맵(Bitmap) 이미지의 가로, 세로 이미지를 리사이징
 * @param bmpSource 원본 Bitmap 객체
 * @param maxResolution 제한 해상도
 * @return 리사이즈된 이미지 Bitmap 객체

 */

    fun resizeView(bmpSource: Bitmap) {

        val iWidth = bmpSource.width      //비트맵이미지의 넓이
        val iHeight = bmpSource.height     //비트맵이미지의 높이

        if(iWidth>iHeight) {
            layoutParams.width = imageViewWidth!!
            layoutParams.height =(imageViewWidth!!.toFloat() * (iHeight.toFloat() / iWidth.toFloat())).toInt()
        }
        else{
            layoutParams.height =imageViewHight!!
            layoutParams.width = (imageViewHight!!.toFloat() * (iWidth.toFloat() / iHeight.toFloat())).toInt()
        }


    }


    interface OnDrawingListener {
        fun drawingStarted()
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas) { // 화면을 그려주는 메서드

//        canvas.drawPath(_path, _paint) // 저장된 _path 를 그려라
        for (p in paths) {
            canvas.drawPath(p,  pathColor!![p] as Paint)
        }
        canvas.drawPath(_path, _paint)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (!isDrawingEnabled) {
            return true
        }

        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                undonePaths.clear()
                _path.reset()
                pathColor.put(_path,Paint(_paint))
                _path.moveTo(x, y) // 자취에 그리지 말고 위치만 이동해라
            }
            MotionEvent.ACTION_MOVE -> {
                _path.lineTo(x, y) // 자취에 선을 그려라
            }
            MotionEvent.ACTION_UP -> {
                paths.add(_path)
                _path = Path()
            }
        }

        invalidate() // 화면을 다시그려라

        if (null != _onDrawingListener) {
            _onDrawingListener!!.drawingStarted()
        }

        return true
    }

    private fun init() {
        _paint.style = Paint.Style.STROKE                    // 선이 그려지도록
        _paint.strokeWidth = DEFAULT_STROKE_WIDTH        // 선의 굵기 지정
        _paint.color = penColor
    }
    fun onUndo() {
        if (paths.size > 0) {
            undonePaths.add(paths.removeAt(paths.size - 1))
            invalidate()
        } else {
        }
        //toast the user
    }

    fun onRedo() {
        if (undonePaths.size > 0) {
            paths.add(undonePaths.removeAt(undonePaths.size - 1))
            invalidate()
        } else {
        }
        //toast the user
    }

    fun clean() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            background = null
        } else {
            setBackgroundDrawable(null)
        }
        _path.reset()
        paths.removeAll(paths)
        invalidate()                                            // 화면을 다시그려라
    }

    fun clear() {
        _path.reset()
        //paths.removeAll(paths)
        while(paths.size > 0)
        undonePaths.add(paths.removeAt(paths.size - 1))
        invalidate()                                            // 화면을 다시그려라
    }

    fun setOnDrawingListener(listener: OnDrawingListener) {
        _onDrawingListener = listener
    }

    fun setStrokeWidth(width: Float) {
        _paint.strokeWidth = width                // 선의 굵기 지정
    }
    fun setStrokColor(penColor: Int) {
      //  background = BitmapDrawable(Resources.getSystem(), bitmap)
      //  _path.reset()
        _paint.color = penColor               // 선의 칼라
    }



}
