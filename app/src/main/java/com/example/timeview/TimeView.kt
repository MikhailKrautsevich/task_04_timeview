package com.example.timeview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.util.*

class TimeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val LOG = "TimeView.LOG"
    }

    private var mCenterX = 0F
    private var mCenterY = 0F
    private var mClockFaceRadius = 0F
    private var mClockLabelLenght = 0F
    private var mSecHandRadius = 0F
    private var mMinHandRadius = 0F
    private var mHourHandRadius = 0F

    // длина задней части стрелки
    private var mClockHandBackLength = 0F

    private var mDefPaintThickness = 15F
    private var mSecHandThickness = 10F
    private var mHourHandThickness = 25F

    private val mDefPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = mDefPaintThickness
        style = Paint.Style.STROKE
        color = Color.CYAN
    }

    private val mSecPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = mSecHandThickness
        style = Paint.Style.STROKE
        color = Color.CYAN
    }

    private val mHourPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = mHourHandThickness
        style = Paint.Style.STROKE
        color = Color.CYAN
    }

    private var mTime: Calendar = GregorianCalendar.getInstance()

    inner class TimeViewUpdateTask(): TimerTask(){
        override fun run() {
            mTime = GregorianCalendar.getInstance()
            invalidate()
        }
    }

    private val sTimer = Timer()
    private val sTask = TimeViewUpdateTask()

    init {
        sTimer.schedule(sTask, 100, 50)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        Log.d(LOG, "onMeasure")

        mCenterX = measuredWidth / 2F
        mCenterY = measuredHeight / 2F
        mClockFaceRadius = if (width > height) {
            mCenterY * 0.95F
        } else mCenterX * 0.95F
        mClockLabelLenght = mClockFaceRadius * 0.1F
        mSecHandRadius = mClockFaceRadius * 0.85F
        mMinHandRadius = mClockFaceRadius * 0.8F
        mHourHandRadius = mClockFaceRadius * 0.5F
        mClockHandBackLength = mHourHandRadius * 0.5F
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        Log.d(LOG, "onDraw")

        canvas?.let {
            canvas.save()
            drawClockFace(canvas)
            for (i in 0..11) {
                drawClockLabel(canvas)
                canvas.rotate(30F, mCenterX, mCenterY)
            }
            canvas.restore()
            drawSecHand(canvas, mTime)
            drawMinHand(canvas, mTime)
            drawHourHand(canvas, mTime)
        }
    }

    private fun drawClockFace(canvas: Canvas) {
        canvas.drawCircle(mCenterX, mCenterY, mClockFaceRadius, mDefPaint)
    }

    private fun drawClockLabel(canvas: Canvas) {
        canvas.drawLine(
            mCenterX,
            mCenterY - mClockFaceRadius,
            mCenterX,
            mCenterY - mClockFaceRadius + mClockLabelLenght,
            mDefPaint
        )
    }

    private fun drawSecHand(canvas: Canvas, time: Calendar) {
        val secs = time.get(Calendar.SECOND)
        val millies = time.get(Calendar.MILLISECOND)
        val angle = getRotateAngle(secs, millies)
        Log.d(LOG, "drawSecHand: secs = $secs, millies = $millies, angle = $angle")
        canvas.save()
        canvas.rotate(angle, mCenterX, mCenterY)
        canvas.drawLine(
            mCenterX,
            mCenterY - mSecHandRadius,
            mCenterX,
            mCenterY + mClockHandBackLength,
            mSecPaint
        )
        canvas.restore()
    }

    private fun drawMinHand(canvas: Canvas, time: Calendar) {
        val mins = time.get(Calendar.MINUTE)
        val secs = time.get(Calendar.SECOND)
        val angle = getRotateAngle(mins, secs)
        Log.d(LOG, "drawSecHand: mins = $mins, secs = $secs, angle = $angle")
        canvas.save()
        canvas.rotate(angle, mCenterX, mCenterY)
        canvas.drawLine(
            mCenterX,
            mCenterY - mMinHandRadius,
            mCenterX,
            mCenterY + mClockHandBackLength,
            mDefPaint
        )
        canvas.restore()
    }

    private fun drawHourHand(canvas: Canvas, time: Calendar) {
        val hours = time.get(Calendar.HOUR)
        val mins = time.get(Calendar.MINUTE)
        val angle = getHourHandRotateAngle(hours, mins)
        Log.d(LOG, "drawSecHand: hours = $hours, mins = $mins, angle = $angle")
        canvas.save()
        canvas.rotate(angle, mCenterX, mCenterY)
        canvas.drawLine(
            mCenterX,
            mCenterY - mHourHandRadius,
            mCenterX,
            mCenterY + mClockHandBackLength,
            mHourPaint
        )
        canvas.restore()
    }

    // функция для вычисления угла поворота минутной и секундной стрелки с учетом влияния секунд и
    // миллисекунд соответственно
    private fun getRotateAngle(mainParam: Int, auxiliaryParam: Int) =
        (auxiliaryParam / 1000F + mainParam) * 6

    // функция для вычисления угла поворота часовой стрелки с учетом влияния минут
    private fun getHourHandRotateAngle(hours: Int, mins: Int) =
        hours * 30F + mins * 0.5F
}