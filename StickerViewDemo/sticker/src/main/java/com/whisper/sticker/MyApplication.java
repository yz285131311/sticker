package com.whisper.sticker;

import android.app.Application;

/**
 * 作者：Admin on 2016/1/6 18:32
 * 邮箱：974453813@qq.com
 */
public class MyApplication extends Application{
    static MyApplication application;

    public static MyApplication getApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }
}
