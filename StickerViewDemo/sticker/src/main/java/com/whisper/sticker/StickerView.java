package com.whisper.sticker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 贴纸视图
 * 作者：yz on 2015/12/14 14:48
 * 邮箱：974453813@qq.com
 */
public class StickerView extends View {
    public static String TAG = "<<-- StickerView -->>";

    private Bitmap mControllerBitmap, mDeleteBitmap, bgBitmap; //拉伸图片 、 删除图片 、 背景图片

    private float mControllerWidth, mControllerHeight, mDeleteWidth, mDeleteHeight;

    private List<Sticker> stickers = new ArrayList<>(); //贴纸列表

    private int focusStickerPosition = -1;//获取焦点的贴纸索引

    private Paint mBorderPaint;
    /**
     * 贴纸边框画笔
     */

    private TextPaint mTextPaint;
    /**
     * 文本画笔
     */

    private RectF mViewRect; // 当前view的范围

    private boolean mInController, mInMove, mInDelete = false;

    private float mLastPointX, mLastPointY;
    /**
     * 处理位移
     */

    private float mTouchDownX, mTouchDownY;
    /**
     * 处理事件 如 当第二次点击已经选择的贴纸时 并且位移不大 则算作输入事件
     */

    private boolean isPreFocused;

    private float deviation; //表示触摸位置与贴纸右下角误差距离

    Typeface typeface;

    public StickerView(Context context) {
        this(context, null);
    }

    public StickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        typeface = Typeface.createFromAsset(getContext().getAssets(),"fonts/HandmadeTypewriter.ttf");
        mControllerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.editmode_scale);
        mControllerWidth = mControllerBitmap.getWidth();
        mControllerHeight = mControllerBitmap.getHeight();

        mDeleteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.editmode_delete);
        mDeleteWidth = mDeleteBitmap.getWidth();
        mDeleteHeight = mDeleteBitmap.getHeight();

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setFilterBitmap(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(2.0f);
        mBorderPaint.setColor(Color.YELLOW);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStyle(Paint.Style.FILL);
        Log.d(TAG, "mControllerBitmap w,h={" + mControllerWidth + "," + mControllerHeight + "} mDeleteBitmap={" + mDeleteWidth + "," + mDeleteHeight + "}");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0, 0);
        //绘制背景
