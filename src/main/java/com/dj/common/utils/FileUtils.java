package com.dj.common.utils;

import com.dj.pdf.constant.Constant;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * 文件工具类
 */
public class FileUtils {

    /**
     * 获取当前工作目录的路径
     *
     * @return 当前工作目录的路径
     */
    public static String getCurrentDir() {
        // 创建文件对象，代表当前目录
        File directory = new File(".");

        // 获取当前目录的路径
        String dir = "";
        try {
            dir = directory.getCanonicalPath();
        } catch (IOException e) {
            // 当出现异常时，打印错误信息
            e.printStackTrace();
        }

        // 返回当前目录路径
        return dir;
    }

    /**
     * 创建指定路径的目录，如果不存在则创建
     *
     * @param dir 待创建目录的路径
     */
    public static void mkdirs(String dir) {
        // 创建File对象，表示待创建的目录
        File file = new File(dir);
        // 如果目录不存在，则创建该目录
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 删除指定路径的文件
     *
     * @param paths 待删除文件路径列表
     */
    public static void delete(List<String> paths) {
        // 遍历待删除的文件路径列表
        for (String path : paths) {
            try {
                // 使用Files类删除指定路径的文件。如果文件不存在则不做处理
                Files.deleteIfExists(Paths.get(path));
            } catch (IOException e) {
                // 如果删除失败，则打印错误信息
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取一个临时PDF文件的路径
     *
     * @return 临时PDF文件的路径
     */
    public static String getTempPdf() {
        // 获取当前工作目录的路径
        String currentDir = FileUtils.getCurrentDir();
        // 临时PDF文件目录为当前工作目录 + "temp/pdf"，确保该目录存在
        String tempPdfDir = currentDir + File.separator + Constant.TempDirectory.PDF;
        FileUtils.mkdirs(tempPdfDir);
        // 返回一个随机生成的PDF文件名（使用UUID）和文件路径
        return tempPdfDir + File.separator + UUID.randomUUID() + Constant.FilePostFix.PDF;
    }

    /**
     * 获取一个临时目录用于存放图片文件
     *
     * @return 临时目录的路径
     */
    public static String getTempImageDir() {
        // 以当前工作目录和"temp/jpeg"为基础路径创建目录用于存放图片
        String tempImageDir = FileUtils.getCurrentDir() + File.separator + Constant.TempDirectory.JPEG;
        FileUtils.mkdirs(tempImageDir);
        // 返回临时目录的路径
        return tempImageDir;
    }
}
