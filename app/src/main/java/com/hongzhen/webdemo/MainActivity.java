package com.hongzhen.webdemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.bugtags.library.Bugtags;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hongzhen.bean.Weather;
import com.hongzhen.bean.WeatherInfo;
import com.hongzhen.cache.BitmapCache;
import com.hongzhen.request.GsonRequest;
import com.hongzhen.request.XMLRequest;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private RequestQueue requestQueue;
    /**
     * Created by yuhongzhen on 2016/8/29.
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private ImageView imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = MyApplication.getInstance().getRequestQueue();
        textView = (TextView) findViewById(R.id.tv);
        imageview = (ImageView) findViewById(R.id.iv);
    }

    /**
     * get请求方法
     *
     */
    public void getHttp(View v) {
        int method = Request.Method.GET;
        StringRequest request = new StringRequest("http://php.weather.sina.com.cn/iframe/index/w_cl.php?code=js&day=0&city=&dfc=1&charset=utf-8", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                textView.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText(error.getMessage());
            }
        }) {
            //重写parseNetworkResponse方法，指定请求结果的编码为UTF-8,解决volley解析字符串的乱码问题
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, "UTF-8");
                    //volley的parseNetworkResponse默认是通过服务器返回response.headers来进行编码解析
                    //但是经常乱码，因为安卓都是以UTF-8编码，左右指定解码UTF-8
                   // parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException e) {
                    parsed = new String(response.data);
                }
                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        requestQueue.add(request);

        /**
         * 通过自定义的XMLRequest类来实现XML数据获取和解析
         */
        XMLRequest xmlRequest = new XMLRequest(
                "http://flash.weather.com.cn/wmaps/xml/china.xml",
                new Response.Listener<XmlPullParser>() {
                    @Override
                    public void onResponse(XmlPullParser response) {
                        try {
                            int eventType = response.getEventType();
                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                switch (eventType) {
                                    case XmlPullParser.START_TAG:
                                        String nodeName = response.getName();
                                        if ("city".equals(nodeName)) {
                                            String pName = response.getAttributeValue(0);
                                            Log.d("TAG", "pName is " + pName);
                                        }
                                        break;
                                }
                                eventType = response.next();
                            }
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
       // requestQueue.add(xmlRequest);

        /**
         * 通过自定义的GsonRequest来进行json数据的获取和解析
         *这里的服务器是我本地的，需要替换为你自己的
         */
        GsonRequest<Weather> gsonRequest = new GsonRequest<Weather>(
                "http://192.168.31.83:8080/my.json", Weather.class,
                new Response.Listener<Weather>() {
                    @Override
                    public void onResponse(Weather weather) {
                        WeatherInfo weatherInfo = weather.getWeatherinfo();
                        Log.d("TAG", "city is " + weatherInfo.getCity());
                        Log.d("TAG", "temp is " + weatherInfo.getTemp());
                        Log.d("TAG", "time is " + weatherInfo.getTime());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        requestQueue.add(gsonRequest);

    }


    /**
     * post请求方法,带param参数的使用
     * @param v
     */
    public void postHttp(View v){
        int method= Request.Method.POST;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.31.83:8080/webapi", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //获取成功的返回数据
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //获取失败的数据
            }
        }) {
            //重写getParams设置POST请求的参数
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("params1", "value1");
                map.put("params2", "value2");
                return map;
            }
        };
//        requestQueue.add(stringRequest);
    }

    /**
     * 通过ImageRequest来获取网络图片
     *
     */
    public void imageHttp(View v){
        String url="http://img3.a0bi.com/upload/ttq/20160709/1468043292244.png";
        Response.Listener<Bitmap> listener=new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imageview.setImageBitmap(response);
            }
        };
//
//          width,height(maxWidth,maxHeight)
//          指定允许图片最大的宽度和高度，如果指定的网络图片的宽度或高度大于这
//          里的最大值，则会对图片进行压缩，指定成0的话就表示不管图片有多大，
//          都不会进行压缩
//
        int width=0;
        int height=0;
        Bitmap.Config config= Bitmap.Config.ARGB_8888;//指定图片的颜色属性
        //ErrorListener当请求失败时的回调，一般设置一张默认的图片
        Response.ErrorListener errorListener=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };
        ImageRequest imageRequest = new ImageRequest(url, listener, width, height, config, errorListener);
        requestQueue.add(imageRequest);
    }

    /**
     * 通过imageloader获取图片,空的图片缓存ImageCache。volley默认实现了硬盘缓存，但是没有实现内存缓存
     */
    public void getImageLoader(){

//        ImageLoader imageLoader=new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
//            @Override
//            public Bitmap getBitmap(String url) {
//                return null;
//            }
//
//            @Override
//            public void putBitmap(String url, Bitmap bitmap) {
//
//            }
//        });
        //通过imageloader获取图片,自定义图片缓存ImageCache
        ImageLoader imageLoader = new ImageLoader(requestQueue, new BitmapCache());
        ImageLoader.ImageListener imageListener=imageLoader.getImageListener(imageview,R.drawable.ic_laucher,R.drawable.ic_laucher);
        imageLoader.get("http://img3.a0bi.com/upload/ttq/20160709/1468043292244.png",imageListener);
        //imageLoader.get("http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg",
        //        listener, 200, 200);//指定图片允许的最大宽度和高度


    }

    //bugtags的回调方法
    @Override
    protected void onResume() {
        super.onResume();
        //注：回调 1
        Bugtags.onResume(this);
    }
    //bugtags的回调方法
    @Override
    protected void onPause() {
        super.onPause();
        //注：回调 2
        Bugtags.onPause(this);
    }
    //bugtags的回调方法
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //注：回调 3
        Bugtags.onDispatchTouchEvent(this, event);
        return super.dispatchTouchEvent(event);
    }
}