//        if (bgBitmap != null)
//            canvas.drawBitmap(bgBitmap, 0, 0, null);

        if (stickers.size() <= 0) {
            return;
        }

        //绘制贴纸
        for (int i = 0; i < stickers.size(); i++) {
            Sticker sticker = stickers.get(i);
            // 计算matrix处理后的坐标
            sticker.getmMatrix().mapPoints(sticker.getMapPointsDst(), sticker.getMapPointsSrc());

            if (sticker instanceof ImageSticker) {
                /** 绘制贴纸图片 */
                canvas.drawBitmap(((ImageSticker) sticker).getBgBitmap(), stickers.get(i).getmMatrix(), null);

            } else if(sticker instanceof SvgSticker) {
                float[] dst = sticker.getMapPointsDst();
//                ((SvgSticker) sticker).resizeBitmap();
                canvas.save();

                canvas.translate(dst[0],dst[1]);
                canvas.rotate((float) MathUtil.caculateAngle(dst[2], dst[3], dst[0], dst[1]));

//                    Log.i("--ondraw size static", staticLayout.getWidth() + ", " + staticLayout.getHeight() + ", " + staticLayout.getTopPadding() + "," + getPaddingBottom());
                canvas.drawBitmap(((SvgSticker) sticker).getBgBitmap(),0,0,null);
                canvas.restore();
//
//                canvas.drawBitmap(((SvgSticker) sticker).getBgBitmap(), ((SvgSticker) sticker).getSvgMatrix(), null);
            } else {
                if (sticker instanceof TextSticker) {
                    /** 绘制贴纸文本 */
                    float[] dst = sticker.getMapPointsDst();
                    mTextPaint.setTextSize(((TextSticker) sticker).getFontSize() * sticker.getScaleSize());
//                    mTextPaint.setTypeface(typeface);

                    Log.i("-- TextSticker size", "size=" + ((TextSticker) sticker).getFontSize() + ", scaleSize=" + sticker.getScaleSize() );
                    Path path = new Path();
                    path.moveTo((dst[6] + dst[0]) / 2, (dst[7] + dst[1]) / 2); //设定起始点
                    path.lineTo((dst[4] + dst[2]) / 2, (dst[5] + dst[3]) / 2);//第一条直线的终点，也是第二条直线的起点

                    path.close();//闭环
                    /** 绘制文本 */
                    Rect rect = new Rect();
                    mTextPaint.getTextBounds(((TextSticker) sticker).getText(), 0, ((TextSticker) sticker).getText().length(), rect);
                    float ascent = mTextPaint.getFontMetrics().ascent;
                    float decent = mTextPaint.getFontMetrics().descent;
                    float leading = mTextPaint.getFontMetrics().leading;
                    float top = mTextPaint.getFontMetrics().top;
                    float bottom = mTextPaint.getFontMetrics().bottom;

                    float dy = (bottom - top) / 2 - decent - (bottom - decent);

                    Log.i("-- TextSticker ondraw", "ascent=" + ascent + ",decent:" + decent + ",leading=" + leading + ",top=" + top + ",bottom=" + bottom + ",height=" + rect.height() + ", bottom-top =" + (bottom - top) + " , dy=" + dy);
//                  canvas.drawTextOnPath(((TextSticker) sticker).getText(), path, 0, dy, mTextPaint);
//                    canvas.drawLine((dst[6] + dst[0]) / 2, (dst[7] + dst[1]) / 2, (dst[4] + dst[2]) / 2, (dst[5] + dst[3]) / 2, mTextPaint);
                    canvas.save();
                    canvas.translate(dst[0] + 10, dst[1] +10);
                    canvas.rotate((float) MathUtil.caculateAngle(dst[2], dst[3], dst[0], dst[1]));

                    StaticLayout staticLayout = new StaticLayout(((TextSticker) sticker).getText(), mTextPaint, 10000, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);
                    staticLayout.draw(canvas, path, mTextPaint,0);
//                    Log.i("--ondraw size static", staticLayout.getWidth() + ", " + staticLayout.getHeight() + ", " + staticLayout.getTopPadding() + "," + getPaddingBottom());
                    canvas.restore();
                }
            }

            if (sticker.isFocused()) { // 绘制边框 以及 删除、拉升图标
                drawFocusedStickerBorder(canvas, sticker);
            }
        }
        Paint p = new Paint();
        p.setColor(Color.RED);
        p.setStrokeWidth(5);
        p.setStyle(Paint.Style.FILL);
        //绘制测试点
        canvas.drawPoint(mLastPointX, mLastPointY, p);
        Log.i("--- isPointInSameSide", mLastPointX + "," + mLastPointY);;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mViewRect == null) {
            mViewRect = new RectF(0f, 0f, getMeasuredWidth(), getMeasuredHeight());
            Log.i(TAG, "Measured: " + getMeasuredWidth() + "," + getMeasuredHeight() + ", real：" + getWidth() + "," + getHeight());
        }
        /** 如果没有贴纸 或者 没有获取焦点的贴纸则不处理触摸事件 */
        if (stickers.size() < 0) {
            return true;
        }
        float x = event.getX();
        float y = event.getY();

        Log.i(TAG, "touch event x,y = (" + x + " , " + y + ")");

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isInController(x, y)) {
                    mInController = true;
                    T("click controll0..");

                    mLastPointX = x;
                    mLastPointY = y;

                    // 计算误差距离 用于缩放
                    float[] dst = stickers.get(focusStickerPosition).getMapPointsDst();
                    float nowLength = (float) MathUtil.caculateDistance(dst[0], dst[1], dst[8], dst[9]);
                    float touchLength = (float) MathUtil.caculateDistance(x, y, dst[8], dst[9]);
                    deviation = touchLength - nowLength;
                    break;
                }

                if (isInDelete(x, y)) {
                    mInDelete = true;
                    break;
                }

                if (isFocusSticker(x, y)) {
                    mTouchDownX = x;
                    mTouchDownY = y;

                    mLastPointY = y;
                    mLastPointX = x;
                    mInMove = true;
                    invalidate();
                } else {
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mInDelete && isInDelete(x, y)) {
                    doDeleteSticker(); // down 和 up 动作都在删除按钮范围内时 触发删除事件
                } else if (isPreFocused && MathUtil.caculateDistance(x, y, mTouchDownX, mTouchDownY) < 2
                        && getFocusSticker() instanceof TextSticker) {
                    /** 触发输入事件 */
                    doInputText();
                }
                mInMove = false;
                mInDelete = false;
                mInController = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                mInMove = false;
                mInDelete = false;
                mInController = false;
                T("ACTION_CANCEL");
                break;
            case MotionEvent.ACTION_MOVE:
                if (mInController) {
                    float centerX = stickers.get(focusStickerPosition).getMapPointsDst()[8];
                    float centerY = stickers.get(focusStickerPosition).getMapPointsDst()[9];
                    double nowAngle = MathUtil.caculateAngle(x, y, centerX, centerY);

                    double beforeAngle = MathUtil.caculateAngle(mLastPointX, mLastPointY, centerX, centerY);

                    Log.i("--- angle -- ", "now =" + nowAngle + ", pre=" + beforeAngle + ", rotate=" + (nowAngle - beforeAngle));
                    // 旋转
                    stickers.get(focusStickerPosition).getmMatrix().postRotate((float) (nowAngle - beforeAngle), centerX, centerY);

                    stickers.get(focusStickerPosition).setRotate(stickers.get(focusStickerPosition).getRotate() + (float) (nowAngle - beforeAngle));
                    // 缩放
                    float[] dst = stickers.get(focusStickerPosition).getMapPointsDst();
                    float nowLength = (float) MathUtil.caculateDistance(dst[0], dst[1], dst[8], dst[9]);
                    float touchLength = (float) MathUtil.caculateDistance(x, y, dst[8], dst[9]) - deviation;
                    if (nowLength != touchLength) {
                        float scale = touchLength / nowLength; //计算缩放率
                        stickers.get(focusStickerPosition).getmMatrix().postScale(scale, scale, dst[8], dst[9]);

                        //更新贴纸的缩放率
                        float newScale = stickers.get(focusStickerPosition).getScaleSize() * scale;
                        stickers.get(focusStickerPosition).setScaleSize(newScale);
                        if(stickers.get(focusStickerPosition) instanceof SvgSticker) {
                            float dx = (float)((scale - 1) * MathUtil.caculateDistance(dst[0],dst[1],dst[2],dst[3]));
                            float dy = (float)((scale - 1) * MathUtil.caculateDistance(dst[0],dst[1],dst[6],dst[7]));
                            ((SvgSticker) stickers.get(focusStickerPosition)).resizeBitmap();
                        }
                    }

                    mLastPointX = x;
                    mLastPointY = y;
                    postInvalidate();
                    break;
                }


                if (mInMove) {
                    float cX = x - mLastPointX; // 水平位移
                    float cY = y - mLastPointY; // 竖直位移
                    if (Math.sqrt(cX * cX + cY * cY) > 2.0f) { //判定为移动
                        stickers.get(focusStickerPosition).getmMatrix().postTranslate(cX, cY);

                        mLastPointX = x;
                        mLastPointY = y;
                        postInvalidate();
                    }
                }
                break;
        }


        return true;
    }




    /**
     * 判断是否点击了控制图标
     */
    private boolean isInController(float x, float y) {
        if (focusStickerPosition < 0)
            return false;
        float[] dst = stickers.get(focusStickerPosition).getMapPointsDst();
        // 获取贴纸左上角坐标
        float ltX = dst[4];
        float ltY = dst[5];

        // 获取删除图标范围
        RectF rectF = new RectF(ltX - mDeleteWidth / 2, ltY - mDeleteHeight / 2, ltX + mDeleteWidth / 2, ltY + mDeleteHeight / 2);
        return rectF.contains(x, y);
    }


    /**
     * 判断是点击了删除
     */
    private boolean isInDelete(float x, float y) {
        if (focusStickerPosition < 0)
            return false;
        float[] dst = stickers.get(focusStickerPosition).getMapPointsDst();
        // 获取贴纸左上角坐标
        float ltX = dst[0];
        float ltY = dst[1];

        // 获取删除图标范围
        RectF rectF = new RectF(ltX - mDeleteWidth / 2, ltY - mDeleteHeight / 2, ltX + mDeleteWidth / 2, ltY + mDeleteHeight / 2);
        return rectF.contains(x, y);
    }


    /**
     * 判断是否选中了贴纸
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isFocusSticker(float x, float y) {
        for (int i = stickers.size() - 1; i >= 0; i--) {
            Sticker sticker = stickers.get(i);
            if (isPointInSticker(x, y, sticker)) {
                isPreFocused = sticker.isFocused(); /** 用于判断是否需要在up事件触发输入事件 */
                setFocusStickerPosition(i);
                return true;
            }
        }
        setFocusStickerPosition(-1);
        isPreFocused = false;
        return false;
    }

    private Sticker getFocusSticker() {
        return stickers.get(focusStickerPosition);
    }

    /**
     * 判断点是否在贴纸范围内
     *
     * @param x
     * @param y
     * @param sticker
     * @return
     */
    private boolean isPointInSticker(float x, float y, Sticker sticker) {
        float[] dsts = sticker.getMapPointsDst();
        Log.i(TAG, "stiker rect p1(" + dsts[0] + " , " + dsts[1] + "), " +
                " p2 (" + dsts[2] + " , " + dsts[3] + "), " +
                " p3(" + dsts[4] + " , " + dsts[5] + "), " +
                " p4(" + dsts[6] + " , " + dsts[7] + ") ");
        return MathUtil.isPointInRectangle(new PointF(dsts[0], dsts[1]), new PointF(dsts[2], dsts[3]), new PointF(dsts[4], dsts[5]), new PointF(dsts[6], dsts[7]), new PointF(x, y));
    }

    /**
     * 内部调用
     */
    private void drawFocusedStickerBorder(Canvas canvas, Sticker sticker) {
        // 获取操作后的坐标
        float[] dst = sticker.getMapPointsDst();
        // 绘制边框 这里是顺时针画框
        canvas.drawLine(dst[0], dst[1], dst[2], dst[3], mBorderPaint); // 顶部线条
        canvas.drawLine(dst[2], dst[3], dst[4], dst[5], mBorderPaint); // 右侧线条
        canvas.drawLine(dst[4], dst[5], dst[6], dst[7], mBorderPaint); // 底部线条
        canvas.drawLine(dst[6], dst[7], dst[0], dst[1], mBorderPaint); // 底部线条

        // 绘制删除、拉伸图标
        canvas.drawBitmap(mDeleteBitmap, dst[0] - mDeleteWidth / 2, dst[1] - mDeleteHeight / 2, null);
        canvas.drawBitmap(mControllerBitmap, dst[4] - mControllerWidth / 2, dst[5] - mControllerHeight / 2, null);
    }

    /**
     * 设置焦点贴纸
     *
     * @param position
     */
    private void setFocusStickerPosition(int position) {
        // 重置所有贴纸的选中状态
        for (int i = 0; i < stickers.size(); i++) {
            if (i == position) {
                stickers.get(i).setFocused(true);
            } else {
                stickers.get(i).setFocused(false);
            }
        }
        if (position >= 0) {
            Sticker sticker = stickers.remove(position);
            stickers.add(sticker);
            position = stickers.size() - 1;
        }
        focusStickerPosition = position;
    }

    /**
     * 计算指定字符串和字体大小的文本长度
     * @param text
     * @param fontSize
     * @return
     */
    private int caculateFontWidth(String text, float fontSize) {
        Paint paint = new Paint();
        Rect rect = new Rect();
        paint.setTextSize(fontSize);
        paint.getTextBounds(text,0,text.length(),rect);

        return rect.width() + 10;
    }

    /*************************   触发事件 【start】 ******************/
    private void doDeleteSticker() { //删除贴纸
        T("triger delete event");
        stickers.remove(focusStickerPosition);
        postInvalidate();
        focusStickerPosition = -1;
    }

    private void doInputText() { //开始输入
//        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
//            .setTitle("输入贴纸文字").create();
//        alertDialog.show();
        final EditText inputServer = new EditText(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("输入贴纸文字").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                T(inputServer.getText().toString());
                updateFocusStickerText(inputServer.getText().toString());
            }
        });
        builder.show();
    }

    /*************************  触发事件 【end】 ******************/
    /**
     * 公开给外部调用的接口
     */
    public void setBgBitmap(Bitmap bitmap) {
        this.bgBitmap = bitmap;
    }

    /**
     * 添加一个贴纸
     *
     * @param bitmap
     */
    public void addSticker(Bitmap bitmap) {
        ImageSticker imageSticker = new ImageSticker(bitmap);
        addSticker(imageSticker);
    }

    public void addSticker(Sticker sticker) {
        stickers.add(sticker);
        focusStickerPosition = stickers.size() - 1; //最新添加的设置为聚焦模式
        setFocusStickerPosition(focusStickerPosition);
        postInvalidate();
    }

    /**
     * 更新贴纸文本
     * @param text
     */
    public void updateFocusStickerText(String text) {
        if(getFocusSticker() instanceof TextSticker) {
            ((TextSticker) getFocusSticker()).setText(text);
            // 文字长度变化 导致长度变化
            TextSticker sticker =  (TextSticker) getFocusSticker();

            float[] dst = sticker.getMapPointsDst();
            float fontSize = sticker.getFontSize() * sticker.getScaleSize();
            float preLength = (float)MathUtil.caculateDistance(dst[0],dst[1],dst[2],dst[3]);
            float nowLength = caculateFontWidth(text, fontSize);
//
//
//            float sacle = nowLength / preLength;
//            sticker.getmMatrix().postScale(sacle,1,(dst[0] + dst[7]) /2,(dst[1] + dst[8]) /2);
//            sticker.setFontSize(fontSize);
//            sticker.setScaleSize(1);
//            sticker.setScaleSize(1);

            stickers.remove(sticker);
            focusStickerPosition = -1;
            TextSticker textSticker = new TextSticker(text,fontSize);
            textSticker.setFontSize(fontSize);
            textSticker.setScaleSize(1);
            textSticker.setRotate(sticker.getRotate());

//            textSticker.setmMatrix(sticker.getmMatrix());
//
//            double degree = MathUtil.caculateAngle(dst[2],dst[3],dst[0],dst[1]);
            textSticker.getmMatrix().postTranslate(dst[8] - textSticker.getMapPointsSrc()[8],dst[9] - textSticker.getMapPointsSrc()[9]);
            textSticker.getmMatrix().mapPoints(textSticker.getMapPointsDst(), textSticker.getMapPointsSrc());
            float centerX = textSticker.getMapPointsDst()[8];
            float centerY = textSticker.getMapPointsDst()[9];
            textSticker.getmMatrix().postRotate(sticker.getRotate(),centerX,centerY);
            addSticker(textSticker);

//            Log.i("-- updateFocusStickerText", "pre= " + preLength + ", now = " + nowLength + "sacle = " + sacle);
            /** note 需要将文字当前的文字大小  */
            postInvalidate();
        }
    }

    public void T(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
