package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlinx.android.synthetic.main.content_main.view.*
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0


    private  var loadingColor: Int = 0
    private  var textColor: Int = 0
    private  var circleColor: Int = 0
    private  var buttonColor: Int = 0
    private  var buttonLabel : String
    private  var progress:Float = 0f

    private val valueAnimator = ValueAnimator.ofFloat(0.0f, 360.0f).setDuration(3000)

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, _, new ->
        when (new) {
            ButtonState.Clicked -> {
                buttonLabel = context.getString(R.string.button_name)
            }
            ButtonState.Loading -> {
                buttonLabel = resources.getString(R.string.button_loading)
                valueAnimator.start()
                loadingButtonId.isClickable = false
                invalidate()
            }
            ButtonState.Completed -> {
                buttonLabel = resources.getString(R.string.button_name)
                loadingButtonId.isClickable = true
                progress = 0F
                valueAnimator.cancel()
                invalidate()
            }
        }
    }


    init {
        isClickable = true
        buttonLabel = context.getString(R.string.button_name)
        buttonState = ButtonState.Clicked
        context.withStyledAttributes(attrs, R.styleable.LoadingButton){
            buttonColor = getColor(R.styleable.LoadingButton_buttonColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            loadingColor = getColor(R.styleable.LoadingButton_loadingColor, 0)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, 0)
        }
        valueAnimator.apply {
            addUpdateListener { valueAnimator ->
                progress = valueAnimator.animatedValue as Float
                invalidate()
            }
        }
    }



    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.BOLD)
        color=context.getColor(R.color.colorPrimary)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = buttonColor
        canvas?.drawRect(0f,0f,widthSize.toFloat(), heightSize.toFloat(), paint)

        paint.color = loadingColor
        canvas?.drawRect(0f, 0f,widthSize *  progress/360f, heightSize.toFloat(), paint)

        paint.color = textColor
        canvas?.drawText(buttonLabel, widthSize / 2.0f, heightSize / 2 - (paint.descent() + paint.ascent()) / 2, paint)

        paint.color = circleColor
        canvas?.drawArc(widthSize - 200f,50f,widthSize - 100f,100f,0f, progress, true, paint)

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