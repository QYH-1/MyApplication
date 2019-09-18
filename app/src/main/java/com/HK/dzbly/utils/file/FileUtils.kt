package com.example.myfilemanager

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.HK.dzbly.adapter.FileInfo
import com.HK.dzbly.ui.activity.FileMainActivity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*


/**
 * @Author：qyh
 * 版本：1.0
 * 创建日期：2019/8/1$
 * 描述：文件管理器工具类
 * 修订历史：
 *
 */
class FileUtils {
    var pathFile = ""
    var adapter: FileAdapter? = null

    companion object {
        lateinit var nameData: String
        var KEY = ""//全局搜索的关键

        /**
         * 获取SDcard根路径
         *
         * @return
         */
        val sdCardPath: String
            get() = Environment.getExternalStorageDirectory().absolutePath


        /**
         * 通过传入的路径,返回该路径下的所有的文件和文件夹列表
         *
         * @param path
         * @return
         */
        fun getListData(path: String): List<FileInfo> {

            val list = ArrayList<FileInfo>()//用来存储便利之后每一个文件中的信息
            val pfile = File(path)// 新建实例化文件对象
            var files: Array<File>? = null// 声明了一个文件对象数组
            if (pfile.exists()) {// 判断路径是否存在
                files = pfile.listFiles()// 该文件对象下所属的所有文件和文件夹列表
            } else {//如果没有这个文件目录。那么这里去创建
                pfile.mkdirs()
                Log.e("name", pfile.absolutePath)
                files = pfile.listFiles()
            }

            if (files != null && files.size > 0) {// 非空验证
                for (file in files) {//循环遍历然后给适配器所要展示的数据赋值。
                    val item = FileInfo()
                    if (file.isHidden) {
                        continue// 跳过隐藏文件
                    }
                    if (file.isDirectory// 文件夹
                        && file.canRead()//是否可读
                    ) {
                        file.isHidden//  是否是隐藏文件
                        // 获取文件夹目录结构
                        item.icon = com.HK.dzbly.R.drawable.folder//图标
                        item.bytesize = file.length()
                        item.size = getSize(item.bytesize.toFloat())//大小
                        item.type = FileMainActivity.T_DIR

                    } else if (file.isFile) {// 文件
                        Log.i("spl", file.name)
                        nameData = file.name
                        Log.i("nameData", nameData)
                        val ext = getFileEXT(file.name)
                        Log.i("spl", "ext=$ext")

                        // 文件的图标
                        item.icon = getDrawableIcon(ext)// 根据扩展名获取图标
                        // 文件的大小
                        val size = getSize(file.length().toFloat())
                        item.size = size
                        item.type = FileMainActivity.T_FILE

                    } else {// 其它
                        item.icon = com.HK.dzbly.R.drawable.mul_file
                    }
                    item.name = file.name// 名称
                    item.lastModify = file.lastModified()
                    item.path = file.path// 路径
                    // 最后修改时间
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
                    val date = Date(file.lastModified())
                    val time = sdf.format(date)
                    item.time = time
                    list.add(item)
                }
            }
            files = null
            return list
        }

        fun getnameData(): String {
            return nameData
        }

        /**
         * 格式转换应用大小 单位"B,KB,MB,GB"
         */

        fun getSize(length: Float): String {

            val kb: Long = 1024
            val mb = 1024 * kb
            val gb = 1024 * mb
            return if (length < kb) {
                String.format("%dB", length.toInt())
            } else if (length < mb) {
                String.format("%.2fKB", length / kb)
            } else if (length < gb) {
                String.format("%.2fMB", length / mb)
            } else {
                String.format("%.2fGB", length / gb)
            }
        }

        fun getSearchResult(list: List<FileInfo>, keyword: String): List<FileInfo> {
            //返回实体集合
            val searchResultList = ArrayList<FileInfo>()
            //循环遍历
            for (i in list.indices) {
                val app = list[i]//拿到单个的实体类
                //拿关键字和实体类比较
                if (app.name.toLowerCase().contains(keyword.toLowerCase())) {

                    searchResultList.add(app)//添加到结果集
                }

            }
            return searchResultList
        }

        /**
         * 截取文件的扩展名
         *
         * @param filename 文件全名
         * @return 扩展名(mp3, txt)
         */
        fun getFileEXT(filename: String): String {
            if (filename.contains(".")) {
                val dot = filename.lastIndexOf(".")// 123.abc.txt
                return filename.substring(dot + 1)
            } else {
                return ""
            }
        }

        /**
         * 检查扩展名end 是否在ends数组中
         *
         * @param end
         * @param ends
         * @return
         */
        fun checkEndsInArray(end: String, ends: Array<String>): Boolean {
            for (aEnd in ends) {
                if (end == aEnd) {
                    return true
                }
            }
            return false
        }

        /**
         * 获得与扩展名对应的图标资源id
         *
         * @param end 扩展名
         * @return
         */
        fun getDrawableIcon(end: String): Int {

            var id = 0
            if (end == "asf") {
                id = com.HK.dzbly.R.drawable.asf
            } else if (end == "avi") {
                id = com.HK.dzbly.R.drawable.avi
            } else if (end == "bmp") {
                id = com.HK.dzbly.R.drawable.bmp
            } else if (end == "doc") {
                id = com.HK.dzbly.R.drawable.doc
            } else if (end == "gif") {
                id = com.HK.dzbly.R.drawable.gif
            } else if (end == "html") {
                id = com.HK.dzbly.R.drawable.html
            } else if (end == "apk") {
                id = com.HK.dzbly.R.drawable.iapk
            } else if (end == "ico") {
                id = com.HK.dzbly.R.drawable.ico
            } else if (end == "jpg") {
                id = com.HK.dzbly.R.drawable.jpg
            } else if (end == "log") {
                id = com.HK.dzbly.R.drawable.log
            } else if (end == "mov") {
                id = com.HK.dzbly.R.drawable.mov
            } else if (end == "mp3") {
                id = com.HK.dzbly.R.drawable.mp3
            } else if (end == "mp4") {
                id = com.HK.dzbly.R.drawable.mp4
            } else if (end == "mpeg") {
                id = com.HK.dzbly.R.drawable.mpeg
            } else if (end == "pdf") {
                id = com.HK.dzbly.R.drawable.pdf
            } else if (end == "png") {
                id = com.HK.dzbly.R.drawable.png
            } else if (end == "ppt") {
                id = com.HK.dzbly.R.drawable.ppt
            } else if (end == "rar") {
                id = com.HK.dzbly.R.drawable.rar
            } else if (end == "txt" || end == "dat" || end == "ini"
                || end == "java"
            ) {
                id = com.HK.dzbly.R.drawable.txt
            } else if (end == "vob") {
                id = com.HK.dzbly.R.drawable.vob
            } else if (end == "wav") {
                id = com.HK.dzbly.R.drawable.wav
            } else if (end == "wma") {
                id = com.HK.dzbly.R.drawable.wma
            } else if (end == "wmv") {
                id = com.HK.dzbly.R.drawable.wmv
            } else if (end == "xls") {
                id = com.HK.dzbly.R.drawable.xls
            } else if (end == "xml") {
                id = com.HK.dzbly.R.drawable.xml
            } else if (end == "zip") {
                id = com.HK.dzbly.R.drawable.zip
            } else if (end == "3gp" || end == "flv") {
                id = com.HK.dzbly.R.drawable.file_video
            } else if (end == "amr") {
                id = com.HK.dzbly.R.drawable.file_audio
            } else {
                id = com.HK.dzbly.R.drawable.default_fileicon
            }
            return id
        }


        /**
         * 打开文件
         *
         * @param context
         * @param aFile
         */
        fun openFile(context: Context, aFile: File) {

            // 实例化意图
            val intent = Intent()
            // 添加动作(干什么?)
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            //取得文件名
            val fileName = aFile.name
            Log.d("打开文件名:", fileName)
            val end = getFileEXT(fileName).toLowerCase()
            Log.d("打开文件名后缀:", end)
            Log.d("打开文件路劲:", aFile.toString())
            if (aFile.exists()) {
                // 根据不同的文件类型来打开文件
                if (checkEndsInArray(end, arrayOf("png", "gif", "jpg", "bmp"))) {
                    // 图片
                    Log.d(
                        "是否找到后缀",
                        (checkEndsInArray(end, arrayOf("png", "gif", "jpg", "bmp"))).toString()
                    )
                    val uriSavedImage =
                        FileProvider.getUriForFile(context, "com.cs.dzl.fileProvider", aFile)
                    Log.d("uriSavedImage", uriSavedImage.toString())
                    intent.setDataAndType(uriSavedImage, "image/*")//MIME TYPE

                } else if (checkEndsInArray(end, arrayOf("apk"))) {
                    // apk
                    val uriSavedImage =
                        FileProvider.getUriForFile(context, "com.cs.dzl.fileProvider", aFile)
                    intent.setDataAndType(uriSavedImage, "application/vnd.android.package-archive")
                } else if (checkEndsInArray(end, arrayOf("mp3", "amr", "ogg", "mid", "wav"))) {
                    // audio
                    val uriSavedImage =
                        FileProvider.getUriForFile(context, "com.cs.dzl.fileProvider", aFile)
                    intent.setDataAndType(uriSavedImage, "audio/*")
                } else if (checkEndsInArray(end, arrayOf("mp4", "3gp", "mpeg", "mov", "flv"))) {
                    // video
                    val uriSavedImage =
                        FileProvider.getUriForFile(context, "com.cs.dzl.fileProvider", aFile)
                    intent.setDataAndType(uriSavedImage, "video/*")
                } else if (checkEndsInArray(
                        end,
                        arrayOf("txt", "ini", "log", "java", "xml", "html")
                    )
                ) {
                    // text
                    val uriSavedImage =
                        FileProvider.getUriForFile(context, "com.cs.dzl.fileProvider", aFile)
                    intent.setDataAndType(uriSavedImage, "text/*")
                } else if (checkEndsInArray(end, arrayOf("doc", "docx"))) {
                    // word
                    val uriSavedImage =
                        FileProvider.getUriForFile(context, "com.cs.dzl.fileProvider", aFile)
                    intent.setDataAndType(uriSavedImage, "application/msword")
                } else if (checkEndsInArray(end, arrayOf("xls", "xlsx"))) {
                    // excel
                    val uriSavedImage =
                        FileProvider.getUriForFile(context, "com.cs.dzl.fileProvider", aFile)
                    intent.setDataAndType(uriSavedImage, "application/vnd.ms-excel")
                } else if (checkEndsInArray(end, arrayOf("ppt", "pptx"))) {
                    // ppt
                    val uriSavedImage =
                        FileProvider.getUriForFile(context, "com.cs.dzl.fileProvider", aFile)
                    intent.setDataAndType(uriSavedImage, "application/vnd.ms-powerpoint")
                } else if (checkEndsInArray(end, arrayOf("chm"))) {
                    // chm
                    val uriSavedImage =
                        FileProvider.getUriForFile(context, "com.cs.dzl.fileProvider", aFile)
                    intent.setDataAndType(uriSavedImage, "application/x-chm")
                } else {
                    // 其他
                    val uriSavedImage =
                        FileProvider.getUriForFile(context, "com.cs.dzl.fileProvider", aFile)
                    intent.setDataAndType(uriSavedImage, "application/$end")
                }
                try {
                    // 发送意图
                    //Log.d("发送意图",true.toString())
                    context.startActivity(intent)

                } catch (e: Exception) {
                    Toast.makeText(context, "没有找到适合打开此文件的应用", Toast.LENGTH_SHORT).show()
                }

            }
        }

        /**
         * 文件分组
         *
         * @param list
         * @return
         */
        fun getGroupList(list: List<FileInfo>): List<FileInfo> {
            val dirs = ArrayList<FileInfo>()// 文件夹列表
            val files = ArrayList<FileInfo>()// 文件列表
            //拆分
            for (i in list.indices) {
                val item = list[i]
                if (item.type == 0) {
                    dirs.add(item)
                } else {
                    files.add(item)
                }
            }
            // 合并
            dirs.addAll(files)// 文件夹+文件

            return dirs
        }

        /**
         * 删除一个文件
         *
         * @param path
         */
        fun deleteFile(path: String) {
            // TODO Auto-generated method stub
            val file = File(path)
            if (file.exists()) {
                file.delete()// 删除
            }
        }

        /**
         * 删除一个文件夹(递归调用)
         *
         * @param path
         */
        fun deleteDir(path: String) {
            // 1. 对自己的子元素进行遍历
            val dir = File(path)// 目标文件夹
            var files: Array<File>? = null// 声明了一个文件对象数组
            if (dir.exists()) {// 判断路径是否存在
                files = dir.listFiles()// 该文件对象下所属的所有文件和文件夹列表
            }

            if (files != null && files.size > 0) {// 非空验证
                for (file in files) {// foreach循环遍历
                    // 2. 对每个子元素进行删除
                    if (file.isFile) {// 删除文件
                        deleteFile(file.absolutePath)
                    }
                    if (file.isDirectory) {// 删除文件夹!!!
                        deleteDir(file.absolutePath)
                    }
                }//for
            }// if
            // 3. 删掉自己
            dir.delete()
        }


        /**
         * 对单个文件的内容的拷贝
         *
         * @param from 待拷贝的原文件对象
         * @param to   目的地文件对象
         */
        fun copyFile(from: File, to: File) {
            var `in`: FileChannel? = null
            var out: FileChannel? = null
            var fis: FileInputStream? = null
            var fos: FileOutputStream? = null
            try {
                fis = FileInputStream(from)
                fos = FileOutputStream(to)
                `in` = fis.channel
                out = fos.channel
                `in`!!.transferTo(0, `in`.size(), out)// 将文件通过out传输到目标文件
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (fis != null && fos != null && `in` != null && out != null) {

                    try {
                        fis.close()
                        fos.close()
                        `in`.close()
                        out.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }

        /**
         * 在给定的文件夹目录下,拷入目标文件
         *
         * @param targetDir 目标文件夹的路径
         * @param file      待考的文件对象
         * @return
         */
        fun pasteFile(targetDir: String, file: File): Int {
            // 生成目的地文件对象  新的文件夹/abc.txt
            val newFile = File(targetDir, file.name)
            if (newFile.exists()) {
                // Toast.makeText(this,newPath+"文件已存在",Toast.LENGTH_SHORT).show();
                return 1// 没有成功
            } else {
                // 没有重名文件
                try {
                    if (file.isFile) {
                        newFile.createNewFile()// 新创建文件(空)

                    } else if (file.isDirectory) {

                        newFile.mkdirs()
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            copyFile(file, newFile)//<------------------ call upper method

            return 0// OK
        }

        /**
         * 在给定的文件夹目录下,粘贴上之前标记的文件夹中所有的内容(递归)
         * @param targetDir 目标文件夹的路径
         * @param dir 待考的文件夹对象
         * @return
         */
        fun pasteDir(targetDir: String, dir: File): Int {
            // 生成目的地文件对象  targetDir=/temp, dir = abc
            //newDir = /temp/abc/....
            val newDir = File(targetDir, dir.name)
            // 生成这个newDir所对应的路径
            newDir.mkdirs()

            var files: Array<File>? = null// 声明了一个文件对象数组
            if (dir.exists()) {// 判断路径是否存在
                files = dir.listFiles()// 该文件对象下所属的所有文件和文件夹列表
            }

            if (files != null && files.size > 0) {// 非空验证
                for (file in files) {// foreach循环遍历
                    // 2. 对每个子元素进行
                    if (file.isFile) {// 复制文件
                        pasteFile(newDir.absolutePath, file)
                    }
                    if (file.isDirectory) {// 复制文件夹!!!
                        pasteDir(newDir.absolutePath, file)// 递归调用
                    }
                }//for
            }// if

            return 0// OK
        }
    }
}
