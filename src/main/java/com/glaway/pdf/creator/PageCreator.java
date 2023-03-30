package com.glaway.pdf.creator;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.glaway.pdf.holder.PageHolder;
import com.glaway.pdf.template.PageTemplate;
import com.glaway.common.utils.FileUtils;
import com.glaway.common.utils.ITextUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * PDF页码生成器
 */
public class PageCreator {

    /**
     *  生成PDF，并添加页码
     * @param template 模板
     * @param holders 页码数据、页码位置、PDF路径
     */
    public void addPageNumber(PageTemplate template, List<PageHolder> holders) {
        String tempPdf = FileUtils.getTempPdf();
        // 合并PDF，并返回合并后的PDF页码
        Map<Integer, PageHolder> holderMap = ITextUtils.mergeWithPage(holders, tempPdf);
        PdfReader reader;
        PdfStamper stamper;
        try {
            // 创建一个pdf读入流
            reader = new PdfReader(tempPdf);
            // 根据一个pdfreader创建一个pdfStamper.用来生成新的pdf.
            stamper = new PdfStamper(reader, Files.newOutputStream(Paths.get(template.getOutPath())));
            BaseFont bf = ITextUtils.getChineseFont();
            // 获取页码
            int num = reader.getNumberOfPages();
            for (int i = 1; i <= num; i++) {
                PageHolder holder = holderMap.get(i);
                if (Objects.isNull(holder)) {
                    continue;
                }
                // 获取总页码位置
                Rectangle totalPageRectangle = holder.getTotalPageRectangle();
                // 获取当前页码位置
                Rectangle numberOfPageRectangle = holder.getNumberOfPageRectangle();
                if (Objects.isNull(totalPageRectangle) && Objects.isNull(numberOfPageRectangle) ) {
                    continue;
                }
                PdfContentByte over = stamper.getOverContent(i);
                over.beginText();
                over.setFontAndSize(bf, template.getFontSize());
                over.setColorFill(BaseColor.BLACK);
                // 设置页码在页面中的坐标
                if (Objects.nonNull(totalPageRectangle)) {
                    over.setTextMatrix(totalPageRectangle.getLeft(8),
                            totalPageRectangle.getBottom(3));
//				over.setTextRenderingMode(1); // 设置字体加粗
                    // 设置文字
                    over.showText(String.valueOf(num));
                }
                if (Objects.nonNull(numberOfPageRectangle)) {
                    over.setTextMatrix(numberOfPageRectangle.getLeft(8),
                            numberOfPageRectangle.getBottom(3));
                    over.showText(String.valueOf(i));
                }
                over.endText();
            }
            stamper.setFormFlattening(template.isFormFlattening());
            ITextUtils.close(stamper, reader);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}
