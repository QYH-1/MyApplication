package com.HK.dzbly.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.HK.dzbly.R;
import com.HK.dzbly.database.DBhelper;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/8/30$
 * 描述：
 * 修订历史：
 */
public class testActivity extends Activity {
    private Button test1;
    private Button test2;
    public Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test );
        test1 = findViewById(R.id.test1);
        test2 = findViewById(R.id.test2);
        test1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建一个DatabaseHelper对象
                DBhelper dbHelper1 = new DBhelper(testActivity.this, "english.db");
                //取得一个只读的数据库对象
                SQLiteDatabase db1 = dbHelper1.getReadableDatabase();
            }
        });
        test2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //创建存放数据的ContentValues对象
//                ContentValues values = new ContentValues();
//                //像ContentValues中存放数据
//               // values.put("id", 1);
//                values.put("name","zhang");
//                DBHelper dbHelper3 = new DBHelper(StudyWordActivity.this, "english.db");
//                SQLiteDatabase db3 = dbHelper3.getWritableDatabase();
//                //数据库执行插入命令
//                db3.insert("user", null, values);
//                Log.d("插入数据","插入成功");
                DBhelper dbHelper2 = new DBhelper(testActivity.this, "cqhk.db");

                // dbHelper2.DeleteTable(context,"PLAN");
//                dbHelper2.CreateTable(context, "NEWWORDS_BOOK");//调用方法创建，当前单词书的表
               // dbHelper2.DeleteTable(context,"NEWWORDS_BOOK");
                dbHelper2.DeleteTable(context,"NEWWORDS_BOOK");
               // dbHelper2.CreateTable(context,"DATA");
              //  String date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
               // ContentValues cv = new ContentValues();
               // cv.put("time",date1);
               // cv.put("name","第二");
               // cv.put("type","line");
              //  cv.put("distance","11");
               // dbHelper2.Insert(context,"DATA",cv);

//                dbHelper2.DeleteTable(context,"book");
//                dbHelper2.DeleteTable(context,"BOOKS");

//                Log.d("创建", "111");
//                ContentValues cv = new ContentValues();
//                cv.put("ID", 1);
//                cv.put("SPELLING", "as");
//                cv.put("MEANNING", "像");
//                cv.put("PHONETIC_ALPHABET", "as");
//                cv.put("LIST", 1);
//                dbHelper2.Insert(context, bookName, cv);
//                Log.d("插入数据", "插入成功");
                //dbHelper2.DeleteTable(context, bookName);

            }
        });

    }
}
