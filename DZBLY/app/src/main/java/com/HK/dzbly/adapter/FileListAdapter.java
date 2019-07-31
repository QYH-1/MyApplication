package com.HK.dzbly.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.HK.dzbly.R;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/7/30$
 * 描述：adapter
 * 修订历史：
 */
public class FileListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<File> files;
    private boolean isRoot;
    private LayoutInflater mInflater;

    public FileListAdapter(Context context, ArrayList<File> files, boolean isRoot) {
        this.context = context;
        this.files = files;
        this.isRoot = isRoot;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_file_style, null);
            convertView.setTag(viewHolder);
            viewHolder.title = (TextView) convertView.findViewById(R.id.file_title);
            viewHolder.type = (TextView) convertView.findViewById(R.id.file_type);
            viewHolder.data = (TextView) convertView.findViewById(R.id.file_date);
            viewHolder.size = (TextView) convertView.findViewById(R.id.file_size);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //拿到Item项的文件
        File file = (File) getItem(position);
        //在第一个并且不再根目录我们就把第一个也就是parentFile显示为：“返回上一级”，下一行的都隐藏掉。

        if (position == 0 && !isRoot) {
            viewHolder.title.setText("返回上一级");
            viewHolder.data.setVisibility(View.GONE);
            viewHolder.size.setVisibility(View.GONE);
            viewHolder.type.setVisibility(View.GONE);
        } else {
            //如果不是第一个位置，可以拿到这个文件的一系列信息。
            String fileName = file.getName();
            viewHolder.title.setText(fileName);
            //这个文件是一个文件夹
            if (file.isDirectory()) {
                viewHolder.size.setText("文件夹");
                viewHolder.size.setTextColor(Color.RED);
                viewHolder.type.setVisibility(View.GONE);
                viewHolder.data.setVisibility(View.GONE);
            } else {
                //不是一个文件夹， 可以拿到文件的长度
                long fileSize = file.length();
                if (fileSize > 1024 * 1024) {
                    float size = fileSize / (1024f * 1024f);
                    viewHolder.size.setText(new DecimalFormat("#.00").format(size) + "MB");
                } else if (fileSize >= 1024) {
                    float size = fileSize / 1024;
                    viewHolder.size.setText(new DecimalFormat("#.00").format(size) + "KB");
                } else {
                    viewHolder.size.setText(fileSize + "B");
                }
                //拿到文件的类型
                int dot = fileName.indexOf('.');
                if (dot > -1 && dot < (fileName.length() - 1)) {
                    viewHolder.type.setText(fileName.substring(dot + 1) + "文件");
                }
                //设置文件的最近修改时间。
                viewHolder.data.setText(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(file.lastModified()));
            }
        }
        return convertView;
    }

    class ViewHolder {
        private TextView title;
        private TextView type;
        private TextView data;
        private TextView size;
    }
}
