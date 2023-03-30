package com.glaway.pdf.template;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 循环模板
 */
@Data
@Accessors(chain = true)
public class LoopTemplate extends Template {
    /**
     * 循环数据最大索引编号
     */
    private Integer maxIndex;
    /**
     * 循环数据分割符，用于分割编号和索引
     * 默认无
     */
    private String separator = "";
}
