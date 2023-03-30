package com.dj.common.utils;

import com.dj.pdf.constant.Constant;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PdfBoxUtils {

    /**
     将PDF文件转换为图片
     @param pdfPath PDF文件路径
     @param outDir 输出文件夹路径
     @return 转换后的图片路径列表
     */
    public static List<String> pdf2Image(String pdfPath, String outDir) {
        // 创建用于存储图片路径的列表
        List<String> imagePaths = new ArrayList<>();
        // 加载PDF文档
        try (PDDocument pd = PDDocument.load(new File(pdfPath))){
            // 创建PDF渲染器
            PDFRenderer pr = new PDFRenderer(pd);
            // 获取PDF文档中的页数
            int pages = pd.getNumberOfPages();
            // 循环遍历PDF页数
            for (int i = 0; i < pages; i++) {
                // 渲染PDF页面为图像
                BufferedImage bi = pr.renderImageWithDPI(i, 300);
                // 生成唯一的输出路径，防止重复覆盖
                String outPath = outDir + File.separator + UUID.randomUUID() + Constant.FilePostFix.JPEG;
                // 将渲染后的图像写入输出路径
                ImageIO.write(bi, "JPEG", new File(outPath));
                // 将图片路径添加到列表中
                imagePaths.add(outPath);
            }
        } catch (Exception e) {
            // 抛出运行时异常，传递异常信息
            throw new RuntimeException(e.getMessage());
        }
        // 返回转换后的图片路径列表
        return imagePaths;
    }

}
