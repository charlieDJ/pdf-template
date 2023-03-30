package com.glaway.pdf.data;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OperationData  {
    private String partName;
    private String operationName;
}
