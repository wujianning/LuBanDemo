package com.wjn.lubandemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.wjn.lubandemo.R;
import com.wjn.lubandemo.bean.PictureBean;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    /**
     * 构造方法
     * */

    private List<PictureBean> list;
    private Context context;
    private Activity activity;
    private LayoutInflater mInflater;

    public RecyclerViewAdapter(List<PictureBean> list,Context context,Activity activity){
        this.list=list;
        this.context=context;
        this.activity=activity;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view=mInflater.inflate(R.layout.recyclerview_listview, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.textView1.setText(list.get(i).getOriginArg());
        viewHolder.textView2.setText(list.get(i).getThumbArg());
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .placeholder(R.mipmap.followup_def)
                .error(R.mipmap.followup_def);
        Glide.with(activity)
                .load(list.get(i).getPath())
                .apply(options)
                .into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * ViewHolder类
     * */

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textView1;
        private TextView textView2;
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.recyclerview_listview_item_textview1);
            textView2 = itemView.findViewById(R.id.recyclerview_listview_item_textview2);
            imageView = itemView.findViewById(R.id.recyclerview_listview_item_imageview);
        }
    }

}
