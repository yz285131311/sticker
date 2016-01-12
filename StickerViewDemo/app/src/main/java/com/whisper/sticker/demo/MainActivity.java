package com.whisper.sticker.demo;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.whisper.sticker.ImageSticker;
import com.whisper.sticker.StickerView;
import com.whisper.sticker.SvgSticker;
import com.whisper.sticker.TextSticker;

import java.io.IOException;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StickerView stickerView = (StickerView) findViewById(R.id.sticker_view);
        //设置背景
//        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test_bg);
//        stickerView.setBgBitmap(bitmap);
//

        stickerView.addSticker(BitmapFactory.decodeResource(getResources(), R.mipmap.share_frame_logo));


        ImageSticker imageSticker2 = new ImageSticker(BitmapFactory.decodeResource(getResources(), R.mipmap.test2));
        imageSticker2.getmMatrix().postTranslate(100,40);
        stickerView.addSticker(imageSticker2);


        ImageSticker imageSticker3 = new ImageSticker(BitmapFactory.decodeResource(getResources(), R.drawable.te1));
        imageSticker2.getmMatrix().postTranslate(200, 100);
        stickerView.addSticker(imageSticker3);


//        Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/0/DCIM/Camera/IMG_20151216_150207.jpg");
//        Log.i("-- file--",file.exists() + "");
//
//        ImageSticker imageSticker4 = new ImageSticker(BitmapFactory.decodeFile("/storage/emulated/0/DCIM/Camera/IMG_20151216_150207.jpg"));
////        imageSticker3.getmMatrix().postTranslate(200, 100);
//        stickerView.addSticker(imageSticker4);


        // 添加文本贴纸
//        TextSticker sticker = new TextSticker("我是测  \n试文本");
//        stickerView.addSticker(sticker);


        TextSticker sticker = new TextSticker("一曲肝肠断\n 天涯何处觅知音",100);
        sticker.getmMatrix().postTranslate(200,100);
        stickerView.addSticker(sticker);

        try {
            SVG s = SVG.getFromAsset(getAssets(), "svg/file.svg");
            stickerView.addSticker(new SvgSticker(s, Color.RED));

            s = SVG.getFromAsset(getAssets(), "svg/icon4.svg");
            stickerView.addSticker(new SvgSticker(s,0x123333));

            s = SVG.getFromAsset(getAssets(), "svg/icon4.svg");
            stickerView.addSticker(new SvgSticker(s,Color.RED));
        } catch (SVGParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
