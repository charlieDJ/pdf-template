package com.pdfbox;

import com.dj.common.utils.PdfBoxUtils;
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class PdfBoxTest {

    @SneakyThrows
    @Test
    public void pdf2Image() {
        String out = "D:\\temp\\text.jpg";
        PDDocument pd = PDDocument.load(new File("D:\\temp\\text.pdf"));
        PDFRenderer pr = new PDFRenderer(pd);
        BufferedImage bi = pr.renderImageWithDPI(0, 300);
        ImageIO.write(bi, "JPEG", new File(out));
    }

    @SneakyThrows
    @Test
    public void pdf2Image2() {
        String outDir = "D:\\temp\\jpg";
        String pdfPath = "D:\\temp\\text.pdf";
        List<String> list = PdfBoxUtils.pdf2Image(pdfPath, outDir);
        for (String s : list) {
            System.out.println("图片地址：" + s);
        }
    }

}
