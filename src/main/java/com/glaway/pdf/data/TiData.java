package com.glaway.pdf.data;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * 图片和文本混排数据
 */
@Data
@Accessors(chain = true)
public class TiData {
    /**
     * 图片路径
     */
    private List<String> images;
    /**
     * 文字和表格混排
     */
    private List<MixData> mixData;
    /**
     * 基础数据
     */
    private Map<String, String> basic;
}
