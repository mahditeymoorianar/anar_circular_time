package com.teymoorianar.anar_circular_time

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import java.util.Locale

/**
 * TODO: document your custom view class.
 */


class AnarCircularTimeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Variables for time management
    private var totalTime: Long = 0L
    private var remainingTime: Long = 0L

    // Paint objects for drawing
    private val backgroundPaint: Paint
    private val progressPaint: Paint
    private val textPaint: Paint

    // Customizable properties
    private var backgroundColor: Int
    private var progressColor: Int
    private var textColor: Int
    private var strokeWidth: Float
    private var textSize: Float
    private var languageLocale: Locale? = null

    init {
        // Retrieve attributes from XML
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressView)
        backgroundColor = typedArray.getColor(R.styleable.CircularProgressView_backgroundColor, Color.GRAY)
        progressColor = typedArray.getColor(R.styleable.CircularProgressView_progressColor, Color.BLUE)
        textColor = typedArray.getColor(R.styleable.CircularProgressView_textColor, Color.BLACK)
        strokeWidth = typedArray.getDimension(R.styleable.CircularProgressView_strokeWidth, 10f)
        textSize = typedArray.getDimension(R.styleable.CircularProgressView_textSize, 24f)
        val localeCode = typedArray.getString(R.styleable.CircularProgressView_languageLocale)
        languageLocale = when {
            localeCode.isNullOrBlank() || localeCode == "system" -> null
            else -> Locale(localeCode)
        }
        typedArray.recycle()

        // Initialize paints
        backgroundPaint = Paint().apply {
            color = backgroundColor
            style = Paint.Style.STROKE
            this.strokeWidth = this@AnarCircularTimeView.strokeWidth
            isAntiAlias = true
        }

        progressPaint = Paint().apply {
            color = progressColor
            style = Paint.Style.STROKE
            this.strokeWidth = this@AnarCircularTimeView.strokeWidth
            isAntiAlias = true
        }

        textPaint = Paint().apply {
            color = textColor
            this.textSize = this@AnarCircularTimeView.textSize
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
    }

    // Ensure the view is square
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = minOf(width, height)
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val centerX = width / 2
        val centerY = height / 2
        val radius = (width / 2) - (strokeWidth / 2)

        // Draw the static background circle
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)

        // Draw the progress arc (decreases as remaining time decreases)
        if (totalTime > 0) {
            val fraction = remainingTime.toFloat() / totalTime.toFloat()
            val sweepAngle = fraction * 360f
            val oval = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
            canvas.drawArc(oval, -90f, sweepAngle, false, progressPaint)
        }

        // Draw the remaining time text in the center
        val text = formatTime(remainingTime)
        val textY = centerY - (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(text, centerX, textY, textPaint)
    }

    // Format remaining time as "MM:SS" or "SS s"
    private fun formatTime(millis: Long): String {
        val seconds = (millis / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        val hasLocale: Boolean = languageLocale != null
        return if (minutes > 0) {
            String.format(if (hasLocale) languageLocale!! else Locale.getDefault(),
                "%d:%02d", minutes, remainingSeconds)
        } else {
            String.format(if (hasLocale) languageLocale!! else Locale.getDefault(),
                "%d s", seconds)
        }
    }

    // Public methods to set time
    fun setTotalTime(time: Long) {
        totalTime = time
        invalidate()
    }

    fun setRemainingTime(time: Long) {
        remainingTime = maxOf(0L, minOf(time, totalTime)) // Clamp between 0 and totalTime
        invalidate()
    }
}