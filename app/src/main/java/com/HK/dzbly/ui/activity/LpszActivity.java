package com.HK.dzbly.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.HK.dzbly.R;
import com.HK.dzbly.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/7/24$
 * 描述：罗盘设置
 * 修订历史：
 */
public class LpszActivity extends BaseActivity {
    private EditText threshold;//阀值
    private EditText declination;//磁偏角
    private Button Confirm_settings;//确认设置
    private Button retreat;//退出
    private Spinner placeName, place;
    private List<String> provinces = new ArrayList<String>(); //用来存储省市
    private List<String> city = new ArrayList<>();//用来存储不同省市中的城市
    private Map<String, Object> declinationData = new HashMap<>(); //记录所有的城市对应的地磁偏角
    private String positionValue;
    private SharedPreferences sp = null;
    private int positionCity;
    private int positionProvinces;
    private String value;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.lpsz);
        sp = PreferenceManager.getDefaultSharedPreferences(this);//获取了SharePreferences对象
        Inint();
        declinationData = setDeclinationData();
        value = sp.getString("value", "重庆市");
        declination.setText(value);

        getDataSource();
        setSpinner();
        Deal();
    }

    //获取控件
    private void Inint() {
        threshold = findViewById(R.id.threshold);
        declination = findViewById(R.id.declination);
        Confirm_settings = findViewById(R.id.Confirm_settings);
        retreat = findViewById(R.id.retreat);
        placeName = findViewById(R.id.placeName);
        place = findViewById(R.id.place);
    }

    //相对应的处理
    private void Deal() {
        //处理确认设置
        Confirm_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("positionProvinces", positionProvinces);
                editor.putInt("positionCity", positionCity);
                editor.putString("value", value);
                editor.commit();
                Intent intent = new Intent(LpszActivity.this, DzlpActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        //处理退出
        retreat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //界面跳转，并且清除上一层栈，这样可以保证一个界面不会重复的出现在栈中.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                Intent intent = new Intent(LpszActivity.this, DzlpActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * 设置Spinner，下拉选项
     */
    private void setSpinner() {
        count = 0;
        //获取用户保存的数据
        positionProvinces = sp.getInt("positionProvinces", 0);
        positionCity = sp.getInt("positionCity", 0);

        //列表定义一个数组适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_datatype, provinces);

        //将适配器配置到下拉列表上
        placeName.setAdapter(adapter);
        //设置默认选中
        placeName.setSelection(positionProvinces, true);
        //给下拉菜单设置监听事件
        placeName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                TextView tv = (TextView) view;
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(20.0f);
                count++;
                city = getCity(provinces.get(position));
                //保存当前的省市位置数据
                positionProvinces = position;

                //级联加载城市的Spinner
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getBaseContext(), R.layout.item_datatype, city);
                place.setAdapter(adapter1);
                //给下拉菜单设置监听事件
                place.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position1, long l) {
                        TextView tv = (TextView) view;
                        tv.setTextColor(Color.WHITE);
                        tv.setTextSize(20.0f);
                        //保存当前的城市数据
                        positionCity = position1;
                        positionValue = city.get(position1);
                        value = String.valueOf(declinationData.get(positionValue));
                        declination.setText(value);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (count == 0) {
            city = getCity(provinces.get(positionProvinces));
            //加载城市的Spinner
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getBaseContext(), R.layout.item_datatype, city);
            place.setAdapter(adapter1);
            place.setSelection(positionCity, true);
            //给下拉菜单设置监听事件
            place.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position1, long l) {
                    TextView tv = (TextView) view;
                    tv.setTextColor(Color.WHITE);
                    tv.setTextSize(20.0f);
                    //保存当前的城市数据
                    positionCity = position1;
                    positionValue = city.get(position1);
                    value = String.valueOf(declinationData.get(positionValue));
                    declination.setText(value);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

    }

    private void getDataSource() {
        provinces.add("重庆市");
        provinces.add("陕西省");
        provinces.add("贵州省");
        provinces.add("四川省");
        provinces.add("广西省");
        provinces.add("甘肃省");
        provinces.add("青海省");
        provinces.add("海南省");
        provinces.add("云南省");
        provinces.add("广东省");
        provinces.add("湖南省");
        provinces.add("台湾省");
        provinces.add("新疆省");
        provinces.add("宁夏省");
        provinces.add("西藏");
        provinces.add("香港");
        provinces.add("澳门");
    }

    private List<String> getCity(String provincesName) {
        List<String> list = new ArrayList<String>();
        if (provincesName.equals("重庆市")) {
            list.add("重庆市");
        } else if (provincesName.equals("香港")) {
            list.add("香港");
        } else if (provincesName.equals("澳门")) {
            list.add("澳门");
        } else if (provincesName.equals("四川省")) {
            list.add("攀枝花");
            list.add("成都市");
            list.add("自贡市");
            list.add("泸州市");
            list.add("德阳市");
            list.add("绵阳市");
            list.add("广元市");
            list.add("遂宁市");
            list.add("内江市");
            list.add("资阳市");
            list.add("乐山市");
            list.add("南充市");
            list.add("宜宾市");
            list.add("广安市");
            list.add("达州市");
            list.add("眉山市");
            list.add("巴中市");
            list.add("雅安市");
            list.add("甘孜州");
            list.add("阿坝州");
            list.add("凉山州");
        } else if (provincesName.equals("湖南省")) {
            list.add("株洲市");
            list.add("湘潭市");
            list.add("衡阳市");
            list.add("邵阳市");
            list.add("岳阳市");
            list.add("常德市");
            list.add("张家界");
            list.add("益阳市");
            list.add("郴州市");
            list.add("永州市");
            list.add("怀化市");
            list.add("娄底市");
            list.add("湘西州");
        } else if (provincesName.equals("广东省")) {
            list.add("广州市");
            list.add("深圳市");
            list.add("龙华市");
            list.add("珠海市");
            list.add("汕头市");
            list.add("佛山市");
            list.add("韶关市");
            list.add("河源市");
            list.add("梅州市");
            list.add("惠州市");
            list.add("汕尾市");
            list.add("东莞市");
            list.add("中山市");
            list.add("江门市");
            list.add("阳江市");
            list.add("湛江市");
            list.add("茂名市");
            list.add("肇庆市");
            list.add("清远市");
            list.add("潮州市");
            list.add("云浮市");
        } else if (provincesName.equals("广西省")) {
            list.add("南宁市");
            list.add("崇左市");
            list.add("柳州市");
            list.add("来宾市");
            list.add("桂林市");
            list.add("梧州市");
            list.add("容  县");
            list.add("贵港市");
            list.add("百色市");
            list.add("钦州市");
            list.add("河池市");
            list.add("宜州市");
            list.add("北海市");
            list.add("防城港");
        } else if (provincesName.equals("海南省")) {
            list.add("海口市");
            list.add("三亚市");
            list.add("三沙市");
        } else if (provincesName.equals("贵州省")) {
            list.add("贵阳市");
            list.add("六盘水");
            list.add("遵义市");
            list.add("安顺市");
            list.add("黔南州");
            list.add("黔西南");
            list.add("黔东南");
            list.add("铜仁区");
            list.add("毕节区");
        } else if (provincesName.equals("云南省")) {
            list.add("昆明市");
            list.add("曲靖市");
            list.add("玉溪市");
            list.add("邵通市");
            list.add("楚雄市");
            list.add("红河州");
            list.add("文山州");
            list.add("普洱市");
            list.add("版纳州");
            list.add("大理州");
            list.add("保山市");
            list.add("德宏州");
            list.add("丽江市");
            list.add("怒江市");
            list.add("迪庆市");
            list.add("临沧市");
        } else if (provincesName.equals("陕西省")) {
            list.add("西安市");
            list.add("铜川市");
            list.add("宝鸡市");
            list.add("咸阳市");
            list.add("渭南市");
            list.add("延安市");
            list.add("汉中市");
            list.add("榆林市");
            list.add("安康市");
            list.add("商洛市");
            list.add("杨凌市");
        } else if (provincesName.equals("甘肃省")) {
            list.add("兰州市");
            list.add("嘉峪关");
            list.add("金昌市");
            list.add("白银市");
            list.add("天水市");
            list.add("平凉市");
            list.add("庆阳市");
            list.add("陇南市");
            list.add("定西市");
            list.add("武威市");
            list.add("张掖市");
            list.add("酒泉市");
            list.add("临夏州");
            list.add("甘南市");
        } else if (provincesName.equals("青海省")) {
            list.add("西宁市");
            list.add("格尔木");
            list.add("德令哈");
            list.add("海东州");
            list.add("海北州");
            list.add("海南州");
            list.add("海西州");
            list.add("黄南州");
            list.add("玉树州");
            list.add("果洛州");
        } else if (provincesName.equals("宁夏省")) {
            list.add("银川市");
            list.add("石嘴山");
            list.add("吴忠市");
            list.add("中王市");
            list.add("固原市");
        } else if (provincesName.equals("新疆省")) {
            list.add("乌鲁木齐");
            list.add("克拉玛依");
            list.add("吐鲁番区");
            list.add("哈密地区");
            list.add("昌吉州");
            list.add("博尔塔拉");
            list.add("巴音郭楞");
            list.add("阿克苏区");
            list.add("克孜州");
            list.add("喀什区");
            list.add("和田区");
            list.add("塔城区");
            list.add("阿勒泰区");
            list.add("石河子市");
            list.add("阿拉尔市");
            list.add("图木舒克");
            list.add("五家渠市");
        } else if (provincesName.equals("西藏")) {
            list.add("拉萨市");
            list.add("那曲市");
            list.add("昌都市");
            list.add("林芝市");
            list.add("山南市");
            list.add("日喀则区");
            list.add("阿里区");
        } else {
            list.add("台北市");
            list.add("台中市");
            list.add("高雄市");
            list.add("台南市");
            list.add("台东市");
        }
        return list;
    }

    private Map<String, Object> setDeclinationData() {
        Map<String, Object> map = new HashMap<>();
        map.put("重庆市", 2.38);
        map.put("香港", 2.55);
        map.put("澳门", 2.43);

        map.put("攀枝花", 1.38);
        map.put("成都市", 2.02);
        map.put("自贡市", 2.03);
        map.put("泸州市", 2.12);
        map.put("德阳市", 2.12);
        map.put("绵阳市", 2.20);
        map.put("广元市", 2.53);
        map.put("遂宁市", 2.28);
        map.put("内江市", 2.12);
        map.put("资阳市", 2.07);
        map.put("乐山市", 1.87);
        map.put("南充市", 2.43);
        map.put("宜宾市", 2.05);
        map.put("广安市", 2.50);
        map.put("达州市", 2.75);
        map.put("眉山市", 1.92);
        map.put("巴中市", 2.70);
        map.put("雅安市", 1.78);
        map.put("甘孜州", 1.58);
        map.put("阿坝州", 1.73);
        map.put("凉山州", 1.53);

        map.put("株洲市", 3.33);
        map.put("湘潭市", 3.35);
        map.put("衡阳市", 3.10);
        map.put("邵阳市", 2.97);
        map.put("岳阳市", 3.63);
        map.put("常德市", 3.30);
        map.put("张家界", 3.07);
        map.put("益阳市", 3.35);
        map.put("郴州市", 2.98);
        map.put("永州市", 2.87);
        map.put("怀化市", 2.71);
        map.put("娄底市", 3.13);
        map.put("湘西州", 2.82);

        map.put("广州市", 2.57);
        map.put("深圳市", 2.57);
        map.put("龙华区", 2.59);
        map.put("珠海市", 2.47);
        map.put("汕头市", 3.05);
        map.put("佛山市", 2.52);
        map.put("韶关市", 2.92);
        map.put("河源市", 2.87);
        map.put("梅州市", 3.17);
        map.put("惠州市", 2.72);
        map.put("汕尾市", 2.78);
        map.put("东莞市", 2.62);
        map.put("中山市", 2.47);
        map.put("江门市", 2.45);
        map.put("阳江市", 2.18);
        map.put("湛江市", 1.92);
        map.put("茂名市", 2.03);
        map.put("肇庆市", 2.45);
        map.put("清远市", 2.63);
        map.put("潮州市", 3.12);
        map.put("云浮市", 2.37);

        map.put("南宁市", 1.88);
        map.put("崇左市", 1.72);
        map.put("柳州市", 2.22);
        map.put("来宾市", 2.12);
        map.put("桂林市", 2.48);
        map.put("梧州市", 2.35);
        map.put("容  县", 2.18);
        map.put("贵港市", 2.08);
        map.put("百色市", 1.78);
        map.put("钦州市", 1.82);
        map.put("河池市", 2.08);
        map.put("宜州市", 2.12);
        map.put("北海市", 1.80);
        map.put("防城港", 1.70);

        map.put("海口市", 1.73);
        map.put("三亚市", 1.40);
        map.put("三沙市", 1.38);

        map.put("贵阳市", 2.08);
        map.put("六盘水", 1.82);
        map.put("遵义市", 2.27);
        map.put("安顺市", 1.93);
        map.put("黔南州", 2.18);
        map.put("黔西南", 1.68);
        map.put("黔东南", 2.28);
        map.put("铜仁区", 2.63);
        map.put("毕节区", 1.85);

        map.put("昆明市", 1.42);
        map.put("曲靖市", 1.58);
        map.put("玉溪市", 1.37);
        map.put("邵通市", 1.72);
        map.put("楚雄州", 1.28);
        map.put("红河州", 1.38);
        map.put("文山州", 1.47);
        map.put("普洱市", 1.13);
        map.put("版纳州", 1.08);
        map.put("大理州", 1.18);
        map.put("保山市", 1.07);
        map.put("德宏州", 0.98);
        map.put("丽江市", 1.22);
        map.put("怒江州", 1.08);
        map.put("迪庆州", 1.17);
        map.put("临沧市", 1.12);

        map.put("西安市", 3.48);
        map.put("铜川市", 3.57);
        map.put("宝鸡市", 3.07);
        map.put("咸阳市", 3.43);
        map.put("渭南市", 3.65);
        map.put("延安市", 3.97);
        map.put("汉中市", 2.87);
        map.put("榆林市", 4.27);
        map.put("安康市", 3.28);
        map.put("商洛市", 3.68);
        map.put("杨凌市", 3.48);

        map.put("兰州市", 2.35);
        map.put("嘉峪关", 0.88);
        map.put("金昌市", 2.02);
        map.put("白银市", 2.47);
        map.put("天水市", 2.72);
        map.put("平凉市", 3.03);
        map.put("庆阳市", 3.33);
        map.put("陇南市", 2.42);
        map.put("定西市", 2.52);
        map.put("武威市", 2.12);
        map.put("张掖市", 1.52);
        map.put("酒泉市", 0.95);
        map.put("临夏州", 2.15);
        map.put("甘南市", 2.03);

        map.put("西宁市", 1.83);
        map.put("格尔木", 0.18);
        map.put("德令哈", 0.73);
        map.put("海东州", 1.70);
        map.put("海北州", 1.62);
        map.put("海南州", 1.52);
        map.put("海西州", 0.73);
        map.put("黄南州", 1.85);
        map.put("玉树州", 0.71);
        map.put("果洛州", 1.52);

        map.put("银川市", 3.23);
        map.put("石嘴山", 3.33);
        map.put("吴忠市", 3.17);
        map.put("中王市", 2.82);
        map.put("固原市", 2.98);

        map.put("乌鲁木齐", -2.68);
        map.put("克拉玛依", -3.92);
        map.put("吐鲁番区", -2.08);
        map.put("哈密地区", -0.78);
        map.put("昌吉州", -2.80);
        map.put("博尔塔拉", -4.42);
        map.put("巴音郭楞", -2.58);
        map.put("阿克苏区", -3.58);
        map.put("克孜州", -3.73);
        map.put("喀什区", -3.65);
        map.put("和田区", -2.48);
        map.put("塔城区", -4.85);
        map.put("阿勒泰区", -3.60);
        map.put("石河子市", -3.23);
        map.put("阿拉尔市", -3.22);
        map.put("图木舒克", -3.38);
        map.put("五家渠市", -2.80);

        map.put("拉萨市", -0.12);
        map.put("那曲市", -0.07);
        map.put("昌都市", -0.82);
        map.put("林芝市", -0.47);
        map.put("山南市", -0.22);
        map.put("日喀则区", -0.08);
        map.put("阿里区", -1.42);

        map.put("台北市", 4.00);
        map.put("台中市", 3.71);
        map.put("高雄市", 3.43);
        map.put("台南市", 3.43);
        map.put("台东市", 3.42);

        return map;
    }
}
