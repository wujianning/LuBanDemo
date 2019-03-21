package com.wjn.lubandemo.activity;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.wjn.lubandemo.R;
import com.wjn.lubandemo.adapter.RecyclerViewAdapter;
import com.wjn.lubandemo.bean.PictureBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private RecyclerView recyclerView;
    private List<PictureBean> list;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * 初始化各种View
     * */

    private void initView(){
        textView=findViewById(R.id.activity_main_textview);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,LuBanActivity.class);
                startActivity(intent);
            }
        });
        recyclerView=findViewById(R.id.activity_main_recycleview);
        list=new ArrayList<>();

        PictureBean pictureBean1=new PictureBean();
        pictureBean1.setOriginArg("原图参数:1920*1080,1453K");
        pictureBean1.setThumbArg("压缩后参数:1920*1080,145K");
        pictureBean1.setImage(R.mipmap.jpg1);
        pictureBean1.setPath("http://cn.bing.com/az/hprichbg/rb/Dongdaemun_ZH-CN10736487148_1920x1080.jpg");

        PictureBean pictureBean2=new PictureBean();
        pictureBean2.setOriginArg("原图参数:480*360,780K");
        pictureBean2.setThumbArg("压缩后参数:480*360,123K");
        pictureBean2.setImage(R.mipmap.jpg2);
        pictureBean2.setPath("https://www.baidu.com/img/bd_logo1.png");

        PictureBean pictureBean3=new PictureBean();
        pictureBean3.setOriginArg("原图参数:3120*1440,1780K");
        pictureBean3.setThumbArg("压缩后参数:3120*1440,303K");
        pictureBean3.setImage(R.mipmap.jpg3);
        pictureBean3.setPath("http://docs-aliyun.cn-hangzhou.oss.aliyun-inc.com/assets/pic/49549/AntCloud_zh/1540278756543/mpaas_benifits_client.png");

        list.add(pictureBean1);
        list.add(pictureBean2);
        list.add(pictureBean3);
        list.add(pictureBean1);
        list.add(pictureBean2);
        list.add(pictureBean3);
        list.add(pictureBean1);
        list.add(pictureBean2);
        list.add(pictureBean3);
        list.add(pictureBean1);
        list.add(pictureBean2);
        list.add(pictureBean3);
        list.add(pictureBean1);
        list.add(pictureBean2);
        list.add(pictureBean3);

//        lubanMethod(list);

        //2.设置LinearLayoutManager ListView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //3.设置ItemAnimator
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //4.设置固定大小
        recyclerView.setHasFixedSize(true);
        adapter = new RecyclerViewAdapter(list,this,this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * luban压缩图片
     * */

    private void lubanMethod(List<PictureBean> list){
        Luban.with(this)//上下文对象
                .load(list)//原图路径
                .ignoreBy(100)//不压缩的阈值 单位为K 即图片小于此阈值时不压缩
                .setFocusAlpha(false)//设置是否保留透明通道
                .setTargetDir(setLuBanPath())//设置缓存压缩图片路径
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path));//图片路径不为空 返回true压缩
                    }
                })//设置开启压缩条件
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.d("TAG","路径:"+file.getAbsolutePath());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                })//压缩回调接口
                .setRenameListener(new OnRenameListener() {
                    @Override
                    public String rename(String filePath) {
                        return null;
                    }
                })//压缩前重命名接口
                .launch();
    }

    /**
     * 设置Luban压缩图片后的路径
     * */

    private String setLuBanPath() {
        String path = Environment.getExternalStorageDirectory() + "/MyLuBan/image/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }

}
