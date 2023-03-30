package com.glaway;

import lombok.SneakyThrows;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageTest {

    @SneakyThrows
    @Test
    public void createTable(){
        int width = 400;
        int height = 200;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();

        // 设置抗锯齿
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 填充背景色
        g2.setColor(Color.BLUE);
        g2.fillRect(0, 0, width, height);

        // 设置表格起始位置和行列数
        int startX = 10;
        int startY = 10;
        int numRows = 3;
        int numCols = 2;

        // 设置表格尺寸
        int tableWidth = width - 2 * startX;
        int tableHeight = height - 2 * startY;
        int cellWidth = tableWidth / numCols;
        int cellHeight = tableHeight / numRows;

        // 设置表格线条宽度
        g2.setStroke(new BasicStroke(1));
        g2.setColor(Color.BLACK);
        // 绘制表格行线
        for (int i = 0; i <= numRows; i++) {
            int y = startY + i * cellHeight;
            g2.drawLine(startX, y, startX + tableWidth, y);
        }

        // 绘制表格列线
        for (int j = 0; j <= numCols; j++) {
            int x = startX + j * cellWidth;
            g2.drawLine(x, startY, x, startY + tableHeight);
        }

        // 设置表格文字字体
        g2.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        g2.setColor(Color.BLACK);

        // 填写表格内容
        String[][] tableData = {
                {"姓名", "年龄"},
                {"张三", "20"},
                {"李四", "22"},
                {"王五", "25"}
        };

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                String text = tableData[i][j];
                int x = startX + j * cellWidth;
                int y = startY + i * cellHeight + cellHeight / 2 + g2.getFontMetrics().getAscent() / 2;
                g2.drawString(text, x + (cellWidth - g2.getFontMetrics().stringWidth(text)) / 2, y);
            }
        }

        // 保存为图片文件
        ImageIO.write(image, "png", new File("D:\\temp\\table.png"));
    }



}
