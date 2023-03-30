package com.dj.pdf.data;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OperationBasicData extends Basic {
    private String partName;

}
