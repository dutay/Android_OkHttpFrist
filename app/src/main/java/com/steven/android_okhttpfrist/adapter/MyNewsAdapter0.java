package com.steven.android_okhttpfrist.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.steven.android_okhttpfrist.R;

import java.util.List;
import java.util.Map;


/**
 * Created by Steven on 2015/12/8 0008.
 */
public class MyNewsAdapter0 extends BaseAdapterHelper<Map<String, String>> {
    private final static int TYPE1 = 0, TYPE2 = 1;
    private Context context = null;
    private ViewHolder1 mHolder1;
    private ViewHolder2 mHolder2;
    private List<Map<String, String>> list = null;

    public MyNewsAdapter0(List<Map<String, String>> list, Context context) {
        super(list, context);
        this.context = context;
        this.list = list;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        String imageUrl = getImageUrl(list.get(position).get("image"));
        return imageUrl.equals("") ? TYPE2 : TYPE1;
    }

    @Override
    public View getItemView(int position, View convertView, ViewGroup parent, List<Map<String, String>> list, LayoutInflater inflater) {
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE1:
                    convertView = inflater.inflate(R.layout.item_listview_main1, parent, false);
                    mHolder1 = new ViewHolder1(convertView);
                    convertView.setTag(mHolder1);
                    break;
                case TYPE2:
                    convertView = inflater.inflate(R.layout.item_listview_main2,
                            parent, false);
                    mHolder2 = new ViewHolder2(convertView);
                    convertView.setTag(mHolder2);
                    break;
            }
        } else {
            switch (type) {
                case TYPE1:
                    mHolder1 = (ViewHolder1) convertView.getTag();
                    break;
                case TYPE2:
                    mHolder2 = (ViewHolder2) convertView.getTag();
                    break;
            }
        }
        // 给控件赋值
        switch (type) {
            case TYPE1:
                mHolder1.textView_item_content.setText(list.get(position).get("content"));
                mHolder1.textView_item_login.setText(list.get(position).get("login"));
                mHolder1.textView_item_commentscount.setText(list.get(position).get("comments_count")+"");


                final String imageUrl = getImageUrl(list.get(position).get("image"));
                Log.i("MyAdapter", "---->:" + imageUrl);

                // 使用Picasso框架加载图片
                Picasso.with(context).load(Uri.parse(imageUrl))
                        //无淡入淡出，快速加载
                        .noFade()
                                //下载图片的大小
                        //.resize(parent.getWidth(), 0)
                                //.resizeDimen(int targetWidthResId, int targetHeightResId)
                                //图片裁切
                        //.centerInside()
                                //占位图片，就是下载中的图片
                        .placeholder(R.mipmap.ic_empty)
                                //错误图片
                        .error(R.mipmap.ic_launcher)
                                //图片质量
                        .config(Bitmap.Config.RGB_565)

                                //自定义图形转换
                        /*.transform(new Transformation() {
                            @Override
                            public Bitmap transform(Bitmap source) {
                                return null;
                            }

                            @Override
                            public String key() {
                                return null;
                            }
                        })*/
                                //设置Tag标签
                                //.tag()
                                //图片旋转
                                //.rotate()
                                //内存策略
                                //.memoryPolicy()

                        .into(mHolder1.imageView_item_show);


                /*mHolder1.imageView_item_show
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setClass(context, ShowImageActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("url", imageUrl);
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                            }
                        });*/

                break;

            case TYPE2:
                mHolder2.textView_item_content.setText(list.get(position).get("content"));
                mHolder2.textView_item_login.setText(list.get(position).get("login"));
                mHolder2.textView_item_commentscount.setText(list.get(position).get("comments_count")+"");
                break;
        }

        return convertView;
    }

    // 根据图片的名称拼凑图片的网络访问地址
    private String getImageUrl(String imageName) {
        String urlFirst = "", urlSecond = "";
        if (imageName!=null && imageName.indexOf('.') > 0) {
            StringBuilder sb = new StringBuilder();
            if (imageName.indexOf("app") == 0) {
                urlSecond = imageName.substring(3, imageName.indexOf('.'));
                switch (urlSecond.length()) {
                    case 8:
                        urlFirst = imageName.substring(3, 7);
                        break;
                    case 9:
                        urlFirst = imageName.substring(3, 8);
                        break;
                    case 10:
                        urlFirst = imageName.substring(3, 9);
                        break;
                }
            } else {
                urlSecond = imageName.substring(0, imageName.indexOf('.'));
                urlFirst = imageName.substring(0, 6);
            }

            sb.append("http://pic.qiushibaike.com/system/pictures/");
            sb.append(urlFirst);
            sb.append("/");
            sb.append(urlSecond);
            sb.append("/");
            sb.append("small/");
            sb.append(imageName);
            return sb.toString();
        } else {
            return "";
        }
    }

    static class ViewHolder2 {
        private TextView textView_item_content;
        private TextView textView_item_login;
        private TextView textView_item_commentscount;

        public ViewHolder2(View convertView) {
            textView_item_content = ((TextView) convertView.findViewById(R.id.textView_item_content));
            textView_item_login = ((TextView) convertView.findViewById(R.id.textView_item_login));
            textView_item_commentscount = ((TextView) convertView.findViewById(R.id.textView_item_commentscount));
        }
    }

    class ViewHolder1 {
        private ImageView imageView_item_show;
        private TextView textView_item_content;
        private TextView textView_item_login;
        private TextView textView_item_commentscount;

        public ViewHolder1(View convertView) {
            imageView_item_show = (ImageView) convertView.findViewById(R.id.imageView_item_show);
            textView_item_content = ((TextView) convertView.findViewById(R.id.textView_item_content));
            textView_item_login = ((TextView) convertView.findViewById(R.id.textView_item_login));
            textView_item_commentscount = ((TextView) convertView.findViewById(R.id.textView_item_commentscount));
        }
    }

}
