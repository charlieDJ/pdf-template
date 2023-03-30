package com.dj.pdf.holder;

import com.itextpdf.text.Rectangle;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * PDF页码数据，页码位置，PDF路径
 */
@Data
@Accessors(chain = true)
public class PageHolder {
    /**
     * 总页数坐标
     */
    private Rectangle totalPageRectangle;
    /**
     * 当前页数坐标
     */
    private Rectangle numberOfPageRectangle;
    /**
     * pdf文件路径
     */
    private String pdfPath;
    /**
     * 页码
     */
    private int pageSize = 1;

    public PageHolder() {
    }

    public PageHolder(String pdfPath) {
        this.pdfPath = pdfPath;
    }
}
