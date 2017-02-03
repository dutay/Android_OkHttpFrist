package com.steven.android_okhttpfrist;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;


/**
 *
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private static MyApplication app;

    public static MyApplication getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        initOkHttpUtils();

        initPicasso();
    }

    /**
     * 初始化单例OkHttpClient对象
     */
    private void initOkHttpUtils() {
        OkHttpClient okHttpClient = OkHttpClientUtils.getOkHttpSingletonInstance();
        Log.d(TAG, "---->initOkHttpUtils(): " + okHttpClient.toString());
    }

    /**
     * 初始化单例Picasso对象
     */
    private void initPicasso() {
        //配置Picasso
        Picasso picasso = new Picasso.Builder(this)
                //设置内存缓存大小，10M
                .memoryCache(new LruCache(10 << 20))
                        //下载图片的格式，这样可以节省一半的内存
                .defaultBitmapConfig(Bitmap.Config.RGB_565)
                        //配置下载器，这里用的是OkHttp，必需单独加OkHttp，同时设置了磁盘缓存的位置和大小
                //.downloader(new UrlConnectionDownloader())
                //.downloader(new OkHttpDownloader())
                .downloader(new MyDownLoader(getCacheDir(), 20 << 20))
                        //设置图片左上角的标记
                        //红色：代表从网络下载的图片
                        //蓝色：代表从磁盘缓存加载的图片
                        //绿色：代表从内存中加载的图片
                .indicatorsEnabled(true)
                //.loggingEnabled(true)
                .build();
        Picasso.setSingletonInstance(picasso);
    }



}
