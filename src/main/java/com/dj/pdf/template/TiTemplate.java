package com.dj.pdf.template;


import lombok.Data;
import lombok.experimental.Accessors;
/**
 * 图片和文本混排模板
 */
@Data
@Accessors(chain = true)
public class TiTemplate extends Template {

    /**
     * 文本域名称
     */
    private String textField;
    /**
     * 图片域名称
     */
    private String imageField;

}
