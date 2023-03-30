package com.itext;

import com.dj.pdf.creator.LoopCreator;
import com.dj.pdf.creator.PageCreator;
import com.dj.pdf.creator.TextCreator;
import com.dj.pdf.creator.TiCreator;
import com.dj.pdf.data.*;
import com.dj.pdf.holder.PageHolder;
import com.dj.pdf.template.LoopTemplate;
import com.dj.pdf.template.PageTemplate;
import com.dj.pdf.template.TextTemplate;
import com.dj.pdf.template.TiTemplate;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.jsoup.Jsoup;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PDFTest {

    private List<String> texts = new ArrayList<>();

    @SneakyThrows
    @Before
    public void setUp() {
        String path = "D:\\processCardPreviewForder\\110\\Template\\操作步骤.txt";
        texts = Files.readAllLines(Paths.get(path));
    }


    @SneakyThrows
    @Test
    public void createPdf() {
        //new一个Document对象,设置纸张大小
        Rectangle rectangle = new Rectangle(324, 262);
        Document document = new Document(rectangle);
        //解析器
        PdfWriter writer = PdfWriter.getInstance(document, Files.newOutputStream(Paths.get("D:/temp/text.pdf")));
        /*页边距*/
        document.setMargins(2, 2, 2, 2);
        // 将字体文件添加到 PDF 文档中
        //打开document
        document.open();

        BaseFont font = BaseFont.createFont("\\font\\华文宋体.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font cFont = new Font(font, 10, Font.NORMAL, BaseColor.BLACK);
        for (String text : texts) {
            /***添加新段落***/
            document.add(new Paragraph(text, cFont));
        }
        document.close();
    }

    @SneakyThrows
    @Test
    public void createPdfWithImage() {
        String templatePath = "D:\\processCardPreviewForder\\110\\Template\\工序图表.pdf";
        TiTemplate tiTemplate = new TiTemplate();
        List<MixData> list = new ArrayList<>();
        org.jsoup.nodes.Document html = Jsoup.parse(new File("D:\\processCardPreviewForder\\110\\Template\\新文件 19.html"), "utf-8");
        MixData data = new MixData();
        data.setType(1)
                .setTexts(texts);
        MixData data2 = new MixData();
        data2.setType(3)
                .setDocument(html);
        list.add(data2);
        list.add(data);
        MixData mixData = new MixData();
        // 使用arraylist构造5行5列的数据
        Table table = new Table();
        for (int i = 0; i < 6; i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < 6; j++) {
                row.add("第" + i + "行，第" + j + "列");
            }
            table.addRow(row);
        }
        mixData.setType(2)
                .setTable(table);
        list.add(mixData);
        List<String> images = new ArrayList<>();
        images.add("C:\\Users\\左右\\Pictures\\Saved Pictures\\荷塘.jpg");
        tiTemplate.setImageField("image")
                .setTextField("text")
                .setPath(templatePath)
                .setOutPath("D:\\temp\\pdf\\merge.pdf");
        TiData tiData = new TiData();
        tiData.setMixData(list)
                .setImages(images);
        OperationData operationData = new OperationData();
        operationData.setOperationName("110")
                .setPartName("2049件号");
        tiData.setBasic(BeanUtils.describe(operationData));
        TiCreator tiCreator = new TiCreator();
        tiCreator.createPDF(tiTemplate, tiData);
    }

    @SneakyThrows
    @Test
    public void createLoopPdf() {
        String templatePath = "D:\\processCardPreviewForder\\110\\Template\\检验图表3.pdf";
        LoopCreator creator = new LoopCreator();
        LoopTemplate template = new LoopTemplate();
        template.setMaxIndex(17)
                .setPath(templatePath)
                .setOutPath("D:\\temp\\pdf\\merge.pdf");
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            OperationLoopData loopData = new OperationLoopData();
            loopData.setNumber(String.valueOf(i));
            loopData.setContent("苹果iPhone15Pro设计图曝光" + i);
            Map<String, String> map = BeanUtils.describe(loopData);
            list.add(map);
        }
        Map<String, String> map = new HashMap<>();
        map.put("partName", "2049件号");
        LoopData loopData = new LoopData();
        loopData.setBasic(map)
                .setList(list);
        creator.createPDF(template, loopData);
    }

    @SneakyThrows
    @Test
    public void createTextPdf() {
        String templatePath = "D:\\processCardPreviewForder\\110\\Template\\首页.pdf";
        TextCreator creator = new TextCreator();
        TextTemplate template = new TextTemplate();
        template.setFormFlattening(true).setPath(templatePath)
                .setOutPath("D:\\temp\\pdf\\merge.pdf");
        FirstPageData data = new FirstPageData();
        data.setDepart("机匣厂");
        data.setProcessId("8888");
        data.setSecretLevel("内部");
        data.setVersion("V5");
        data.setImageVersion("V5_21");
        data.setProcessName("通用加工工艺");
        data.setRemark("这是一本测试工艺");
        LocalDate date = LocalDate.now();
        data.setYear(String.valueOf(date.getYear()));
        data.setMonth(String.valueOf(date.getMonthValue()));
        data.setDay(String.valueOf(date.getDayOfMonth()));
        Map<String, String> map = BeanUtils.describe(data);
        PageHolder holder = creator.createPdf(template, map);
        System.out.println(holder);
    }

    @SneakyThrows
    @Test
    public void pagePdf() {
        PageTemplate template = new PageTemplate();
        template.setFormFlattening(true);
        template.setOutPath("D:\\temp\\pdf\\merge1.pdf");
        List<PageHolder> holders = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            PageHolder holder = new PageHolder(String.format("D:\\temp\\pdf\\page%s.pdf", (i + 1)));
            Rectangle total = new Rectangle(429.179f, 117.083f, 453.726f, 134.107f);
            Rectangle number = new Rectangle(623.658f, 117.038f, 666.328f, 133.919f);
            holder.setTotalPageRectangle(total);
            holder.setNumberOfPageRectangle(number);
            holders.add(holder);
        }
        PageCreator creator = new PageCreator();
        creator.addPageNumber(template, holders);
    }


}
