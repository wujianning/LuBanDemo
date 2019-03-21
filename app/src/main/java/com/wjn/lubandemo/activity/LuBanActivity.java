package com.wjn.lubandemo.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
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
import java.util.Locale;

import me.nereo.multi_image_selector.MultiImageSelector;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

public class LuBanActivity extends AppCompatActivity {

    private TextView textView;
    private RecyclerView recyclerView;
    private List<PictureBean> list;
    private RecyclerViewAdapter adapter;
    private static final int REQUEST_IMAGE = 2;
    private boolean showCamera = false;//是否显示相机 false 不支持相机
    private int maxNum = 6;//最大图片个数
    private ArrayList<String> mSelectPath;
    private int savenum=001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luban);
        initView();
    }

    /**
     * 初始化各种View
     * */

    private void initView(){
        textView=findViewById(R.id.activity_luban_textview);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               pickImage();
            }
        });

        recyclerView=findViewById(R.id.activity_luban_recyclerview);
        list=new ArrayList<>();
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
     * 选取图片
     * */

    private void pickImage() {
        MultiImageSelector selector = MultiImageSelector.create(LuBanActivity.this);
        selector.showCamera(showCamera);
        selector.count(maxNum);
        if (showCamera) {
            selector.single();
        } else {
            selector.multi();
        }
        selector.origin(mSelectPath);
        selector.start(LuBanActivity.this, REQUEST_IMAGE);
    }

    /**
     * onActivityResult 接收图片回传路径
     * */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE://多张图片选中
                if (resultCode == RESULT_OK) {
                    mSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                    int num = mSelectPath.size();
                    if (num > 0) {//有数据
                        list.removeAll(list);
                        for (int i = 0; i < num; i++) {
                            String path=mSelectPath.get(i);
                            computeSize(path);
                            Log.d("TAG","原生路径:"+path);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    /**
     * 确定文件大小
     * */

    private void computeSize(String path) {
        File file=new File(path);
        int[] size = new int[2];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;

        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        size[0] = options.outWidth;
        size[1] = options.outHeight;

        String originArg = String.format(Locale.CHINA, "图片参数：%d*%d, %dk", size[0], size[1], file.length() >> 10);
        String thumbArg = String.format(Locale.CHINA, "图片参数：%d*%d, %dk", size[0], size[1], file.length() >> 10);

        PictureBean pictureBean=new PictureBean();
        pictureBean.setPath(path);
        pictureBean.setOriginArg(originArg);
        pictureBean.setThumbArg(thumbArg);

        list.add(pictureBean);
        adapter.notifyDataSetChanged();

        Log.d("TAG", "原始outWidth----:"+size[0]);
        Log.d("TAG", "原始outHeight----:"+size[1]);
        Log.d("TAG", "原始大小----:"+(file.length() >> 10)+"K");

        lubanMethod(file);
    }

    /**
     * luban压缩图片
     * */

    private void lubanMethod(File file){
        Luban.with(this)//上下文对象
                .load(file)//原图路径
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
                        addthumbArg(file);
                        Log.d("TAG","压缩成功后的路径:"+file.getAbsolutePath());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                })//压缩回调接口
                .setRenameListener(new OnRenameListener() {
                    @Override
                    public String rename(String filePath) {
                        String result=savenum+".jpg";
                        savenum++;
                        return result;
                    }
                })//压缩前重命名接口
                .launch();//压缩
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

    /**
     * 确定文件大小
     * */

    private void addthumbArg(File file) {
        int[] size = new int[2];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;

        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        size[0] = options.outWidth;
        size[1] = options.outHeight;

        String originArg = String.format(Locale.CHINA, "压缩后图片参数：%d*%d, %dk", size[0], size[1], file.length() >> 10);
        String thumbArg = String.format(Locale.CHINA, "压缩后图片参数：%d*%d, %dk", size[0], size[1], file.length() >> 10);

        PictureBean pictureBean=new PictureBean();
        pictureBean.setPath(file.getAbsolutePath());
        pictureBean.setOriginArg(originArg);
        pictureBean.setThumbArg(thumbArg);

        list.add(pictureBean);
        adapter.notifyDataSetChanged();

        Log.d("TAG", "压缩outWidth----:"+size[0]);
        Log.d("TAG", "压缩outHeight----:"+size[1]);
        Log.d("TAG", "压缩大小----:"+(file.length() >> 10)+"K");
    }

}
