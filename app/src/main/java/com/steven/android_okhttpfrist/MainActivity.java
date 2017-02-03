package com.steven.android_okhttpfrist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.steven.android_okhttpfrist.adapter.MyNewsAdapter;
import com.steven.android_okhttpfrist.adapter.MyNewsAdapter0;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Context mContext = this;
    private ListView listView_main;
    private TextView textView_empty;
    private ProgressBar progressBar_main;
    private MyNewsAdapter0 adapter0 = null;
    private MyNewsAdapter adapter = null;
    private List<Map<String, String>> totalList0 = new ArrayList<>();
    private List<QiushiModel.ItemsEntity> totalList = new ArrayList<>();
    private Handler handler = new Handler();
    private int curPage = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化控件
        initView();
        //加载网络数据
        loadNetwokData();
    }

    private void loadNetwokData() {
        //做法1：get网络请求的普通做法
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String result_string = OkHttpUtils.getStringFromURL(String.format(Constant.URL_LATEST, 1));

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (result_string == null) {
                                Toast.makeText(mContext, "网络访问异常，加载失败！", Toast.LENGTH_SHORT).show();
                            } else {
                                final List<Map<String, String>> result = jsonStringToList(result_string);
                                progressBar_main.setVisibility(View.GONE);
                                if (curPage == 1) {
                                    adapter.reloadData(result, true);
                                } else {
                                    adapter.reloadData(result, false);
                                }
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "网络访问异常，加载失败！", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();*/

        //做法2：get异步网络请求的做法
        OkHttpClientUtils.getDataAsync(mContext, String.format(Constant.URL_LATEST, 1), new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "网络异常，加载失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                //Log.d(TAG, "---->>onResponse: " + Thread.currentThread().getId());
                if (response.isSuccessful()) {
                    String result_json = response.body().string();

                    //一、调用利用泛型封装的方法
                    /*Map<String, List<Map<String, String>>> result_map = parseJsonToMap(result_json);
                    Log.d(TAG, "---->>map: " + result_map);
                    final List<Map<String, String>> result_list0 = result_map.get("items");
                    Log.d(TAG, "---->>list0: " + result_list0);*/


                    //List<QiushiModel> result_model = parseJsonToList(response.body().toString());
                    //List<QiushiModel.ItemsEntity> result_items = parseJsonToList(response.body().string());
                    //Log.d(TAG, "---->>items: " + result_items);

                    //二、加载数据到adapter0
                    /*final List<Map<String, String>> result_list = jsonStringToList(result_json);
                    Log.d(TAG, "---->>list: " + result_list);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Log.d(TAG, "---->>内部run: " + Thread.currentThread().getId());
                            progressBar_main.setVisibility(View.GONE);
                            if (curPage == 1) {
                                adapter0.reloadData(result_list, true);
                            } else {
                                adapter0.reloadData(result_list, false);
                            }
                        }
                    });*/


                    //三、加载数据到adapter
                    QiushiModel result_model = parseJsonToQiushiModel(result_json);
                    Log.d(TAG, "---->>qiushi model: " + result_model.getItems());

                    final List<QiushiModel.ItemsEntity> result_list_entity = result_model.getItems();
                    Log.d(TAG, "---->>qiushi items entity: " + result_list_entity);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Log.d(TAG, "---->>内部run: " + Thread.currentThread().getId());
                            progressBar_main.setVisibility(View.GONE);
                            if (curPage == 1) {
                                adapter.reloadData(result_list_entity, true);
                            } else {
                                adapter.reloadData(result_list_entity, false);
                            }
                        }
                    });
                }
            }
        }, "main");
    }

    private void initView() {
        listView_main = (ListView) findViewById(R.id.listview_main);
        textView_empty = (TextView) findViewById(R.id.textView_empty);
        progressBar_main = (ProgressBar) findViewById(R.id.progressBar_main);

        adapter0 = new MyNewsAdapter0(totalList0, mContext);
        adapter = new MyNewsAdapter(totalList, mContext);
        listView_main.setAdapter(adapter);
        listView_main.setEmptyView(textView_empty);

    }

    //json字符串转List集合对象
    private List<Map<String, String>> jsonStringToList(String jsonString) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray_items = jsonObject.getJSONArray("items");
            for (int i = 0; i < jsonArray_items.length(); i++) {
                Map<String, String> map = new HashMap<String, String>();

                JSONObject jsonObject_item = jsonArray_items.getJSONObject(i);

                map.put("content", jsonObject_item.getString("content"));
                map.put("image", jsonObject_item.getString("image"));
                map.put("comments_count",
                        jsonObject_item.getString("comments_count"));

                JSONObject jsonObject_user = jsonObject_item
                        .optJSONObject("user");
                if (jsonObject_user == null) {
                    map.put("login", "");
                } else {
                    map.put("login", jsonObject_user.optString("login"));
                }
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //gson解析
    private QiushiModel parseJsonToQiushiModel(String jsonString) {
        Gson gson = new Gson();
        QiushiModel model = gson.fromJson(jsonString,
                new TypeToken<QiushiModel>() {
                }.getType());
        return model;
    }

    //gson解析成List集合对象
    private List<QiushiModel.ItemsEntity> parseJsonToQiushiList(String jsonString) {
        Gson gson = new Gson();
        List<QiushiModel.ItemsEntity> list = gson.fromJson(jsonString,
                new TypeToken<List<QiushiModel.ItemsEntity>>() {
                }.getType());
        return list;
    }


    private <T> T parseJsonToMap(String jsonString) {
        Gson gson = new Gson();
        Type typeOfT = new TypeToken<T>() {
        }.getType();
        T obj = gson.fromJson(jsonString, typeOfT);
        return obj;
    }

    private <T> List<T> parseJsonToList(String jsonString) {
        List<T> list = new ArrayList<T>();
        try {
            Gson gson = new Gson();
            list = gson.fromJson(jsonString, new TypeToken<List<T>>() {
            }.getType());
        } catch (Exception e) {
        }
        return list;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_register:
                Intent view = new Intent();
                view.setClass(mContext, RegisterActivity.class);
                startActivity(view);
                break;
            case R.id.action_download:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            byte[] result = OkHttpUtils.getBytesFromURL(Constant.URL_DOWNLOAD_IMAGE);
                            Bitmap bm = BitmapFactory.decodeByteArray(result, 0, result.length);
                            //将bm保存进SD卡
                            String fileName = Constant.URL_DOWNLOAD_IMAGE.substring(Constant.URL_DOWNLOAD_IMAGE.lastIndexOf("/") + 1);
                            final boolean flag = SDCardHelper.saveFileToSDCardPrivateCacheDir(result, fileName, mContext);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "结果：" + flag, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        OkHttpClientUtils.cancelCall(mContext, "main");
    }
}
