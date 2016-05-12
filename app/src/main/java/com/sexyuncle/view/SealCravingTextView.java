package com.sexyuncle.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.sexyuncle.sealcravingmaster.R;

/**
 * Created by dev-sexyuncle on 16/5/12.
 */
public class SealCravingTextView extends View {

    private static final String TAG = SealCravingTextView.class.getSimpleName();
    private String text = null;
    private int textColor = Color.RED;
    private float textSize = 20 * TypedValue.COMPLEX_UNIT_SP;
    private int numColumns = 1;
    private int numRows = 1;
    private static int VERTICAL = 0;
    private static int HORIZONTAL = 1;
    private int orientation = VERTICAL;
    private int textStyle;
    private boolean isInRelief = false;
    private boolean isPressed = false;

    public SealCravingTextView(Context context) {
        this(context, null);
    }

    public SealCravingTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SealCravingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SealCravingTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    /**
     * @param attributeSet
     * @description 初始化一些数据
     */
    private void init(AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.SealCravingTextView);
            text = typedArray.getString(R.styleable.SealCravingTextView_text);
            textSize = typedArray.getDimension(R.styleable.SealCravingTextView_textSize, textSize);
            textColor = typedArray.getColor(R.styleable.SealCravingTextView_sealsColor, textColor);
            numColumns = typedArray.getInt(R.styleable.SealCravingTextView_numColumns, numColumns);
            numRows = typedArray.getInt(R.styleable.SealCravingTextView_numRows, numRows);
            orientation = typedArray.getInt(R.styleable.SealCravingTextView_orientation, orientation);
            textStyle = typedArray.getInt(R.styleable.SealCravingTextView_textStyle, textStyle);
            isInRelief = typedArray.getBoolean(R.styleable.SealCravingTextView_inRelief, isInRelief);
            typedArray.recycle();
        }
        measureTextSize();
    }

    /**
     * @description 测算textSize
     */
    void measureTextSize() {
        TextView textView = new TextView(getContext());
        textView.setTextSize(getTextSize());
        textView.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "default_seals.ttf"));
        TextPaint textPaint = textView.getPaint();
        float oldTextSize = textSize;
        textSize = px2Sp(textPaint.measureText("鼎"));
        Log.e(TAG,"old text size is "+oldTextSize+" text size is "+textSize);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //外面的圈
        canvas.translate(getPaddingLeft(), getPaddingRight());
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(getTextSize());
        RectF outRing = new RectF(getTextSize() * 0.15f, getTextSize() * 0.15f, getWidth() - getTextSize() * 0.15f, getHeight() - getTextSize() * 0.15f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(getTextSize() * 0.05f);
        canvas.drawRoundRect(outRing, getTextSize() * 0.075f, getTextSize() * 0.075f, paint);

        //判断里面的部分是否着色
        boolean isColour = isInRelief == isPressed;
        paint.setColor(isColour ? textColor : Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        RectF inside = new RectF(getTextSize() * 0.25f, getTextSize() * 0.25f, getWidth() - getTextSize() * 0.25f, getHeight() - getTextSize() * 0.25f);
        canvas.drawRoundRect(inside, getTextSize() * 0.075f, getTextSize() * 0.075f, paint);

        //画字体
        paint.setColor(isColour ? Color.WHITE : textColor);
        drawText(canvas, paint);

    }

    /**
     * @description 画字体
     */
    void drawText(Canvas canvas, Paint paint) {
        paint.setTextAlign(Paint.Align.CENTER);
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "default_running_hand.TTF");
        paint.setTypeface(typeface);
        float startX, startY;
        float centX, centY;
        if (orientation == VERTICAL) {
            //x轴的中心位置
            centX = getWidth() - getTextSize() * 0.75f;
            int size = (int) (getHeight() / getTextSize() - 0.5f);
            for (int column = 0; column < numColumns; column++) {
                startY = getTextSize() * 1.0f;
                for (int index = 1; index <= size; index++) {
                    int textIndex = column * size + index;
                    //最后一个字
                    if (textIndex > getText().length())
                        return;
                    canvas.drawText(text.substring(textIndex - 1, textIndex), centX, startY, paint);
//                    canvas.drawCircle(centX,startY,10,paint);
                    startY = startY + getTextSize();

                }
                //x中心座椅1.5个字节
                centX = centX - getTextSize();
            }
        } else {
            for (int row = 0; row < numRows; row++) {

            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(calculateViewWidthSpec(), calculateViewHeightSpec());
    }

    /**
     * @return
     * @dewscription 计算view的宽度
     */
    private final int calculateViewWidthSpec() {
        float width;
        if (orientation == VERTICAL) {
            if (getText() == null || TextUtils.isEmpty(getText())) {
                width = getTextSize();

            } else {
                width = (numColumns + 0.5f) * getTextSize();
                Log.e(TAG, "纵向的时候,宽度根据列数计算 " + width);
            }


        } else {
            if (getText() == null || TextUtils.isEmpty(getText())) {
                width = getTextSize() * 2f;
            } else {
                width = (float) (Math.ceil(getText().length() / (float) numRows) * (getTextSize() + 0.5f));
                Log.e(TAG, "横向的时候,宽度根据行数计算 " + width);
            }

        }
        return MeasureSpec.makeMeasureSpec((int) width, MeasureSpec.EXACTLY);
    }


    /**
     * @return
     * @description 计算view的高度
     */
    private final int calculateViewHeightSpec() {
        float height;
        if (orientation == VERTICAL) {
            if (getText() == null || TextUtils.isEmpty(getText())) {
                height = getTextSize() * 2f;
            } else {
                height = (float) ((Math.ceil(getText().length() / (float) numColumns) + 0.5f) * getTextSize());
                Log.e(TAG, "纵向的时候,高度根据列数计算 " + height);

            }
        } else {
            if (getText() == null || TextUtils.isEmpty(getText())) {
                height = getTextSize();
            } else {
                height = (numRows + 0.5f) * getTextSize();
                Log.e(TAG, "横向的时候,高度根据行数计算 " + height);
            }

        }
        return MeasureSpec.makeMeasureSpec((int) height, MeasureSpec.EXACTLY);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        measure(calculateViewWidthSpec(), calculateViewHeightSpec());
        invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        measure(calculateViewWidthSpec(), calculateViewHeightSpec());
        invalidate();
    }

    public int getNumColumns() {
        return numColumns;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
        measure(calculateViewWidthSpec(), calculateViewHeightSpec());
        invalidate();
    }

    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
        measure(calculateViewWidthSpec(), calculateViewHeightSpec());
        invalidate();
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
        measure(calculateViewWidthSpec(), calculateViewHeightSpec());
        invalidate();
    }

    public int getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(int textStyle) {
        this.textStyle = textStyle;
        invalidate();
    }

    public boolean isInRelief() {
        return isInRelief;
    }

    public void setIsInRelief(boolean isInRelief) {
        this.isInRelief = isInRelief;
        invalidate();
    }

    private float px2Sp(float pxValue) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (float) (pxValue / (metrics.densityDpi / 160));
    }
}
