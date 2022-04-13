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

    private var centerX = 0F
    private var centerY = 0F

    // длина стрелок относительно радиуса циферблата
    private var secHandLenPercents = 0.85F
    private var minHandLenPercents = 0.8F
    private var hourHandLenPercents = 0.5F

    private var clockFaceRadius = 0F
    private var clockLabelLenght = 0F
    private var secHandRadius = 0F
    private var minHandRadius = 0F
    private var hourHandRadius = 0F

    // длина задней части стрелки
    private var clockHandBackLength = 0F

    // толщина линии циферблата и минутной стрелки
    private var defPaintThickness = 15F
    private var secHandThickness = 10F
    private var hourHandThickness = 25F

    private var clockFaceColor = Color.CYAN
    private var secHandColor = Color.CYAN
    private var minHandColor = Color.CYAN
    private var hourHandColor = Color.CYAN

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = defPaintThickness
        style = Paint.Style.STROKE
        color = clockFaceColor
    }

    private var calendar: Calendar = GregorianCalendar.getInstance()

    inner class TimeViewUpdateTask : TimerTask() {
        override fun run() {
            calendar = GregorianCalendar.getInstance()
            invalidate()
        }
    }

    private val timer = Timer()
    private val task = TimeViewUpdateTask()

    init {
        timer.schedule(task, 50, 50)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthUnSpec = 160
        val heightUnSpec = 160

        var widthToSet = MeasureSpec.getSize(widthMeasureSpec)
        var heightToSet = MeasureSpec.getSize(heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            widthToSet = widthUnSpec
        }
        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightToSet = heightUnSpec
        }
        Log.d(LOG, "widthMode = $widthMode, widthToSet = $widthToSet,\n heightMode = $heightMode, heightToSet = $heightToSet")

        setMeasuredDimension(widthToSet, heightToSet)

        centerX = measuredWidth / 2F
        centerY = measuredHeight / 2F
        val minSide = if (measuredWidth > measuredHeight) {
            measuredHeight
        } else measuredWidth
        clockFaceRadius = 0.5F * minSide * 0.95F
        clockLabelLenght = clockFaceRadius * 0.1F
        secHandRadius = clockFaceRadius * secHandLenPercents
        minHandRadius = clockFaceRadius * minHandLenPercents
        hourHandRadius = clockFaceRadius * hourHandLenPercents
        clockHandBackLength = hourHandRadius * 0.5F
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        Log.d(LOG, "onDraw")

        canvas?.let {
            drawClockFace(canvas)
            drawSecHand(canvas, calendar)
            drawMinHand(canvas, calendar)
            drawHourHand(canvas, calendar)
        }
    }

    private fun drawClockFace(canvas: Canvas) {
        canvas.save()
        paint.apply {
            strokeWidth = defPaintThickness
            color = clockFaceColor
        }
        canvas.drawCircle(centerX, centerY, clockFaceRadius, paint)
        for (i in 0..11) {
            drawClockLabel(canvas)
            canvas.rotate(30F, centerX, centerY)
        }
        canvas.restore()
    }

    private fun drawClockLabel(canvas: Canvas) {
        canvas.drawLine(
            centerX,
            centerY - clockFaceRadius,
            centerX,
            centerY - clockFaceRadius + clockLabelLenght,
            paint
        )
    }

    private fun drawSecHand(canvas: Canvas, time: Calendar) {
        val secs = time.get(Calendar.SECOND)
        val millies = time.get(Calendar.MILLISECOND)
        val angle = getSecHandRotateAngle(secs, millies)
//        Log.d(LOG, "drawSecHand: secs = $secs, millies = $millies, angle = $angle")
        paint.apply {
            strokeWidth = secHandThickness
            color = secHandColor
        }
        canvas.save()
        canvas.rotate(angle, centerX, centerY)
        canvas.drawLine(
            centerX,
            centerY - secHandRadius,
            centerX,
            centerY + clockHandBackLength,
            paint
        )
        canvas.restore()
    }

    private fun drawMinHand(canvas: Canvas, time: Calendar) {
        val mins = time.get(Calendar.MINUTE)
        val secs = time.get(Calendar.SECOND)
        val angle = getMinHandRotateAngle(mins, secs)
//        Log.d(LOG, "drawSecHand: mins = $mins, secs = $secs, angle = $angle")
        paint.apply {
            strokeWidth = defPaintThickness
            color = minHandColor
        }
        canvas.save()
        canvas.rotate(angle, centerX, centerY)
        canvas.drawLine(
            centerX,
            centerY - minHandRadius,
            centerX,
            centerY + clockHandBackLength,
            paint
        )
        canvas.restore()
    }

    private fun drawHourHand(canvas: Canvas, time: Calendar) {
        val hours = time.get(Calendar.HOUR)
        val mins = time.get(Calendar.MINUTE)
        val angle = getHourHandRotateAngle(hours, mins)
//        Log.d(LOG, "drawSecHand: hours = $hours, mins = $mins, angle = $angle")
        paint.apply {
            strokeWidth = hourHandThickness
            color = hourHandColor
        }
        canvas.save()
        canvas.rotate(angle, centerX, centerY)
        canvas.drawLine(
            centerX,
            centerY - hourHandRadius,
            centerX,
            centerY + clockHandBackLength,
            paint
        )
        canvas.restore()
    }

    // функция для вычисления угла поворота секундной стрелки с учетом влияния миллисекунд
    private fun getSecHandRotateAngle(secs: Int, millies: Int) =
        (millies / 1000F + secs) * 6

    // функция для вычисления угла поворота минутной стрелки с учетом влияния секунд
    private fun getMinHandRotateAngle(mins: Int, secs: Int) =
        6F * mins + 0.1F * secs

    // функция для вычисления угла поворота часовой стрелки с учетом влияния минут
    private fun getHourHandRotateAngle(hours: Int, mins: Int) =
        hours * 30F + mins * 0.5F
}