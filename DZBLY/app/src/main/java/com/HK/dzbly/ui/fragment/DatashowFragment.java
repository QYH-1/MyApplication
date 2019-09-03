package com.HK.dzbly.ui.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.HK.dzbly.R;
import com.HK.dzbly.database.DBhelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/8/28$
 * 描述：根据条件显示相对应的数据
 * 修订历史：
 */
public class DatashowFragment extends Fragment {
    private View view;
    private ListView listView_names;
    private ArrayAdapter<String> adapter;
    private int pageNo = 1;          //当前页号
    private int pageSize = 20;      //每页显示的记录数
    private List<HashMap<String, Object>>  data = new ArrayList<HashMap<String, Object>>(); //总数据源
    private int lastVisibleItem;   //最后一个可见的条目
    private boolean isBottom; //判断书否滚动到页面底部，false:没有，true：表示已经滚动到页面底部了
    private  SimpleAdapter simpleAdapter;
    List<HashMap<String, Object>> data1 = new ArrayList<HashMap<String, Object>>();
    private String ditem; //传递过来的数据的类型
    private String titem; //传递过来的时间的节点
    private DBhelper dBhelper;
    private String type; //传递过来的数据在数据库中的类型
    private String dtime; //传递过来的时间的节点的转换

    // 定义用来与外部activity交互，获取到宿主activity
    private FragmentInteraction listterner;
    // 定义所有activity必须实现的接口方法
    public interface FragmentInteraction {
        void process(HashMap<String, Object> str);
    }
    // 当fragmnt被加载到activity的时候会被回调
    @Override
    public void onAttach( Activity activity) {
        super.onAttach(activity);
        if(activity instanceof FragmentInteraction) {
            listterner = (FragmentInteraction)activity; // 2.2 获取到宿主activity并赋值
        } else{
            throw new IllegalArgumentException("activity must implements FragmentInteraction");
        }
    }

    public DatashowFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.datashowfragment, container, false);

        inInt(view);
        DataConversion();
        setData();
        setView(view);
        setListView_names(view);
        return view;
    }

    //获取控件
    private void inInt(View view) {
        this.listView_names = view.findViewById(R.id.listView_names);
        //页脚
        View footerView = View.inflate(getActivity(), R.layout.footer_view, null);
        this.listView_names.addFooterView(footerView);
        this.listView_names.setAdapter(adapter);

    }

    private void setView(View view) {
        //注册滚动监听器对象(当滚动条发生变化时自动调用的方法)
        this.listView_names.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                //当前ListView回归到空闲状态了
                if (lastVisibleItem == simpleAdapter.getCount() && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    pageNo++;
                    loadNextPageData();
                }

            }

            //firstVisibleItem：第一个可见条目的索引值
            // visibleItemCount:当前手机屏幕上显示的记录条数
            // totalItemCount:数据源中的总记录数
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //-1:是为了减去页脚的索引值
                lastVisibleItem = firstVisibleItem + visibleItemCount - 1;
            }
        });
    }

    //为listview适配样式和数据
    private void setListView_names(View view) {
        Log.i("----data1", String.valueOf(data1));
        simpleAdapter = new SimpleAdapter(getActivity(), data1, R.layout.item_datall, new String[]
                {"name", "time"}, new int[]{R.id.nameall, R.id.timeall});
        listView_names.setAdapter(simpleAdapter);
        //设置监听事件
        listView_names.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //获取点击item的数据
                HashMap<String, Object> temp = (HashMap<String, Object>) listView_names.getItemAtPosition(position);
                //Log.i("temp", String.valueOf(temp.get("name")));
              //  Log.i("temp1", String.valueOf(temp.get("time")));
                listterner.process(temp);
            }
        });
    }

    //完成加载下一页的操作
    private void loadNextPageData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //做耗时操作
                SystemClock.sleep(1000);
                List<HashMap<String, Object>> subList = setData();
                //改变数据源，把数据源放到一个更大的list集合中
                data.addAll(subList);
                //不能在子线程中更新UI，所以只能再建立一个主线程
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //此处更新UI
                        simpleAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }
    //获取数据源
    private List<String> getData() {
        List<String> subList = new ArrayList<>();
        for (int i = (pageNo - 1) * pageSize; i < pageNo * pageSize; i++) {
            subList.add("测试" + i);
        }
        return subList;
    }

    private List<HashMap<String, Object>> setData() {
        DBhelper dbHelper2 = new DBhelper(getActivity(), "cqhk.db");
        HashMap<String, Object> item = new HashMap<String, Object>();
        Log.i("------type",type);
        Log.i("------titem",titem);
        data1.clear();
        Log.i("++++++data1", String.valueOf(data1));
        Cursor cursor = dbHelper2.Query(getContext(),"DATA",null,null,null,null,null,null);
        while (cursor.moveToNext()){
           item.put("name",cursor.getString(2));
           Log.i("-----", String.valueOf(item.put("name",cursor.getString(2))));
           item.put("time",cursor.getString(1));
            Log.i("-----", String.valueOf(cursor.getString(1)));
           data1.add(item);
            Log.i("++++++------data1", String.valueOf(data1));
        }
//        for (int i = (pageNo - 1) * pageSize;i<pageNo * pageSize;i++){
//
//            item.put("name","第二巷道产转测量数据展示" );
//            item.put("time", "2019-8-29 174432");
//            data1.add(item);
//        }
        return data1;
    }
    //把传递进来的activity对象释放掉
    @Override
    public void onDetach() {
        super.onDetach();
        listterner = null;
    }
    //获取数据库中的数据
    private void getDBdata(){

    }
    //进行数据的转换
    private void DataConversion(){
        Bundle bundle =this.getArguments();//得到从Activity传来的数据
        if (bundle != null){
            ditem = bundle.getString("ditem");
            titem = bundle.getString("titem");
        }
        Log.i("ditem+++++",ditem);
        Log.i("titem++++++",titem);
        if(ditem.equals("直线测距")){
            type = "line";
        }else  if(ditem.equals("两点测距")){
            type = "twopoint";
        }else if(ditem.equals("地质编录")){
            type = "dzbl";
        }else if(ditem.equals("照片")){
            type = "jpg";
        } else if(ditem.equals("视频")){
            type = "video";
        }else{
            type = "all";
        }
        Log.i("type",type);
    }
}
