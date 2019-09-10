package com.HK.dzbly.model;

import org.w3c.dom.Text;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/7/30$
 * 描述：数据的字段
 * 修订历史：
 */
public class data {
    private String name; //文件名
    private Text content;//文件内容
    private String time;//文件创建的事件
    private String size;//文件的大小
    private String type;//文件的类型
    private String folder;//所在文件夹

    private void data(){

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Text getContent() {
        return content;
    }

    public void setContent(Text content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    @Override
    public String toString() {
        return "data{" +
                "name='" + name + '\'' +
                ", content=" + content +
                ", time='" + time + '\'' +
                ", size='" + size + '\'' +
                ", type='" + type + '\'' +
                ", folder='" + folder + '\'' +
                '}';
    }
}
