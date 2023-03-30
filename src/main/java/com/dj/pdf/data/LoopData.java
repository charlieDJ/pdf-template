package com.dj.pdf.data;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * 循环数据
 */
@Data
@Accessors(chain = true)
public class LoopData {
    /**
     * 循环数据
     */
    private List<Map<String, String>> list;
    /**
     * 基础数据
     */
    private Map<String, String> basic;
}
