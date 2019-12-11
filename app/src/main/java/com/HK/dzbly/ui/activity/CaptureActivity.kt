package com.HK.dzbly.ui.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.HK.dzbly.R
import com.HK.dzbly.database.DBhelper
import com.HK.dzbly.utils.drawing.DecodeImgTask
import com.HK.dzbly.utils.file.FileUtil
import kotlinx.android.synthetic.main.activity_capture.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author：qyh
 * 版本：1.0
 * 创建日期：2019/7/31$
 * 描述：相机
 * 修订历史：
 *
 */
class CaptureActivity : Activity() {
    companion object {
        const val AUTHORITY = "com.cs.dzl.fileProvider"
        const val REQUEST_CODE_CAPTURE_SMALL = 1
        const val REQUEST_CODE_CAPTURE_RAW = 2
        const val REQUEST_CODE_CAPTURE = 3
        const val REQUEST_CODE_CAPTURE_CROP = 4
        const val REQUEST_CODE_ALBUM = 5
        const val REQUEST_CODE_VIDEO = 6
        var type = 0
        var imgUri: Uri? = null
        var imageFile: File? = null
        var imageCropFile: File? = null
        var vidoFile: File? = null
        var vidoUri: Uri? = null
        internal var sp: SharedPreferences? = null  //存储对象
        internal lateinit var imageOldPath: String //当前文件地址
        internal lateinit var imageNewPath: String //新的文件地址
        internal lateinit var videoOldPath: String //当前文件地址
        internal lateinit var videoNewPath: String //新的文件地址

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)//隐藏标题栏
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )//隐藏状态栏
        setContentView(com.HK.dzbly.R.layout.activity_capture)

        sp = PreferenceManager.getDefaultSharedPreferences(this)//获取了SharePreferences对象
        btnCaptureRaw.setOnClickListener { gotoCaptureRaw() }        //拍照(返回原始图)
        btnCaptureAndClip.setOnClickListener { gotoCaptureCrop() }   //拍照 + 裁切
        btnCaptureVideo.setOnClickListener { gotoCaptureVideo() }   //录视频 + 播放
        btnCapturesave.setOnClickListener { gotoCaptureSave() } //保存数据

    }

    /**
     * 保存数据
     */
    private fun gotoCaptureSave() {
        var view: View? = null
        if (type == 1) {
            view = LayoutInflater.from(this).inflate(R.layout.layoutjpg, null, false)
        } else {
            view = LayoutInflater.from(this).inflate(R.layout.layoutvideo, null, false)
        }
        val dialog = AlertDialog.Builder(this).setView(view).create()
        val desc1 = view.findViewById<TextView>(R.id.desc1)
        val fileName = view.findViewById<EditText>(R.id.name1)
        //获取当前时间
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        desc1.text = date
        fileName.setText(date)
        AlertDialog.Builder(this)
            .setTitle("系统提示")
            .setView(view)
            .setPositiveButton("确定") { dialogInterface, i ->
                val text = view.findViewById<EditText>(R.id.name1)
                val name = text.text.toString()
                if (type == 1) {
                    imageOldPath = imageFile.toString()
                    var newImageFile = imageOldPath.substring(0, imageOldPath.indexOf("I"))
                    imageNewPath = newImageFile + name + ".jpg"
                    val imageName = name + ".jpg"
                    val file1 = File(imageNewPath)
                    if (file1.exists()) {
                        Toast.makeText(this, "该名称已存在，请重新输入", Toast.LENGTH_SHORT).show()
                    }
                    //将数据存入数据库
                    val dbHelper = DBhelper(this, "cqhk.db")
                    val editor = sp?.edit()
                    //判断数据库是否存在，不存在就创建数据库（0为不存在，1为已经存在）
                    val sqlNumber = sp?.getString("sqlNumber", "0")
                    Log.d("sqlNumber", sqlNumber)
                    if (sqlNumber == "0") {
                        val db3 = dbHelper.getWritableDatabase()
                        editor?.putString("sqlNumber", "1")
                    } else {
                        editor?.putString("sqlNumber", "1")
                        editor?.commit()
                    }
                    if (!dbHelper.IsTableExist("File")) {
                        dbHelper.CreateTable(this, "File")
                    }
                    val cv = ContentValues()
                    cv.put("name", imageName)
                    cv.put("type", "jpg")
                    dbHelper.Insert(this, "File", cv)

                    var file = File(imageOldPath)
                    file.renameTo(File(imageNewPath))
                } else if (type == 2) {
                    videoOldPath = vidoFile.toString()
                    var newVideoFile = videoOldPath.substring(0, videoOldPath.indexOf("V"))
                    videoNewPath = newVideoFile + name + ".mp4"
                    var videoName = name + ".mp4"
                    //将数据存入数据库
                    val dbHelper = DBhelper(this, "cqhk.db")
                    val editor = sp?.edit()
                    //判断数据库是否存在，不存在就创建数据库（0为不存在，1为已经存在）
                    val sqlNumber = sp?.getString("sqlNumber", "0")
                    Log.d("sqlNumber", sqlNumber)
                    if (sqlNumber == "0") {
                        val db3 = dbHelper.getWritableDatabase()
                        editor?.putString("sqlNumber", "1")
                    } else {
                        editor?.putString("sqlNumber", "1")
                        editor?.commit()
                    }
                    if (!dbHelper.IsTableExist("File")) {
                        dbHelper.CreateTable(this, "File")
                    }
                    val cv1 = ContentValues()
                    cv1.put("name", videoName)
                    cv1.put("type", "video")
                    dbHelper.Insert(this, "File", cv1)
                    var file1 = File(videoOldPath)
                    file1.renameTo(File(videoNewPath))
                }


            }.setNegativeButton("取消", null)
            .create()
            .show()

    }

    /**
     * 拍照(返回原始图)
     */
    private fun gotoCaptureRaw() {
        //获取文件存储位置
        imageFile = FileUtil.createImageFile()
        Log.d("照片存储", imageFile.toString());
        imageFile?.let {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val PHOTO_REQUEST_TAKEPHOTO = 1;
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PHOTO_REQUEST_TAKEPHOTO
                )
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

                imgUri = FileProvider.getUriForFile(this, AUTHORITY, it)

                Log.d("照片存储路径", imgUri.toString())
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(it))
            }

            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
            intent.resolveActivity(packageManager)?.let {
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_RAW)
            }
        }
        type = 1;
    }

    // 拍照 + 裁切
    private fun gotoCaptureCrop() {
        imageFile = FileUtil.createImageFile()

        imageFile?.let {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                imgUri = FileProvider.getUriForFile(this, AUTHORITY, it)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(it))
            }

            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
            intent.resolveActivity(packageManager)?.let {
                startActivityForResult(intent, REQUEST_CODE_CAPTURE)
            }
        }
        type = 1;
    }

    //裁剪
    private fun gotoCrop(sourceUri: Uri) {
        imageCropFile = FileUtil.createImageFile(true)
        imageCropFile?.let {

            val intent = Intent("com.android.camera.action.CROP")
            intent.putExtra("crop", "true")
            intent.putExtra("aspectX", 1)    //X方向上的比例
            intent.putExtra("aspectY", 1)    //Y方向上的比例
            intent.putExtra("outputX", 500)  //裁剪区的宽
            intent.putExtra("outputY", 500) //裁剪区的高
            intent.putExtra("scale ", true)  //是否保留比例
            intent.putExtra("return-data", false)
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.setDataAndType(sourceUri, "image/*")  //设置数据源

                var imgCropUri = Uri.fromFile(it)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgCropUri) //设置输出  不需要ContentUri,否则失败
                Log.d("tag", "输入 $sourceUri")
                Log.d("tag", "输出 ${Uri.fromFile(it)}")
            } else {
                intent.setDataAndType(Uri.fromFile(imageFile!!), "image/*")
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(it))
            }
            startActivityForResult(intent, REQUEST_CODE_CAPTURE_CROP)
        }
    }


    //打开系统相册
    private fun gotoGallery() {
        var intent = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, REQUEST_CODE_ALBUM)
    }


    //录制视频
    private fun gotoCaptureVideo() {
        //获取视频的存储位置
        vidoFile = FileUtil.createVideoFile()
        vidoFile?.let {
            var intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)

            vidoUri = FileProvider.getUriForFile(this, AUTHORITY, it)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, vidoUri)
            if (intent.resolveActivity(packageManager) != null)
                startActivityForResult(intent, REQUEST_CODE_VIDEO)
        }
        type = 2;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                REQUEST_CODE_CAPTURE_SMALL -> {
                    data?.data
                    val bitmap = data?.extras?.get("data") as Bitmap
                    ivResult.setImageBitmap(bitmap)
                }

                REQUEST_CODE_CAPTURE_RAW -> { //拍照成功后，压缩图片，显示结果
                    imageFile?.let {
                        DecodeImgTask(ivResult).execute(it.absolutePath)
                    }
                }

                REQUEST_CODE_CAPTURE -> { //拍照成功后，裁剪
                    val sourceUri =
                        FileProvider.getUriForFile(
                            this,
                            AUTHORITY,
                            imageFile!!
                        ) //通过FileProvider创建一个content类型的Uri
                    gotoCrop(sourceUri)
                }

                REQUEST_CODE_CAPTURE_CROP -> {   //裁剪成功后，显示结果
                    imageCropFile?.let {
                        ivResult.setImageBitmap(BitmapFactory.decodeFile(it.absolutePath))
                    }
                }

                REQUEST_CODE_ALBUM -> { //从相册选择照片后，裁剪
                    data?.let {
                        it.data?.let { it1 -> gotoCrop(it1) }
                    }
                }

                REQUEST_CODE_VIDEO -> {   //录制视频成功后播放
                    data?.let {
                        var uri = it.data
                        videoView.visibility = View.VISIBLE
                        videoView.setVideoURI(uri)
                        videoView.start()
                        Log.d("tag", "视频uri $uri")
                    }
                }
            }
        } else {
            Log.d("tag", "错误码 $resultCode")
        }
    }

//    Android7.0以上，相机调用时，intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)，
//    Uri就不能用Uri.fromFile(file)
//    而是要FileProvider.getUriForFile(activity, Constants.FILE_CONTENT_FILEPROVIDER, file);
//    但是裁剪的时候就不一样，裁剪继续使用 Uri.fromFile(file)。

    override fun onStop() {
        if (videoView.isPlaying)
            videoView.pause()
        super.onStop()
    }
}