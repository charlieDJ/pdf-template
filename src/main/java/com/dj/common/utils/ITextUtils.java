package com.dj.common.utils;

import com.dj.pdf.data.MixData;
import com.dj.pdf.data.Table;
import com.dj.pdf.holder.PageHolder;
import com.dj.pdf.template.Template;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.codec.Base64;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;

/**
 * Itext 工具类
 */
public class ITextUtils {


    /**
     * 获取AcroFields中指定字段（field）的宽度和高度值
     *
     * @param fields AcroFields对象
     * @param field  字段名称
     * @return 包含字段宽度和高度两个值的列表
     */
    public static List<Float> getFiledWidthAndHeight(AcroFields fields, String field) {
        // 创建一个包含字段宽度和高度的列表
        List<Float> widthAndHeight = new ArrayList<>();

        // 向列表中添加字段宽度和高度两个值
        widthAndHeight.add(fieldWidth(fields, field));
        widthAndHeight.add(fieldHeight(fields, field));

        // 返回列表
        return widthAndHeight;
    }


    /**
     * 获取指定PDF文件中指定AcroFields对象的字段宽度和高度
     *
     * @param path  待查询PDF文件路径
     * @param field 字段名称
     * @return 包含字段宽度和高度两个值的列表
     */
    public static List<Float> getWidthAndHeight(String path, String field) {
        // 创建一个包含字段宽度和高度的列表对象
        List<Float> widthAndHeight;

        try {
            // 创建PdfReader对象
            PdfReader reader = new PdfReader(path);
            // 获取AcroFields对象并调用getFiledWidthAndHeight()方法获取字段宽度和高度
            AcroFields s = reader.getAcroFields();
            widthAndHeight = getFiledWidthAndHeight(s, field);
        } catch (IOException e) {
            // 当异常发生时，打印出错误信息并抛出一个新的RuntimeException异常
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

        // 返回包含字段宽度和高度的列表
        return widthAndHeight;
    }

    /**
     * 生成PDF文件
     *
     * @param outPath        输出路径
     * @param mixDataList    混排数据
     * @param widthAndHeight 宽高
     */
    public static void createComplexPdf(String outPath, List<MixData> mixDataList, List<Float> widthAndHeight) {
        try {
            //new一个Document对象,设置纸张大小
            Rectangle rectangle = new Rectangle(widthAndHeight.get(0), widthAndHeight.get(1));
            Document document = new Document(rectangle);
            //解析器
            PdfWriter.getInstance(document, Files.newOutputStream(Paths.get(outPath)));
            /*页边距*/
            document.setMargins(2, 2, 2, 2);
            // 将字体文件添加到 PDF 文档中
            //打开document
            document.open();
            // 中文字体
            BaseFont font = BaseFont.createFont("\\font\\华文宋体.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font cFont = new Font(font, 10, Font.NORMAL, BaseColor.BLACK);
            for (MixData mixData : mixDataList) {
                int type = mixData.getType();
                if (type == 1) {
                    List<String> texts = mixData.getTexts();
                    for (String text : texts) {
                        document.add(new Paragraph(text, cFont));
                    }
                } else if (type == 2) {
                    Table dataTable = mixData.getTable();
                    document.add(new Paragraph(dataTable.getDesc(), cFont));
                    List<List<String>> tableData = dataTable.getData();
                    int numColumns = dataTable.getNumColumns();
                    // 创建表格
                    PdfPTable table = new PdfPTable(numColumns);
                    // 设置表格宽度
                    table.setTotalWidth(widthAndHeight.get(0) - 5);
                    // 设置表格宽度固定
                    table.setLockedWidth(true);
                    // 设置表格上下间距
                    table.setSpacingAfter(5);
                    table.setSpacingBefore(5);
                    for (List<String> row : tableData) {
                        for (String s : row) {
                            table.addCell(new PdfPCell(new Paragraph(s, cFont)));
                        }
                    }
                    // 设置表格头部
                    table.setHeaderRows(1);
                    // 添加表格到 PDF
                    document.add(table);
                } else if (type == 3) {
                    addRichText2Para(document, mixData.getDocument(), cFont);
                }
            }
            document.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 添加富文本到段落
     *
     * @param document 文档
     * @param html     html文档
     * @param font     字体
     */
    private static void addRichText2Para(Document document, org.jsoup.nodes.Document html,
                                         Font font) {
        Elements elements = html.select("p");
        // 遍历所有的p标签
        for (org.jsoup.nodes.Element element : elements) {
            // 创建段落,设置字体
            Paragraph paragraph = new Paragraph("", font);
            List<Node> nodes = element.childNodes();
            for (Node node : nodes) {
                // 文本
                if (node instanceof TextNode) {
                    paragraph.add(node.toString());
                    continue;
                }
                // 图片
                if (node instanceof org.jsoup.nodes.Element) {
                    org.jsoup.nodes.Element child = (org.jsoup.nodes.Element) node;
                    if (child.tagName().equals("img")) {
                        String base64 = child.attr("src").split(",")[1];
                        try {
                            Image image = Image.getInstance(Base64.decode(base64));
                            Chunk chunk = new Chunk(image, 2, -3, true);
                            paragraph.add(chunk);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            try {
                document.add(paragraph);
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 在PDF文件中指定位置插入一张图片
     *
     * @param stamper   PdfStamper对象
     * @param fieldName 字段名称
     * @param imagePath 图片路径
     */
    public static void placeImage(PdfStamper stamper, String fieldName, String imagePath) {
        try {
            // 获取AcroFields对象
            AcroFields fields = stamper.getAcroFields();
            // 获取指定字段的AcroFields.Item对象
            AcroFields.Item item = fields.getFieldItem(fieldName);
            // 如果AcroFields.Item对象不存在，则返回
            if (Objects.isNull(item)) {
                return;
            }
            // 获取字段所在页面的页码
            int pageNo = fields.getFieldPositions(fieldName).get(0).page;
            // 获取字段在页面中的位置
            Rectangle signRect = fields.getFieldPositions(fieldName).get(0).position;
            float x = signRect.getLeft();
            float y = signRect.getBottom();
            // 创建一个Image对象
            Image image = Image.getInstance(imagePath);
            /*获取操作的页面*/
            // 获取操作页面的PdfContentByte对象
            PdfContentByte overContent = stamper.getOverContent(pageNo);
            /*根据域的大小缩放图片*/
            // 根据字段所在位置的大小，对图片进行缩放
            image.scaleToFit(signRect.getWidth(), signRect.getHeight());
            /*添加图片*/
            // 将图片添加到文档的指定位置
            image.setAbsolutePosition(x, y);
            overContent.addImage(image);
        } catch (IOException | DocumentException e) {
            // 如果在操作过程中出现异常，则打印错误信息
            e.printStackTrace();
        }
    }

    /**
     * 设置一个PageHolder对象的页面数和总页面数字段矩形区域
     *
     * @param s        AcroFields对象
     * @param template 模板对象
     * @param holder   需要设置的PageHolder对象
     * @return 已设置好页面数和总页面数字段矩形区域的PageHolder对象
     */
    public static PageHolder setPageRectangle(AcroFields s, Template template, PageHolder holder) {
        // 如果尚未设置页面数字段矩形区域，则使用getFieldRectangle()方法进行设置
        if (Objects.isNull(holder.getNumberOfPageRectangle())) {
            holder.setNumberOfPageRectangle(getFieldRectangle(template.getNumberOfPage(), s));
        }
        // 如果尚未设置总页面数字段矩形区域，则使用getFieldRectangle()方法进行设置
        if (Objects.isNull(holder.getTotalPageRectangle())) {
            holder.setTotalPageRectangle(getFieldRectangle(template.getTotalPage(), s));
        }
        // 返回已设置好页面数和总页面数字段矩形区域的PageHolder对象
        return holder;
    }

    /**
     * 获取指定字段的矩形区域
     *
     * @param fieldName 字段名称
     * @param fields    AcroFields对象
     * @return 字段的矩形区域
     */
    public static Rectangle getFieldRectangle(String fieldName, AcroFields fields) {
        AcroFields.Item item = fields.getFieldItem(fieldName);
        if (Objects.isNull(item)) {
            return null;
        }
        return fields.getFieldPositions(fieldName).get(0).position;
    }

    /**
     * 获取指定字段的宽度
     *
     * @param acroFields AcroFields对象
     * @param field      字段名称
     * @return 字段的宽度
     */
    public static float fieldWidth(AcroFields acroFields, String field) {
        Rectangle position = acroFields.getFieldPositions(field).get(0).position;
        return position.getWidth();
    }

    /**
     * 获取指定字段的高度
     *
     * @param acroFields AcroFields对象
     * @param field      字段名称
     * @return 字段的高度
     */
    public static float fieldHeight(AcroFields acroFields, String field) {
        Rectangle position = acroFields.getFieldPositions(field).get(0).position;
        return position.getHeight();
    }

    /**
     * 合并PDF文件
     *
     * @param paths   待合并的PDF文件路径
     * @param outPath 合并后的PDF文件路径
     */
    public static void merge(List<String> paths, String outPath) {
        Document document = new Document();
        try (FileOutputStream fos = new FileOutputStream(outPath)) {
            PdfCopy copy = new PdfCopy(document, fos);
            // 打开文档准备写入内容
            document.open();
            for (String path : paths) {
                PdfReader reader = new PdfReader(Files.newInputStream(new File(path).toPath()));
                // 获取页数
                int numberOfPages = reader.getNumberOfPages();
                // pdf的所有页, 从第1页开始遍历, 这里要注意不是0
                for (int i = 1; i <= numberOfPages; i++) {
                    // 把第 i 页读取出来
                    PdfImportedPage page = copy.getImportedPage(reader, i);
                    document.newPage();
                    // 把读取出来的页追加进输出文件里
                    copy.addPage(page);
                }
            }
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 合并PDF文件, 并设置每个PDF文件的页数和总页数
     *
     * @param holders 待合并的PDF文件路径
     * @param outPath 合并后的PDF文件路径
     * @return 合并后的PDF文件的页数和总页数
     */
    public static Map<Integer, PageHolder> mergeWithPage(List<PageHolder> holders, String outPath) {
        Map<Integer, PageHolder> map = new HashMap<>();
        Document document = new Document();
        try (FileOutputStream fos = new FileOutputStream(outPath)) {
            PdfCopy copy = new PdfCopy(document, fos);
            // 打开文档准备写入内容
            document.open();
            int numberOfPage = 1;
            for (PageHolder holder : holders) {
                String path = holder.getPdfPath();
                PdfReader reader = new PdfReader(Files.newInputStream(new File(path).toPath()));
                // 获取页数
                int numberOfPages = reader.getNumberOfPages();
                // pdf的所有页, 从第1页开始遍历, 这里要注意不是0
                for (int i = 1; i <= numberOfPages; i++) {
                    // 把第 i 页读取出来
                    PdfImportedPage page = copy.getImportedPage(reader, i);
                    document.newPage();
                    // 把读取出来的页追加进输出文件里
                    copy.addPage(page);
                    // 保存页数和页码的关系
                    map.put(numberOfPage, holder);
                    numberOfPage++;
                }
            }
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return map;
    }

    /**
     * 获取中文字体
     *
     * @return 中文字体
     */
    public static BaseFont getChineseFont() {
        try {
            return BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("未找到字体，请检查程序");
        }
    }

    /**
     * 获取指定字段的字体大小
     *
     * @param fields AcroFields对象
     * @param field  字段名
     * @return 字体大小值
     */
    public static float getFontSize(AcroFields fields, String field) {
        // 获取指定字段的字段项
        AcroFields.Item fieldItem = fields.getFieldItem(field);
        if (Objects.isNull(fieldItem)) {
            return 0f;
        }
        // 获取字段项的合并字典
        PdfDictionary fieldDictionary = fieldItem.getMerged(0);
        // 获取字段字典中的默认表现字符串
        PdfString defaultAppearance = fieldDictionary.getAsString(PdfName.DA);
        // 如果默认表现字符串为空，则返回0
        return extractFontSize(defaultAppearance.toString());
    }

    /**
     * 从默认表现字符串中提取字体大小，以浮点数的形式返回
     *
     * @param defaultAppearance 默认表现字符串
     * @return 字体大小值
     */
    private static float extractFontSize(String defaultAppearance) {
        // 将默认表现字符串按空格分割为一个字符串数组
        String[] values = defaultAppearance.split("\\s+");
        // 遍历字符串数组
        for (String value : values) {
            // 如果字符串以 "g" 结尾，那么这是一个字形名称，忽略它
            if (value.endsWith("g")) {
                continue;
            }
            try {
                // 尝试将当前字符串值解析为浮点数并返回
                return Float.parseFloat(value);
            } catch (NumberFormatException e) {
                // 如果字符串不能转换为浮点数，则忽略它
            }
        }
        // 如果没有找到字体大小，返回 0
        return 0;
    }

    /**
     * 计算给定字体、字号和文本的宽度
     *
     * @param fontSize 字号
     * @param text     文本
     * @param font     字体
     * @return 文本的宽度（以点为单位）
     */
    public static float getTextWidth(float fontSize, String text, BaseFont font) {
        // 通过 BaseFont 的 getWidthPoint 方法来获取文本在给定字体和字号下的宽度
        return font.getWidthPoint(text, fontSize);
    }

    /**
     * 将对象中的属性值填充到 AcroFields 中对应的字段中
     *
     * @param s   AcroFields 对象
     * @param map   对象
     */
    public static void populateFields(AcroFields s, Map<String,String> map) {
        try {
            // 获取对象 t 中的所有字段列表
            for (String key : map.keySet()) {
                // 获取 AcroFields 中同名字段对应的 Item 对象
                AcroFields.Item fieldItem = s.getFieldItem(key);
                // 如果字段不存在，则跳过该字段
                if (Objects.isNull(fieldItem)) {
                    continue;
                }
                String value = map.get(key);
                // 将字段值填入到对应的 AcroFields 字段中
                s.setField(key, value);
            }
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            // 如果捕获到异常，则将其包装为 RuntimeException，并抛出
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 关闭 PdfStamper 和 PdfReader
     *
     * @param stamper PdfStamper 对象
     * @param reader  PdfReader 对象
     */
    public static void close(PdfStamper stamper, PdfReader reader) {
        try {
            // 如果 PdfStamper 和 PdfReader 都不为空，则关闭它们
            if (Objects.nonNull(stamper)) {
                stamper.close();
            }
            if (Objects.nonNull(reader)) {
                reader.close();
            }
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }
}
