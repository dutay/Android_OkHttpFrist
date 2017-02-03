package com.steven.android_okhttpfrist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private Context mContext = this;
    private static final String TAG = "RegisterActivity";
    private static final String URL_BASE = "http://192.168.0.103:8080";
    private static final String URL_POST = URL_BASE + "/MyWeb/RegServlet";
    private static final String URL_UPLOAD = URL_BASE + "/MyWeb/UploadServlet";

    private EditText editText_username;
    private EditText editText_pwd;
    private EditText editText_age;
    private TextView textView_result;
    private ImageView imageView_login;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
    }

    private void initView() {
        editText_username = (EditText) findViewById(R.id.editText_username);
        editText_pwd = (EditText) findViewById(R.id.editText_pwd);
        editText_age = (EditText) findViewById(R.id.editText_age);
        textView_result = (TextView) findViewById(R.id.textView_result);
        imageView_login = (ImageView) findViewById(R.id.imageView_login);
    }

    public void clickButton(View view) {
        String username = editText_username.getText() + "";
        String pwd = editText_pwd.getText() + "";
        String age = editText_age.getText() + "";

        final Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("password", pwd);
        map.put("age", age);

        switch (view.getId()) {

            //用法1：post提交键值对
            case R.id.button_post_keyvalue:
                //A .post同步提交
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final String result = OkHttpUtils.postKeyValuePair(URL_POST , map);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    textView_result.setText(result);
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();*/

                //B.post异步请求网络，提交键值对
                OkHttpUtils.postKeyValuePairAsync(URL_POST, map, new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "网络异常，访问失败！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(final Response response) throws IOException {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    textView_result.setText(response.body().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
                break;

            //用法2：post上传文件
            case R.id.button_upload:
                String filename = Constant.URL_DOWNLOAD_IMAGE.substring(Constant.URL_DOWNLOAD_IMAGE.lastIndexOf("/") + 1);
                //从SD卡私有目录中获取图片
                String filepath = SDCardHelper.getSDCardPrivateCacheDir(mContext) + File.separator + filename;
                final File[] files = new File[]{new File(filepath)};
                final String[] formFieldName = new String[]{"uploadfile"};

                //同步上传
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final String result = OkHttpUtils.postUploadFiles(URL_UPLOAD, map , files , formFieldName);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext , result , Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d(TAG, "---->e:" + e.toString());
                        }
                    }
                }).start();*/

                //异步上传
                try {
                    OkHttpUtils.postUploadFilesAsync(URL_UPLOAD, map, files, formFieldName, new Callback() {
                        @Override
                        public void onFailure(final Request request, IOException e) {
                            Log.d(TAG, "---->e1:" + e.toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "网络异常，加载失败！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(final Response response) throws IOException {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (response.isSuccessful()) {
                                        try {
                                            textView_result.setText(response.body().string());
                                            Toast.makeText(mContext, response.body().string(), Toast.LENGTH_SHORT).show();
                                        } catch (Exception ex) {

                                        }
                                    }
                                }
                            });
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "---->e2:" + e.toString());
                }
                break;
        }
    }
}
