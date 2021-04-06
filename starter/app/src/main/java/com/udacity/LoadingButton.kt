package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var buttonWidth = 0
    private var sweepAngle = 0
    private var oval :RectF

    private var buttonText = resources.getString(R.string.button_name)

    private val ANIM_DURATION = 3000L

    private var buttonBackgroundColor = 0
    private var buttonTextColor = 0
    private var buttonTextColorDefault = 0
    private var buttonTextColorAnim = 0

    private var paintRect = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        style = Paint.Style.FILL_AND_STROKE
        color = resources.getColor(R.color.colorPrimary, null)
    }
    private var paintRectLoadingProgressBar = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        style = Paint.Style.FILL_AND_STROKE
    }
    private var paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        style = Paint.Style.FILL
        color = resources.getColor(R.color.colorAccent, null)
    }

    private var paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 60f
        typeface = Typeface.create("", Typeface.NORMAL)
    }

    private var textPosX : Float = 0F
    private var textPosY: Float  = 0F
    private var valueAnimator = ValueAnimator()
    private var circleAnimator = ValueAnimator()

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

        when(new){

            ButtonState.Loading -> showLoadingAnimation()

            ButtonState.Completed -> stopLoadingAnimation()
        }

    }

    init{

        context.withStyledAttributes(attrs, R.styleable.LoadingButton){
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_buttonBackgroundColor,0)
            buttonTextColorDefault = getColor(R.styleable.LoadingButton_buttonTextColorDefault,0)
            buttonTextColorAnim = getColor(R.styleable.LoadingButton_buttonTextColorAnim,0)
        }

        buttonState = ButtonState.Clicked
        oval = RectF()
        buttonTextColor = buttonTextColorDefault


    }

    private fun showLoadingAnimation() {
        buttonText = resources.getString(R.string.button_loading)
        buttonTextColor = buttonTextColorAnim
        val left = widthSize - 250f
        val right = widthSize - 150f
        oval.set(left, (heightSize/4).toFloat(),right, (heightSize/4+100f))

        valueAnimator = ValueAnimator.ofInt(0, widthSize).apply {
            duration = ANIM_DURATION
            addUpdateListener { animation ->
                buttonWidth = animation.animatedValue as Int
                animation.repeatCount = ValueAnimator.INFINITE
                animation.repeatMode = ValueAnimator.RESTART
                invalidate()
            }
        }

        circleAnimator = ValueAnimator.ofInt(0,widthSize).apply{
            duration = ANIM_DURATION
            addUpdateListener { animation ->
                sweepAngle = animation.animatedValue as Int
                animation.repeatCount = ValueAnimator.INFINITE
                animation.repeatMode = ValueAnimator.RESTART
                invalidate()
            }
        }

        valueAnimator.start()
        circleAnimator.start()
    }

    private fun stopLoadingAnimation(){
        buttonText = resources.getString(R.string.button_name)
        buttonTextColor = buttonTextColorDefault
        buttonWidth = 0
        sweepAngle = 0
        valueAnimator.cancel()
        circleAnimator.cancel()
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paintRectLoadingProgressBar.color = buttonBackgroundColor
        paintText.color = buttonTextColor

        canvas?.drawRect(0f,0f,widthSize.toFloat(),heightSize.toFloat(), paintRect)
        canvas?.drawRect(0f,0f,buttonWidth.toFloat(),heightSize.toFloat(),paintRectLoadingProgressBar)

        textPosX = widthSize/2.toFloat()
        textPosY = (heightSize / 2)-((paintText.descent() + paintText.ascent())/ 2)
        canvas?.drawArc(oval,0f, sweepAngle.toFloat(),true,paintCircle)
        canvas?.drawText(buttonText,textPosX,textPosY,paintText)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}