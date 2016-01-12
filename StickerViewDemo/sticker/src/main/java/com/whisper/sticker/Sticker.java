package com.whisper.sticker;

import android.graphics.Matrix;

/**
 * 作者：yz on 2015/12/28 16:03
 * 邮箱：974453813@qq.com
 */
public class Sticker {
    public Sticker() {
        mMatrix = new Matrix();
    }

    protected Matrix mMatrix;

    protected boolean focused; // 是否聚焦

    protected float[] mapPointsSrc; // 分别为 左上角 右上角 右下角 左下角 中心点的坐标  需要初始化

    protected float[] mapPointsDst = new float[10];

    protected float scaleSize = 1.0f;

    protected float rotate = 0f;

    public Matrix getmMatrix() {
        return mMatrix;
    }

    public void setmMatrix(Matrix mMatrix) {
        this.mMatrix = mMatrix;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public float[] getMapPointsSrc() {
        return mapPointsSrc;
    }

    public void setMapPointsSrc(float[] mapPointsSrc) {
        this.mapPointsSrc = mapPointsSrc;
    }

    public float[] getMapPointsDst() {
        return mapPointsDst;
    }

    public void setMapPointsDst(float[] mapPointsDst) {
        this.mapPointsDst = mapPointsDst;
    }

    public float getScaleSize() {
        return scaleSize;
    }

    public void setScaleSize(float scaleSize) {
        this.scaleSize = scaleSize;
    }

    public float getRotate() {
        return rotate;
    }

    public void setRotate(float rotate) {
        this.rotate = rotate;
    }
}
