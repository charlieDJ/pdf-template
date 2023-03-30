package com.itext;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author dj
 * @date 2023/3/27
 */
public class HtmlTest {


    @SneakyThrows
    @Test
    public void html2Pdf(){
        String html = "D:\\processCardPreviewForder\\110\\Template\\新文件 19.html";
        String htmlStr = String.join(" ", Files.readAllLines(Paths.get(html)));
        Document document = new Document(PageSize.A4);
        String outPath = "D:/temp/html.pdf";
        //解析器
        PdfWriter writer = PdfWriter.getInstance(document, Files.newOutputStream(Paths.get(outPath)));
        /*页边距*/
        document.setMargins(2, 2, 2, 2);
        // 将字体文件添加到 PDF 文档中
        //打开document
        document.open();
        BaseFont font = BaseFont.createFont("\\font\\华文宋体.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font1 = new Font(font, 12, Font.NORMAL);
        org.jsoup.nodes.Document parse = Jsoup.parse(new File(html), "UTF-8");
        Elements elements = parse.select("p");
        // 遍历所有的p标签
        for (org.jsoup.nodes.Element element : elements) {
            // 创建段落,设置字体
            Paragraph paragraph = new Paragraph("", font1);
            List<Node> nodes = element.childNodes();
            for (Node node : nodes) {
                // 文本
                if(node instanceof TextNode){
                    paragraph.add(node.toString());
                    continue;
                }
                // 图片
                if(node instanceof org.jsoup.nodes.Element){
                    org.jsoup.nodes.Element child = (org.jsoup.nodes.Element) node;
                    if(child.tagName().equals("img")){
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
            document.add(paragraph);
        }
        document.close();


    }

}
