package com.glaway.pdf.template;

import lombok.Data;
import lombok.experimental.Accessors;
/**
 * 模板
 */
@Data
@Accessors(chain = true)
public class Template {
    /**
     * 模板的路径
     */
    private String path;
    /**
     * PDF文件的输出路径
     */
    private String outPath;

    /**
     * 如果为false那么生成的PDF文件还能编辑
     */
    private boolean formFlattening = true;

    /**
     * 当前页数域名称
     */
    private String numberOfPage = "numberOfPage";
    /**
     * 总页数域名称
     */
    private String totalPage = "totalPage";

}
