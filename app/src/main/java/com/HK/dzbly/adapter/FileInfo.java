package com.HK.dzbly.adapter;

/**
 * @Author：qyh
 * 版本：1.0
 * 创建日期：2019/8/1$
 * 描述：文件适配器
 * 修订历史：
 *
 */
public class FileInfo {
    public int icon;
    public String name;
    public String size;
    public String time;
    public String path;
    public int type;
    public long lastModify;
    public long bytesize;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getLastModify() {
        return lastModify;
    }

    public void setLastModify(long lastModify) {
        this.lastModify = lastModify;
    }

    public long getBytesize() {
        return bytesize;
    }

    public void setBytesize(long bytesize) {
        this.bytesize = bytesize;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "icon=" + icon +
                ", name='" + name + '\'' +
                ", size='" + size + '\'' +
                ", time='" + time + '\'' +
                ", path='" + path + '\'' +
                ", type=" + type +
                ", lastModify=" + lastModify +
                ", bytesize=" + bytesize +
                '}';
    }
}
