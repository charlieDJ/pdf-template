package com.dj.pdf.data;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OperationLoopData {
    private String number;
    private String content;
}
