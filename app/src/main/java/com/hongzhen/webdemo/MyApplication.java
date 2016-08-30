package com.hongzhen.webdemo;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bugtags.library.Bugtags;

/**
 * Created by yuhongzhen on 2016/8/25.
 */
public class MyApplication extends Application {

    private RequestQueue mRequestQueue;
    private static MyApplication myApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication=this;
        //单例实现RequestQueue，全局只有一个RequestQueue，所以的request都添加到RequestQueue进行管理操作
        mRequestQueue = Volley.newRequestQueue(this);
        //在这里初始化
        Bugtags.start("680cf80f789f0b87f376b1bc432e6b6f", this, Bugtags.BTGInvocationEventBubble);
    }
    //获取RequestQueue实例
    public RequestQueue getRequestQueue(){
        return mRequestQueue;
    }
    //获取application实例
    public static MyApplication getInstance(){
        return myApplication;
    }
}
