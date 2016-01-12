package com.whisper.sticker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.util.Log;

import com.caverock.androidsvg.SVG;

/**
 * 作者：Admin on 2016/1/6 17:58
 * 邮箱：974453813@qq.com
 */
public class SvgSticker extends Sticker {
    private SVG svg;
    private Bitmap bgBitmap;
    private float width;
    private float height;

    private int color = -1;
    public SvgSticker(SVG svg) {
        this(svg,-1);
    }

    public SvgSticker(SVG svg,int color) {
        this.color = color;
        this.svg = svg;
        this.bgBitmap = svgToBitmap(svg,200,200,color);
        int x = bgBitmap.getWidth();
        int y = bgBitmap.getHeight();
        Log.i("-- img --", "w,h =" + x + "," + y);
        this.mapPointsSrc = new float[]{0, 0, x, 0, x, y, 0, y, x / 2, y / 2};
        this.mMatrix.postTranslate(50, 50);
        this.width = x;
        this.height = y;
    }

    /**
     * svg文件渲染成图片
     * @param svg
     * @param width
     * @param height
     * @param color
     * @return
     */
    private Bitmap svgToBitmap(SVG svg,int width,int height,int color) {
        svg.setDocumentWidth(width);
        svg.setDocumentHeight(height);

        Picture p =  svg.renderToPicture(width,height, color);
        Bitmap bitmap = Bitmap.createBitmap(p.getWidth(), p.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);
        p.draw(c);
        return bitmap;
    };

    public SVG getSvg() {
        return svg;
    }

    public void setSvg(SVG svg) {
        this.svg = svg;
    }

    public Bitmap getBgBitmap() {
        return bgBitmap;
    }

    public void setBgBitmap(Bitmap bgBitmap) {
        this.bgBitmap = bgBitmap;
    }

    public void resizeBitmap() {
        int width = (int)(this.width * getScaleSize());
        int height = (int) (this.height * getScaleSize());
        if(width < 0)
            width = 1;
        if(height < 0)
            height = 1;
        Log.i("-- resizeBitmap", width + "," + height);
//        svg.setDocumentWidth(width);
//        svg.setDocumentHeight(height);
////        0x123333
//        Picture p =  svg.renderToPicture(width,height, Color.BLUE);
        this.bgBitmap = svgToBitmap(svg,width, height, this.color);
//        Canvas c = new Canvas(this.bgBitmap);
//        p.draw(c);
        Log.i("-- resizeBitmap bitmap", bgBitmap.getWidth() + "," + bgBitmap.getHeight());
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

