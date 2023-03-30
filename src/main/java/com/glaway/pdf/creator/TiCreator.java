package com.glaway.pdf.creator;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.glaway.pdf.data.MixData;
import com.glaway.pdf.data.TiData;
import com.glaway.pdf.template.TiTemplate;
import com.glaway.common.utils.FileUtils;
import com.glaway.common.utils.ITextUtils;
import com.glaway.common.utils.PdfBoxUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 文本和图片混合生成器
 *
 */
public class TiCreator {

    /**
     * 生成PDF文件
     *
     * @param tiTemplate 模板对象
     * @param data       数据对象
     */
    public void createPDF(TiTemplate tiTemplate, TiData data) {
        List<String> deletePaths = new ArrayList<>();
        // 检查参数
        check(tiTemplate);
        // 获取文本域名称
        String textField = tiTemplate.getTextField();
        // 获取文本域和图表数据对象
        List<MixData> dataList = data.getMixData();
        // 获取模板文件路径
        String templatePath = tiTemplate.getPath();
        // 获取所有的图片路径
        List<String> images = data.getImages();
        // 获取文本域的宽度和高度
        List<Float> widthAndHeight = ITextUtils.getWidthAndHeight(templatePath, textField);
        // 生成随机的PDF文件名
        String tempPdfPath = FileUtils.getTempPdf();
        // 将数据对象和文本域宽度高度信息写入PDF文件中
        ITextUtils.createComplexPdf(tempPdfPath, dataList, widthAndHeight);
        // 将PDF文件路径添加到需要删除的文件列表中
        deletePaths.add(tempPdfPath);
        String tempImageDir = FileUtils.getTempImageDir();
        // 将PDF文件转换为图片
        List<String> textImagePaths = PdfBoxUtils.pdf2Image(tempPdfPath, tempImageDir);
        // 将所有生成的图片文件路径添加到需要删除的文件列表中
        deletePaths.addAll(textImagePaths);
        // 设置循环图片标识
        boolean isLoopImage = true;
        // 创建一个PDF文件路径列表
        List<String> pdfPaths = new ArrayList<>();
        //使用微软雅黑字体显示中文
        BaseFont chineseFont = ITextUtils.getChineseFont();
        for (String textImagePath : textImagePaths) {
            // 生成的文件路径
            String outputFileName = FileUtils.getTempPdf();
            PdfReader reader;
            PdfStamper stamper;
            try (OutputStream os = Files.newOutputStream(new File(outputFileName).toPath())) {
                // 读取源PDF文件，不能重复使用，必须重复创建对象才能使用
                reader = new PdfReader(templatePath);
                // 写入目标PDF文件
                stamper = new PdfStamper(reader, os);
                // 获取表单
                AcroFields s = stamper.getAcroFields();
                // 设置文本域字体
                s.addSubstitutionFont(chineseFont);
                // 插入文本域
                Map<String, String> basic = data.getBasic();
                for (String field : basic.keySet()) {
                    AcroFields.Item fieldItem = s.getFieldItem(field);
                    if (Objects.isNull(fieldItem)) {
                        continue;
                    }
                    s.setField(field, basic.get(field));
                }
                //插入图片/
                // 只插入一次背景图片
                if (isLoopImage) {
                    ITextUtils.placeImage(stamper, tiTemplate.getImageField(), images.get(0));
                    isLoopImage = false;
                }
                // 插入文本域与图片
                ITextUtils.placeImage(stamper, textField, textImagePath);
                // 如果为false那么生成的PDF文件还能编辑，一定要设为true
                stamper.setFormFlattening(tiTemplate.isFormFlattening());
                ITextUtils.close(stamper, reader);
            } catch (DocumentException | IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
            pdfPaths.add(outputFileName);
        }
        // 合并所有生成的PDF文件
        ITextUtils.merge(pdfPaths, tiTemplate.getOutPath());
        // 将所有生成的PDF和图片文件添加到需要删除的文件列表中
        deletePaths.addAll(pdfPaths);
        FileUtils.delete(deletePaths);
    }

    /**
     * 检查参数
     *
     * @param tiTemplate 模板对象
     */
    private void check(TiTemplate tiTemplate) {
        String templatePath = tiTemplate.getPath();
        if (StringUtils.isEmpty(templatePath)) {
            throw new RuntimeException("模板文件不能为空");
        }
        String textField = tiTemplate.getTextField();
        if (StringUtils.isEmpty(textField)) {
            throw new RuntimeException("文本域为空，请检查PDF模板，模板文件地址：" + templatePath);
        }
        String outPath = tiTemplate.getOutPath();
        if (StringUtils.isEmpty(outPath)) {
            throw new RuntimeException("输出路径不能为空");
        }
    }


}
