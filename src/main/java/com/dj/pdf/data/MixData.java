package com.dj.pdf.data;

import lombok.Data;
import lombok.experimental.Accessors;
import org.jsoup.nodes.Document;

import java.util.List;

/**
 * 表格和文本混排数据
 */
@Data
@Accessors(chain = true)
public class MixData {
    /**
     * 数据类型，1：文本，2：表格，3：html
     */
    private int type;
    /**
     * 文本
     */
    private List<String> texts;
    /**
     * 表格
     */
    private Table table;
    /**
     * html文本
     */
    private Document document;





}
