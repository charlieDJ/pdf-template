package com.glaway.pdf.creator;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.glaway.pdf.data.LoopData;
import com.glaway.pdf.holder.PageHolder;
import com.glaway.pdf.template.LoopTemplate;
import com.glaway.common.utils.FileUtils;
import com.glaway.common.utils.ITextUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.*;

/**
 * PDF内容循环生成器
 */
public class LoopCreator {
    /**
     * 生成PDF
     *
     * @param template 模板
     * @param loopData 数据
     * @return 生成的PDF文件路径，页数
     */
    public PageHolder createPDF(LoopTemplate template, LoopData loopData) {
        check(template);
        Map<String, String> basic = loopData.getBasic();
        List<Map<String, String>> list = loopData.getList();
        // 删除的文件路径
        List<String> deletePaths = new ArrayList<>();
        int maxIndex = template.getMaxIndex();
        String templatePath = template.getPath();
        PageHolder holder = new PageHolder(template.getOutPath());
//        List<Field> basicFields = ReflectionUtils.getFields(basic.getClass());
        List<String> paths = new ArrayList<>();
        int dataSize = 0;
        // 由于不知道数据会占多少篇幅，所以需要一直输出
        while (dataSize < list.size()) {
            // 生成的文件路径
            // 创建临时目录用于存放PDF和图片文件
            String tempPdf = FileUtils.getTempPdf();
            paths.add(tempPdf);
            deletePaths.add(tempPdf);
            PdfReader reader = null;
            PdfStamper stamper = null;
            //使用字体显示中文
            BaseFont chineseFont = ITextUtils.getChineseFont();
            try (OutputStream os = Files.newOutputStream(new File(tempPdf).toPath())) {
                reader = new PdfReader(templatePath);
                stamper = new PdfStamper(reader, os);
                AcroFields s = stamper.getAcroFields();
                // 设置文本域字体
                s.addSubstitutionFont(chineseFont);
                // 保存页码坐标信息
                ITextUtils.setPageRectangle(s, template, holder);
                // 填充基础数据
                ITextUtils.populateFields(s, basic);
                List<Integer> rowSizes = new ArrayList<>();
                int rowIndex = 0;
                for (int i = dataSize; i < list.size(); i++) {
                    Map<String, String> loopMap = list.get(i);
                    for (String field : loopMap.keySet()) {
                        String fieldName = field + template.getSeparator() + rowIndex;
                        AcroFields.Item fieldItem = s.getFieldItem(fieldName);
                        if (Objects.isNull(fieldItem)) {
                            continue;
                        }
                        List<String> texts = new ArrayList<>();
                        float fontSize = ITextUtils.getFontSize(s, fieldName);
                        String value = loopMap.get(field);
                        StringBuilder splitText = new StringBuilder();
                        char[] chars = value.toCharArray();
                        float textWidth = 0f;
                        float fieldWidth = ITextUtils.fieldWidth(s, fieldName);
                        for (int j = 0; j < chars.length; j++) {
                            // 每个字符的宽度
                            float singleWidth = ITextUtils.getTextWidth(fontSize, String.valueOf(chars[j]), chineseFont);
                            // 文本的宽度，返回的英文字符长度略小，向上取整
                            textWidth += Math.ceil(singleWidth);
                            // 文本宽度大于域宽度，需要对这块字符进行收集
                            if (textWidth >= fieldWidth) {
                                texts.add(splitText.toString());
                                splitText = new StringBuilder();
                                textWidth = 0f;
                            }
                            splitText.append(chars[j]);
                            // 最后一行数据
                            if (j == chars.length - 1) {
                                texts.add(splitText.toString());
                            }
                        }
                        for (int j = 0; j < texts.size(); j++) {
                            s.setField(field + (rowIndex + j), texts.get(j));
                        }
                        rowSizes.add(texts.size());
                    }
                    // 一行中有多个单元格换行，取最大的换行数
                    Optional<Integer> max = rowSizes.stream()
                            .max(Comparator.naturalOrder());
                    if (max.isPresent()) {
                        // 换多行，需要跳过多行
                        rowIndex = rowIndex + max.get();
                    } else {
                        rowIndex++;
                    }
                    dataSize++;
                    // 文本域位置已经用光，需要换页
                    if (rowIndex > maxIndex) {
                        break;
                    }
                }
                //如果为false那么生成的PDF文件还能编辑，一定要设为true
                stamper.setFormFlattening(template.isFormFlattening());
                ITextUtils.close(stamper, reader);
            } catch (DocumentException | IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
        ITextUtils.merge(paths, template.getOutPath());
        // 删除临时文件
        deletePaths.addAll(paths);
        FileUtils.delete(deletePaths);
        return holder;
    }

    /**
     * 校验参数
     *
     * @param template 模板
     */
    private void check(LoopTemplate template) {
        String templatePath = template.getPath();
        if (StringUtils.isEmpty(templatePath)) {
            throw new RuntimeException("模板文件不能为空");
        }
        String outPath = template.getOutPath();
        if (StringUtils.isEmpty(outPath)) {
            throw new RuntimeException("输出路径不能为空");
        }
        Integer maxIndex = template.getMaxIndex();
        if (Objects.isNull(maxIndex)) {
            throw new RuntimeException("没有设置最大索引值");
        }
    }

}
