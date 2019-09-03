package com.HK.dzbly.model;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/8/28$
 * 描述：
 * 修订历史：
 */
public class mDataline {
    private String id;//测量编号
    private String time;//测量时间
    private String Lname;//测量地点名字
    private String Ldistance;//距离

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLname() {
        return Lname;
    }

    public void setLname(String lname) {
        Lname = lname;
    }

    public String getLdistance() {
        return Ldistance;
    }

    public void setLdistance(String ldistance) {
        Ldistance = ldistance;
    }

    @Override
    public String toString() {
        return "mDataline{" +
                "id='" + id + '\'' +
                ", time='" + time + '\'' +
                ", Lname='" + Lname + '\'' +
                ", Ldistance='" + Ldistance + '\'' +
                '}';
    }
}
