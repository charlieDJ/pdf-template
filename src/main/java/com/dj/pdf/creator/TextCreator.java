package com.dj.pdf.creator;

import com.dj.common.utils.ITextUtils;
import com.dj.pdf.holder.PageHolder;
import com.dj.pdf.template.TextTemplate;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Map;

/**
 * 文本PDF生成器
 *
 */
public class TextCreator {

    /**
     * 生成PDF
     *
     * @param template 模板
     * @param basic   基本信息
     * @return 生成的PDF文件路径，页数
     */
    public PageHolder createPdf(TextTemplate template, Map<String,String> basic) {
        check(template);
        String outPath = template.getOutPath();
        //使用字体显示中文
        BaseFont chineseFont = ITextUtils.getChineseFont();
        PdfReader reader;
        PdfStamper stamper;
        PageHolder holder = new PageHolder(outPath);
        try (OutputStream os = Files.newOutputStream(new File(outPath).toPath())) {
            reader = new PdfReader(template.getPath());
            stamper = new PdfStamper(reader, os);
            AcroFields s = stamper.getAcroFields();
            // 保存页码矩形
            ITextUtils.setPageRectangle(s, template, holder);
            // 设置文本域字体
            s.addSubstitutionFont(chineseFont);
            ITextUtils.populateFields(s, basic);
            //如果为false那么生成的PDF文件还能编辑，一定要设为true
            stamper.setFormFlattening(template.isFormFlattening());
            ITextUtils.close(stamper, reader);
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
        return holder;
    }


    /**
     * 检查模板
     *
     * @param template 模板
     */
    private void check(TextTemplate template) {
        String templatePath = template.getPath();
        if (StringUtils.isEmpty(templatePath)) {
            throw new RuntimeException("模板文件不能为空");
        }
        String outPath = template.getOutPath();
        if (StringUtils.isEmpty(outPath)) {
            throw new RuntimeException("输出路径不能为空");
        }
    }

}
