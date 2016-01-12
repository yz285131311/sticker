package com.whisper.sticker;

import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

/**
 * 作者：Admin on 2015/12/28 16:01
 * 邮箱：974453813@qq.com
 */
public class TextSticker extends Sticker {
    public static float DEFAULT_SIZE = 60f;
    private String text;
    private float fontSize;

    private float width;

    private float height;


    public TextSticker(String text) {
        this(text, DEFAULT_SIZE);
    }

    public TextSticker(String text, float textSize) {
        this.text = text;
        this.fontSize = textSize;


        Paint paint = new Paint();
        Rect rect = new Rect();
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), rect);
        float ascent = paint.getFontMetrics().ascent;
        float decent = paint.getFontMetrics().descent;
        float leading = paint.getFontMetrics().leading;

        float top = paint.getFontMetrics().top;
        float bottom = paint.getFontMetrics().bottom;
        Log.i("-- TextSticker create", "height:" + rect.height() + ",width:" + rect.width());
        Log.i("-- TextSticker create", "ascent=" + ascent + ",decent:" + decent + ",leading=" + leading + ",top=" + top + ",bottom=" + bottom);

        init(text, paint);

        this.height = this.lines * (bottom - top);
        if (this.width <= 0)
            this.width = rect.width();
        Log.i("--stoker",paint.getStrokeWidth() + "");
        width += 20;
        height += 20;
        float x = width;
        float y = this.height;
        if (x > 0) {
            System.out.println("w,h =" + x + "," + y);
        }
        Log.i("-- img --", "w,h =" + x + "," + y);


        this.mapPointsSrc = new float[]{0, 0, x, 0, x, y, 0, y, x / 2, y / 2};

    }

    private int lines;


    public void init(String text, Paint paint) {
        if (text.contains("\n")) {
            lines = 1;
            this.width = 0;
            int lastIndex = 0;
            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) == '\n') {
                    lines++;
                    int startIndex = lastIndex == 0 ? lastIndex : lastIndex + 1;
                    float width = caculateWidth(text.substring(startIndex, i), paint);
                    Log.i("width caculate", text.substring(startIndex, i) + ", " + width);
                    if (width > this.width)
                        this.width = width;
                    lastIndex = i;
                }
            }
            float width = caculateWidth(text.substring(lastIndex + 1), paint);
            Log.i("width caculate", text.substring(lastIndex + 1) + ", " + width);
            if (width > this.width)
                this.width = width;

            Log.i("-- line count", lines + "");
        } else {
            lines = 1;
        }
    }

    public float caculateWidth(String text, Paint paint) {
//        Rect rect = new Rect();
//        paint.getTextBounds(text, 0, text.length(), rect);
//        return rect.width();
        return paint.measureText(text);
    }

    public int getFontHeight(String text, Paint paint) {
        Rect rect = new Rect();
//            paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
