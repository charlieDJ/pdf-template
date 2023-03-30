package com.glaway.pdf.data;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * 表格类，用于表示一个简单的二维表格
 */
@Data
@Accessors(chain = true)
public class Table {
    /**
     * 表格描述
     */
    private String desc;

    private List<List<String>> data; // 声明表示表格的变量
    private int numColumns; // 表格的列数

    public Table() { // 构造函数
        data = new ArrayList<>();
        numColumns = 0;
    }

    public void addRow(List<String> row) { // 添加一行
        if (data.size() == 0) {
            numColumns = row.size();
        } else if (row.size() != numColumns) {
            throw new IllegalArgumentException("Row size does not match column size");
        }
        data.add(row);
    }

    public void addCell(int rowIndex, String cellValue) { // 在指定行添加一个单元格
        List<String> row = data.get(rowIndex);
        row.add(cellValue);
        if (row.size() != numColumns) {
            numColumns = row.size();
            for (int i = 0; i < data.size(); i++) {
                List<String> currentRow = data.get(i);
                while (currentRow.size() < numColumns) {
                    currentRow.add("");
                }
            }
        }
    }

    public void removeRow(int rowIndex) { // 删除一行
        data.remove(rowIndex);
        if (data.size() == 0) {
            numColumns = 0;
        }
    }

    public void removeCell(int rowIndex, int cellIndex) { // 删除一个单元格
        List<String> row = data.get(rowIndex);
        row.remove(cellIndex);
        if (row.size() < numColumns) {
            numColumns = row.size();
            for (int i = 0; i < data.size(); i++) {
                List<String> currentRow = data.get(i);
                while (currentRow.size() > numColumns) {
                    currentRow.remove(currentRow.size() - 1);
                }
            }
        }
    }

    public int getNumColumns() { // 获取表格的列数
        if (numColumns == 0 && data.size() != 0) {
            numColumns = data.get(0).size();
        }
        return numColumns;
    }

    public void display() { // 显示表格
        for (List<String> row : data) {
            for (String cellValue : row) {
                System.out.print(cellValue + " ");
            }
            System.out.println();
        }
    }
}
