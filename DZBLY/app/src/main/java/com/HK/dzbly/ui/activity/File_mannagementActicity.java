package com.HK.dzbly.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.HK.dzbly.R;
import com.HK.dzbly.adapter.FileListAdapter;
import com.HK.dzbly.ui.base.BaseActivity;

import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;

/**
 * @Author：qyh 版本：1.0
 * 创建日期：2019/7/30$
 * 描述：文件管理器
 * 修订历史：
 */
public class File_mannagementActicity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView mListView;
    private TextView mPathView;
    private FileListAdapter mFileAdpter;
    private TextView mItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_management);
        initView();//获取控件
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.file_list);
        mPathView = (TextView) findViewById(R.id.path);
        mItemCount = (TextView) findViewById(R.id.item_count);
        mListView.setOnItemClickListener(this);
        //添加apk的权限，777 表示可读可写可操作
        String apkRoot = "chmod 777 " + getPackageCodePath();
        Log.d("文件路径：",apkRoot);
        RootCommand(apkRoot);
        File folder = new File("/");
        initData(folder);
    }

    /**
     * 修改Root权限
     * @param command
     * @return
     */
    public static boolean RootCommand(String command) {
        boolean status = false;
        if (TextUtils.isEmpty(command)) {
            return status;
        }
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }
//    public static boolean RootCommand(String command) {
//        boolean status = false;
//        if (TextUtils.isEmpty(command)) {
//            return status;
//        }
//        try {
//            Process exec = Runtime.getRuntime().exec("su");
//            OutputStream outputStream = exec.getOutputStream();
//            outputStream.write(command.getBytes(Charset.forName("utf-8")));
//            outputStream.write("\n".getBytes());
//            outputStream.write("exit\n".getBytes());
//            outputStream.flush();
//            int waitFor = exec.waitFor();
//            Log.e(TAG, "execCommand command:"+command+";waitFor=" + waitFor);
//            if (waitFor == 0) {
//                //chmod succeed
//                status = true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TAG, "execCommand exception=" + e.getMessage());
//            return false;
//        }
//        return status;
//    }

    /**
     * 获取根目录下面的所有的数据，然后设置到我们的ListView中让它显示出来
     * @param folder
     */
    private void initData(File folder) {
        //获取当前是否是根目录，然后把文件的路径设置给我们要显示的View
        boolean isRoot = folder.getParent() == null;
        mPathView.setText(folder.getAbsolutePath());
        //用一个ArrayList来装我们目录下的所有的文件或者文件夹
        ArrayList<File> files = new ArrayList<File>();
        if (!isRoot) {
            //把这个文件夹的父类装到我们的列表中去
            files.add(folder.getParentFile());
        }
        //这个文件夹下的子文件都拿到，也装在列表中
        File[] filterFiles = folder.listFiles();
        if(filterFiles != null) {
            mItemCount.setText(filterFiles.length + "项");
        }
        if (null != filterFiles && filterFiles.length > 0) {
            for (File file : filterFiles) {
                files.add(file);
            }
        }
        //利用Adapter显示文件夹
        mFileAdpter = new FileListAdapter(this, files, isRoot);
        mListView.setAdapter(mFileAdpter);
    }
    /**
     * 设置每个Item项的点击事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File file = (File) mFileAdpter.getItem(position);
        if (!file.canRead()) {
            new AlertDialog.Builder(this).setTitle("提示").setMessage("权限不足").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
        } else if (file.isDirectory()) {
            initData(file);
        } else {
            openFile(file);
        }
    }

    /**
     * 打开文件
     * @param file
     */
    private void openFile(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = getMIMEType(file);
        //打开设置打开文件的类型
        intent.setDataAndType(Uri.fromFile(file), type);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "未知类型，不能打开", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 文件的后缀名拿到文件的MIMEType类型
     * @param file
     * @return
     */
    private String getMIMEType(File file) {
        //弹出所有的可供选择的应用程序
        String type = "*/*";
        String fileName = file.getName();
        int dotIndex = fileName.indexOf('.');
        if (dotIndex < 0) {
            return type;
        }
        String end = fileName.substring(dotIndex, fileName.length()).toLowerCase();
        if (end == "") {
            return type;
        }
        //先遍历后缀名，如果找到，就把对应的类型找到并返回
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end == MIME_MapTable[i][0]) {
                type = MIME_MapTable[i][1];
            }
        }
        //返回文件类型
        return type;
    }

    /**
     * 分别对应的是后缀名和对应的文件类型
     */
    private final String[][] MIME_MapTable = {
            // {后缀名， MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };
}
