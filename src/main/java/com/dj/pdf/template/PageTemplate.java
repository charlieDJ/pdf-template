package com.dj.pdf.template;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 页码模板
 */
@Data
@Accessors(chain = true)
public class PageTemplate extends Template {
    /**
     * 当前页数域名称
     */
    private String numberOfPage;
    /**
     * 总页数域名称
     */
    private String totalPage;
    /**
     * 页码字体大小
     */
    private int fontSize = 12;

}
