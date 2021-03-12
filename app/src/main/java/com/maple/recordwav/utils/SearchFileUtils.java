package com.maple.recordwav.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件检索工具类
 *
 * @author maple
 * @time 2016/5/23
 */
public class SearchFileUtils {
    private static final List<File> list = new ArrayList<File>();

    /**
     * 搜索SD卡文件
     *
     * @param file 需要进行文件搜索的目录
     * @param ext  过滤搜索文件类型
     * @return
     */
    public static List<File> search(File file, String[] ext) {
        list.clear();
        searchFile(file, ext);
        return list;
    }

    private static void searchFile(File file, String[] ext) {
        if (file != null) {
            if (file.isDirectory()) {// 是目录则遍历
                File[] listFile = file.listFiles();// listFiles()可以把当前目录下面的文件和子目录都打出来
                if (listFile != null) {
                    for (File value : listFile) {
                        // 如果目录可读就执行（一定要加，不然会挂掉）
                        if (file.canRead()) {
                            searchFile(value, ext);// 递归查找
                        }
                    }
                }
            } else {
                String filename = file.getAbsolutePath();
                // file.getName();// 加入名称
                // file.getPath();// 加入路径
                // file.length(); // 加入文件大小
                for (String type : ext) {
                    if (filename.endsWith(type)) {
                        list.add(file);
                    }
                }
            }
        }
    }

}
